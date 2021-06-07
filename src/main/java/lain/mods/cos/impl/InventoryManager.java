package lain.mods.cos.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lain.mods.cos.api.event.CosArmorDeathDrops;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.packet.PacketSyncCosArmor;
import lain.mods.cos.impl.network.packet.PacketSyncHiddenFlags;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;

public class InventoryManager {

    protected static final InventoryCosArmor Dummy = new InventoryCosArmor() {

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        @Nonnull
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public boolean isHidden(String modid, String identifier) {
            return false;
        }

        @Override
        public boolean isSkinArmor(int slot) {
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
        }

        @Override
        protected void onLoad() {
        }

        @Override
        public boolean setHidden(String modid, String identifier, boolean set) {
            return false;
        }

        @Override
        public void setSkinArmor(int slot, boolean enabled) {
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        }

        @Override
        public boolean setUpdateListener(ContentsChangeListener listener) {
            return false;
        }

        @Override
        public boolean setUpdateListener(HiddenFlagsChangeListener listener) {
            return false;
        }

    };

    protected static final Random RANDOM = new Random();

    protected final LoadingCache<UUID, InventoryCosArmor> CommonCache = CacheBuilder.newBuilder().build(new CacheLoader<UUID, InventoryCosArmor>() {

        @Override
        public InventoryCosArmor load(UUID key) throws Exception {
            InventoryCosArmor inventory = new InventoryCosArmor();
            inventory.setUpdateListener((inv, slot) -> onInventoryChanged(key, inv, slot));
            inventory.setUpdateListener((inv, modid, identifier) -> onHiddenFlagsChanged(key, inv, modid, identifier));
            loadInventory(key, inventory);
            return inventory;
        }

    });

    public static boolean checkIdentifier(String modid, String identifier) {
        if (modid == null || modid.isEmpty() || identifier == null || identifier.isEmpty() || !ModList.get().isLoaded(modid))
            return false;

        return false;
    }

    public ContainerCosArmor createContainerClient(int windowId, PlayerInventory invPlayer, PacketBuffer extraData) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public InventoryCosArmor getCosArmorInventory(UUID uuid) {
        if (uuid == null)
            return Dummy;
        return CommonCache.getUnchecked(uuid);
    }

    @Nonnull
    public InventoryCosArmor getCosArmorInventoryClient(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    protected File getDataFile(UUID uuid) {
        return new File(LogicalSidedProvider.INSTANCE.<MinecraftServer>get(LogicalSide.SERVER).playerDataStorage.getPlayerDataFolder(), uuid + ".cosarmor");
    }

    private void handlePlayerDrops(LivingDropsEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            if (event.getEntityLiving().isEffectiveAi() && !event.getEntityLiving().getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !ModConfigs.CosArmorKeepThroughDeath.get()) {
                InventoryCosArmor inv = getCosArmorInventory(event.getEntityLiving().getUUID());
                if (MinecraftForge.EVENT_BUS.post(new CosArmorDeathDrops((PlayerEntity) event.getEntityLiving(), inv)))
                    return;
                for (int i = 0; i < inv.getSlots(); i++) {
                    ItemStack stack = inv.getStackInSlot(i).copy();
                    if (stack.isEmpty())
                        continue;

                    float fX = RANDOM.nextFloat() * 0.75F + 0.125F;
                    float fY = RANDOM.nextFloat() * 0.75F;
                    float fZ = RANDOM.nextFloat() * 0.75F + 0.125F;
                    while (!stack.isEmpty()) {
                        ItemEntity entity = new ItemEntity(event.getEntityLiving().getCommandSenderWorld(), event.getEntityLiving().getX() + (double) fX, event.getEntityLiving().getY() + (double) fY, event.getEntityLiving().getZ() + (double) fZ, stack.split(RANDOM.nextInt(21) + 10));
                        entity.setDeltaMovement(RANDOM.nextGaussian() * (double) 0.05F, RANDOM.nextGaussian() * (double) 0.05F + (double) 0.2F, RANDOM.nextGaussian() * (double) 0.05F);
                        event.getDrops().add(entity);
                    }

                    inv.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }
    }

    private void handlePlayerLoggedIn(PlayerLoggedInEvent event) {
        CommonCache.invalidate(event.getPlayer().getUUID());
        getCosArmorInventory(event.getPlayer().getUUID());

        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            for (ServerPlayerEntity other : LogicalSidedProvider.INSTANCE.<MinecraftServer>get(LogicalSide.SERVER).getPlayerList().getPlayers()) {
                if (other == player)
                    continue;
                UUID uuid = other.getUUID();
                InventoryCosArmor inv = getCosArmorInventory(uuid);
                for (int i = 0; i < inv.getSlots(); i++)
                    ModObjects.network.sendTo(new PacketSyncCosArmor(uuid, inv, i), player);
                inv.forEachHidden((modid, identifier) -> ModObjects.network.sendTo(new PacketSyncHiddenFlags(uuid, inv, modid, identifier), player));
            }
        }
    }

    private void handlePlayerLoggedOut(PlayerLoggedOutEvent event) {
        UUID uuid;
        InventoryCosArmor inv;
        if ((inv = CommonCache.getIfPresent(uuid = event.getPlayer().getUUID())) != null) {
            saveInventory(uuid, inv);
            CommonCache.invalidate(uuid);
        }
    }

    private void handleRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("clearcosarmor").requires(s -> {
            return s.hasPermission(2);
        }).executes(s -> {
            int count = 0;
            ServerPlayerEntity player = s.getSource().getPlayerOrException();
            InventoryCosArmor inv = getCosArmorInventory(player.getUUID());
            for (int i = 0; i < inv.getSlots(); i++)
                count += inv.extractItem(i, Integer.MAX_VALUE, false).getCount();
            s.getSource().sendSuccess(new TranslationTextComponent("cos.command.clearcosarmor.success.single", count, player.getDisplayName()), true);
            return count;
        }).then(Commands.argument("targets", EntityArgument.players()).executes(s -> {
            int count = 0;
            Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(s, "targets");
            for (ServerPlayerEntity player : players) {
                InventoryCosArmor inv = getCosArmorInventory(player.getUUID());
                for (int i = 0; i < inv.getSlots(); i++)
                    count += inv.extractItem(i, Integer.MAX_VALUE, false).getCount();
            }
            if (players.size() == 1)
                s.getSource().sendSuccess(new TranslationTextComponent("cos.command.clearcosarmor.success.single", count, players.iterator().next().getDisplayName()), true);
            else
                s.getSource().sendSuccess(new TranslationTextComponent("cos.command.clearcosarmor.success.multiple", count, players.size()), true);
            return count;
        })));

