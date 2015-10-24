package lain.mods.cos;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import lain.mods.cos.inventory.InventoryCosArmor;
import lain.mods.cos.network.packet.PacketSyncCosArmor;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
        public InventoryCosArmor load(UUID key) throws Exception
        {
            return new InventoryCosArmor();
        }

    });

    public ItemStack[] getCosArmor(EntityPlayer player)
    {
        return getCosArmorInventory(player).getInventory();
    }

    public InventoryCosArmor getCosArmorInventory(EntityPlayer player)
    {
        return cache.getUnchecked(player.getUniqueID());
    }

    public ItemStack getCosArmorSlot(EntityPlayer player, int slot)
    {
        return getCosArmorInventory(player).getStackInSlot(slot);
    }

    @SubscribeEvent
    public void handleEvent(PlayerDropsEvent event)
    {
        if (event.entityPlayer instanceof EntityPlayerMP && !event.entityPlayer.worldObj.isRemote && !event.entityPlayer.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
        {
            InventoryCosArmor inv = getCosArmorInventory(event.entityPlayer);
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
        InventoryCosArmor inv = cache.getUnchecked(uuid);

        try
        {
            inv.readFromNBT(CompressedStreamTools.readCompressed(new FileInputStream(event.getPlayerFile("cosarmor"))));
        }
        catch (FileNotFoundException ignored)
        {
        }
        catch (IOException e)
        {
            System.err.println("Error loading CosmeticArmor data file: " + e.getMessage());
            e.printStackTrace();
            cache.refresh(uuid);
            inv = cache.getUnchecked(uuid);
        }

        inv.markDirty();
    }

    @SubscribeEvent
    public void handleEvent(PlayerEvent.SaveToFile event)
    {
        UUID uuid = UUID.fromString(event.playerUUID);
        InventoryCosArmor inv = cache.getUnchecked(uuid);
        NBTTagCompound compound = new NBTTagCompound();
        inv.writeToNBT(compound);
        try
        {
            CompressedStreamTools.writeCompressed(compound, new FileOutputStream(event.getPlayerFile("cosarmor")));
        }
        catch (IOException e)
        {
            System.err.println("Error saving CosmeticArmor data file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void handleEvent(PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            InventoryCosArmor inv = getCosArmorInventory(event.player);
            for (int i = 0; i < inv.getSizeInventory(); i++)
                CosmeticArmorReworked.network.sendToAll(new PacketSyncCosArmor(event.player, i));
            inv.markClean();
        }
    }

    @SubscribeEvent
    public void handleEvent(PlayerLoggedOutEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            cache.invalidate(event.player.getUniqueID());
        }
    }

    @SubscribeEvent
    public void handleEvent(PlayerTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            if (event.player instanceof EntityPlayerMP)
            {
                InventoryCosArmor inv = getCosArmorInventory(event.player);
                if (inv.isDirty())
                {
                    for (int i = 0; i < inv.getSizeInventory(); i++)
                        CosmeticArmorReworked.network.sendToAll(new PacketSyncCosArmor(event.player, i));
                    inv.markClean();
                }
            }
        }
    }

    public boolean isSkinArmor(EntityPlayer player, int slot)
    {
        return getCosArmorInventory(player).isSkinArmor(slot);
    }

}
