package lain.mods.cos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lain.mods.cos.inventory.InventoryCosArmor;
import lain.mods.cos.network.packet.PacketSyncCosArmor;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class InventoryManager
{

    LoadingCache<UUID, InventoryCosArmor> cache = CacheBuilder.newBuilder().build(new CacheLoader<UUID, InventoryCosArmor>()
    {

        @Override
        public InventoryCosArmor load(UUID owner) throws Exception
        {
            InventoryCosArmor inv = new InventoryCosArmor();

            try
            {
                forceLoad(owner, inv);
            }
            catch (IOException e)
            {
                System.err.println("Error loading CosmeticArmor data file: " + e.getMessage());
                e.printStackTrace();
                inv = new InventoryCosArmor();
            }

            inv.markDirty();

            return inv;
        }

    });

    void forceLoad(UUID uuid, InventoryCosArmor inv) throws IOException
    {
        try
        {
            inv.readFromNBT(CompressedStreamTools.readCompressed(new FileInputStream(getDataFile(uuid))));
        }
        catch (FileNotFoundException ignored)
        {
        }
    }

    void forceSave(UUID uuid, InventoryCosArmor inv) throws IOException
    {
        NBTTagCompound compound = new NBTTagCompound();
        inv.writeToNBT(compound);
        CompressedStreamTools.writeCompressed(compound, new FileOutputStream(getDataFile(uuid)));
    }

    public InventoryCosArmor getCosArmorInventory(UUID uuid)
    {
        return cache.getUnchecked(uuid);
    }

    public InventoryCosArmor getCosArmorInventoryClient(UUID uuid)
    {
        throw new UnsupportedOperationException();
    }

    File getDataFile(UUID uuid)
    {
        return new File(new File(getSavesDirectory(), "playerdata"), uuid + ".cosarmor");
    }

    File getSavesDirectory()
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (FMLCommonHandler.instance().getSide().isClient())
            return new File(server.getFile("saves"), server.worldServerForDimension(0).getSaveHandler().getWorldDirectoryName());
        return server.getFile(server.getFolderName());
    }

    @SubscribeEvent
    public void handleEvent(PlayerDropsEvent event)
    {
        if (event.entityPlayer instanceof EntityPlayerMP && !event.entityPlayer.worldObj.isRemote && !event.entityPlayer.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
        {
            InventoryCosArmor inv = getCosArmorInventory(event.entityPlayer.getUniqueID());
            for (int i = 0; i < inv.getSizeInventory(); i++)
            {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack != null)
                {
                    EntityItem ent = new EntityItem(event.entityPlayer.worldObj, event.entityPlayer.posX, event.entityPlayer.posY + event.entityPlayer.getEyeHeight(), event.entityPlayer.posZ, stack.copy());
                    ent.delayBeforeCanPickup = 40;
                    float f1 = event.entityPlayer.worldObj.rand.nextFloat() * 0.5F;
                    float f2 = event.entityPlayer.worldObj.rand.nextFloat() * (float) Math.PI * 2.0F;
                    ent.motionX = (double) (-MathHelper.sin(f2) * f1);
                    ent.motionZ = (double) (MathHelper.cos(f2) * f1);
                    ent.motionY = 0.20000000298023224D;
                    event.drops.add(ent);
                    inv.setInventorySlotContents(i, null);
                    inv.markDirty();
                }
            }
        }

    }

    @SubscribeEvent
    public void handleEvent(PlayerEvent.LoadFromFile event)
    {
        UUID uuid = UUID.fromString(event.playerUUID);
        InventoryCosArmor inv = getCosArmorInventory(uuid);

        try
        {
            inv.readFromNBT(CompressedStreamTools.readCompressed(new FileInputStream(getDataFile(uuid))));
        }
        catch (FileNotFoundException ignored)
        {
        }
        catch (IOException e)
        {
            System.err.println("Error loading CosmeticArmor data file: " + e.getMessage());
            e.printStackTrace();
            cache.refresh(uuid);
            inv = getCosArmorInventory(uuid);
        }

        inv.markDirty();
    }

    @SubscribeEvent
    public void handleEvent(PlayerEvent.SaveToFile event)
    {
        UUID uuid = UUID.fromString(event.playerUUID);
        InventoryCosArmor inv = getCosArmorInventory(uuid);
        NBTTagCompound compound = new NBTTagCompound();
        inv.writeToNBT(compound);
        try
        {
            CompressedStreamTools.writeCompressed(compound, new FileOutputStream(getDataFile(uuid)));
        }
        catch (IOException e)
        {
            System.err.println("Error saving CosmeticArmor data file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void handleEvent(PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            InventoryCosArmor inv = getCosArmorInventory(event.player.getUniqueID());
            for (int i = 0; i < inv.getSizeInventory(); i++)
                CosmeticArmorReworked.network.sendToAll(new PacketSyncCosArmor(event.player, i));
            inv.markClean();

            for (EntityPlayerMP other : (List<EntityPlayerMP>) FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
            {
                if (other == event.player)
                    continue;
                inv = getCosArmorInventory(other.getUniqueID());
                for (int i = 0; i < inv.getSizeInventory(); i++)
                    CosmeticArmorReworked.network.sendTo(new PacketSyncCosArmor(other, i), (EntityPlayerMP) event.player);
            }
        }
    }

    @SubscribeEvent
    public void handleEvent(PlayerLoggedOutEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            UUID uuid = event.player.getUniqueID();
            try
            {
                forceSave(uuid, getCosArmorInventory(uuid));
            }
            catch (IOException e)
            {
                System.err.println("Error saving CosmeticArmor data file: " + e.getMessage());
                e.printStackTrace();
            }
            cache.invalidate(uuid);
        }
    }

    @SubscribeEvent
    public void handleEvent(PlayerTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            if (event.player instanceof EntityPlayerMP)
            {
                InventoryCosArmor inv = getCosArmorInventory(event.player.getUniqueID());
                if (inv.isDirty())
                {
                    for (int i = 0; i < inv.getSizeInventory(); i++)
                        CosmeticArmorReworked.network.sendToAll(new PacketSyncCosArmor(event.player, i));
                    inv.markClean();
                }
            }
        }
    }

    void onServerStarting()
    {
        cache.invalidateAll();
    }

    void onServerStopping()
    {
        System.out.println("Server is stopping... force saving all loaded CosmeticArmor data.");
        for (UUID uuid : cache.asMap().keySet())
        {
            System.out.println(uuid);
            try
            {
                forceSave(uuid, getCosArmorInventory(uuid));
            }
            catch (IOException e)
            {
                System.err.println("Error saving CosmeticArmor data file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