        if (!ModConfigs.CosArmorDisableCosHatCommand.get()) {
            event.getDispatcher().register(Commands.literal("coshat").requires(s -> {
                return s.hasPermission(0);
            }).executes(s -> {
                ServerPlayerEntity player = s.getSource().getPlayerOrException();
                InventoryCosArmor inv = getCosArmorInventory(player.getUUID());
                ItemStack stack1 = player.getItemBySlot(EquipmentSlotType.MAINHAND);
                ItemStack stack2 = inv.getStackInSlot(3);
                player.setItemSlot(EquipmentSlotType.MAINHAND, stack2);
                inv.setStackInSlot(3, stack1);
                return 0;
            }));
        }
    }

    private void handleSaveToFile(PlayerEvent.SaveToFile event) {
        UUID uuid;
        InventoryCosArmor inv;
        if ((inv = CommonCache.getIfPresent(uuid = UUID.fromString(event.getPlayerUUID()))) != null)
            saveInventory(uuid, inv);
    }

    private void handleServerStopping(FMLServerStoppingEvent event) {
        ModObjects.logger.debug("Server is stopping... try to save all still loaded CosmeticArmor data");
        CommonCache.asMap().entrySet().forEach(e -> {
            ModObjects.logger.debug(e.getKey());
            saveInventory(e.getKey(), e.getValue());
        });
        CommonCache.invalidateAll();
    }

    protected void loadInventory(UUID uuid, InventoryCosArmor inventory) {
        if (inventory == Dummy)
            return;
        try {
            File file;
            if ((file = getDataFile(uuid)).exists())
                inventory.deserializeNBT(CompressedStreamTools.read(file));
        } catch (Throwable t) {
            ModObjects.logger.fatal("Failed to load CosmeticArmor data", t);
        }
    }

    protected void onHiddenFlagsChanged(UUID uuid, InventoryCosArmor inventory, String modid, String identifier) {
        ModObjects.network.sendToAll(new PacketSyncHiddenFlags(uuid, inventory, modid, identifier));
    }

    protected void onInventoryChanged(UUID uuid, InventoryCosArmor inventory, int slot) {
        ModObjects.network.sendToAll(new PacketSyncCosArmor(uuid, inventory, slot));
    }

    public void registerEvents() {
        MinecraftForge.EVENT_BUS.addListener(this::handlePlayerDrops);
        MinecraftForge.EVENT_BUS.addListener(this::handlePlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(this::handlePlayerLoggedOut);
        MinecraftForge.EVENT_BUS.addListener(this::handleSaveToFile);
        MinecraftForge.EVENT_BUS.addListener(this::handleRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(this::handleServerStopping);
    }

    public void registerEventsClient() {
        throw new UnsupportedOperationException();
    }

    protected void saveInventory(UUID uuid, InventoryCosArmor inventory) {
        if (inventory == Dummy)
            return;
        try {
            CompressedStreamTools.write(inventory.serializeNBT(), getDataFile(uuid));
        } catch (Throwable t) {
            ModObjects.logger.fatal("Failed to save CosmeticArmor data", t);
        }
    }

}
