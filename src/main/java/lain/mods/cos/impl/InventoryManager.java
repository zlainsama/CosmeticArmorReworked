package lain.mods.cos.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lain.mods.cos.api.event.CosArmorDeathDrops;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.packet.PacketSyncCosArmor;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

public class InventoryManager
{

    protected static final InventoryCosArmor Dummy = new InventoryCosArmor()
    {

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return ItemStack.EMPTY;
        }

        @Override
        @Nonnull
        public ItemStack getStackInSlot(int slot)
        {
            return ItemStack.EMPTY;
        }

        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            return stack;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
        }

        @Override
        protected void onLoad()
        {
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack)
        {
        }

        @Override
        public boolean setUpdateListener(BiConsumer<InventoryCosArmor, Integer> listener)
        {
            return false;
        }

    };

    protected static final Random RANDOM = new Random();

    protected final LoadingCache<UUID, InventoryCosArmor> CommonCache = CacheBuilder.newBuilder().build(new CacheLoader<UUID, InventoryCosArmor>()
    {

        @Override
        public InventoryCosArmor load(UUID key) throws Exception
        {
            InventoryCosArmor inventory = new InventoryCosArmor();
            inventory.setUpdateListener((i, s) -> onInventoryChanged(key, i, s));
            loadInventory(key, inventory);
            return inventory;
        }

    });

    @Nonnull
    public InventoryCosArmor getCosArmorInventory(UUID uuid)
    {
        if (uuid == null)
            return Dummy;
        return CommonCache.getUnchecked(uuid);
    }

    @Nonnull
    public InventoryCosArmor getCosArmorInventoryClient(UUID uuid)
    {
        throw new UnsupportedOperationException();
    }

    protected File getDataFile(UUID uuid)
    {
        File dir = new File(getSavesDirectory(), "playerdata");
        if (!dir.exists())
            dir.mkdirs();
        return new File(dir, uuid + ".cosarmor");
    }

    protected File getSavesDirectory()
    {
        try
        {
            return LogicalSidedProvider.INSTANCE.<MinecraftServer>get(LogicalSide.SERVER).getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory();
        }
        catch (Throwable t)
        {
            ModObjects.logger.fatal("Failed to get saves directory", t);
            return Paths.get(".", "SomethingWentWrongFolder").toFile();
        }
    }

    private void handlePlayerDrops(PlayerDropsEvent event)
    {
        if (event.getEntityPlayer().isServerWorld() && !event.getEntityPlayer().getEntityWorld().getGameRules().getBoolean("keepInventory") && !ModConfigs.CosArmorKeepThroughDeath.get())
        {
            InventoryCosArmor inv = getCosArmorInventory(event.getEntityPlayer().getUniqueID());
            if (MinecraftForge.EVENT_BUS.post(new CosArmorDeathDrops(event.getEntityPlayer(), inv)))
                return;
            for (int i = 0; i < inv.getSlots(); i++)
            {
                ItemStack stack = inv.getStackInSlot(i).copy();
                if (stack.isEmpty())
                    continue;

                float fX = RANDOM.nextFloat() * 0.75F + 0.125F;
                float fY = RANDOM.nextFloat() * 0.75F;
                float fZ = RANDOM.nextFloat() * 0.75F + 0.125F;
                while (!stack.isEmpty())
                {
                    EntityItem entity = new EntityItem(event.getEntityPlayer().getEntityWorld(), event.getEntityPlayer().posX + (double) fX, event.getEntityPlayer().posY + (double) fY, event.getEntityPlayer().posZ + (double) fZ, stack.split(RANDOM.nextInt(21) + 10));
                    entity.motionX = RANDOM.nextGaussian() * (double) 0.05F;
                    entity.motionY = RANDOM.nextGaussian() * (double) 0.05F + (double) 0.2F;
                    entity.motionZ = RANDOM.nextGaussian() * (double) 0.05F;
                    event.getDrops().add(entity);
                }

                inv.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    private void handlePlayerLoggedIn(PlayerLoggedInEvent event)
    {
        CommonCache.invalidate(event.getPlayer().getUniqueID());
        getCosArmorInventory(event.getPlayer().getUniqueID());

        if (event.getPlayer() instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
            for (EntityPlayerMP other : LogicalSidedProvider.INSTANCE.<MinecraftServer>get(LogicalSide.SERVER).getPlayerList().getPlayers())
            {
                if (other == player)
                    continue;
                UUID uuid = other.getUniqueID();
                InventoryCosArmor inv = getCosArmorInventory(uuid);
                for (int i = 0; i < inv.getSlots(); i++)
                    ModObjects.network.sendTo(new PacketSyncCosArmor(uuid, inv, i), player);
            }
        }
    }

    private void handlePlayerLoggedOut(PlayerLoggedOutEvent event)
    {
        UUID uuid;
        InventoryCosArmor inv;
        if ((inv = CommonCache.getIfPresent(uuid = event.getPlayer().getUniqueID())) != null)
        {
            saveInventory(uuid, inv);
            CommonCache.invalidate(uuid);
        }
    }

    private void handleSaveToFile(PlayerEvent.SaveToFile event)
    {
        UUID uuid;
        InventoryCosArmor inv;
        if ((inv = CommonCache.getIfPresent(uuid = UUID.fromString(event.getPlayerUUID()))) != null)
            saveInventory(uuid, inv);
    }

    private void handleServerStarting(FMLServerStartingEvent event)
    {
        event.getCommandDispatcher().register(Commands.literal("clearcosarmor").requires(s -> {
            return s.hasPermissionLevel(2);
        }).executes(s -> {
            int count = 0;
            EntityPlayerMP player = s.getSource().asPlayer();
            InventoryCosArmor inv = getCosArmorInventory(player.getUniqueID());
            for (int i = 0; i < inv.getSlots(); i++)
                count += inv.extractItem(i, Integer.MAX_VALUE, false).getCount();
            s.getSource().sendFeedback(new TextComponentTranslation("cos.command.clearcosarmor.success.single", count, player.getDisplayName()), true);
            return count;
        }).then(Commands.argument("targets", EntityArgument.multiplePlayers())).executes(s -> {
            int count = 0;
            Collection<EntityPlayerMP> players = EntityArgument.getPlayers(s, "targets");
            for (EntityPlayerMP player : players)
            {
                InventoryCosArmor inv = getCosArmorInventory(player.getUniqueID());
                for (int i = 0; i < inv.getSlots(); i++)
                    count += inv.extractItem(i, Integer.MAX_VALUE, false).getCount();
            }
            if (players.size() == 1)
                s.getSource().sendFeedback(new TextComponentTranslation("cos.command.clearcosarmor.success.single", count, players.iterator().next().getDisplayName()), true);
            else
                s.getSource().sendFeedback(new TextComponentTranslation("cos.command.clearcosarmor.success.multiple", count, players.size()), true);
            return count;
        }));
    }

    private void handleServerStopping(FMLServerStoppingEvent event)
    {
        ModObjects.logger.info("Server is stopping... try to save all still loaded CosmeticArmor data");
        CommonCache.asMap().entrySet().forEach(e -> {
            ModObjects.logger.info(e.getKey());
            saveInventory(e.getKey(), e.getValue());
        });
        CommonCache.invalidateAll();
    }

    protected void loadInventory(UUID uuid, InventoryCosArmor inventory)
    {
        if (inventory == Dummy)
            return;
        try
        {
            inventory.deserializeNBT(CompressedStreamTools.read(getDataFile(uuid)));
        }
        catch (FileNotFoundException ignored)
        {
        }
        catch (Throwable t)
        {
            ModObjects.logger.fatal("Failed to load CosmeticArmor data", t);
        }
    }

    protected void onInventoryChanged(UUID uuid, InventoryCosArmor inventory, int slot)
    {
        ModObjects.network.sendToAll(new PacketSyncCosArmor(uuid, inventory, slot));
    }

    public void registerEvents()
    {
        MinecraftForge.EVENT_BUS.addListener(this::handlePlayerDrops);
        MinecraftForge.EVENT_BUS.addListener(this::handlePlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(this::handlePlayerLoggedOut);
        MinecraftForge.EVENT_BUS.addListener(this::handleSaveToFile);
        MinecraftForge.EVENT_BUS.addListener(this::handleServerStarting);
        MinecraftForge.EVENT_BUS.addListener(this::handleServerStopping);
    }

    public void registerEventsClient()
    {
        throw new UnsupportedOperationException();
    }

    protected void saveInventory(UUID uuid, InventoryCosArmor inventory)
    {
        if (inventory == Dummy)
            return;
        try
        {
            CompressedStreamTools.write(inventory.serializeNBT(), getDataFile(uuid));
        }
        catch (Throwable t)
        {
            ModObjects.logger.fatal("Failed to save CosmeticArmor data", t);
        }
    }

}
