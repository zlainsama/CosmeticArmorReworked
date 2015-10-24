package lain.mods.cos;

import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class InventoryManager
{

    public ItemStack[] getCosArmor(EntityPlayer player)
    {
        return null;
    }

    public ItemStack getCosArmorSlot(EntityPlayer player, int slot)
    {
        return null;
    }

    public boolean isSkinCosArmor(EntityPlayer player, int slot)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void setCosArmorClient(EntityPlayer player, int slot, boolean isSkinCosArmor, ItemStack itemCosArmor)
    {

    }
    
    public InventoryCosArmor getCosArmorInventory(EntityPlayer player)
    {
        return null;
    }

}
