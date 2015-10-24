package lain.mods.cos;

import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryManager
{

    public ItemStack[] getCosArmor(EntityPlayer player)
    {
        return getCosArmorInventory(player).getInventory();
    }

    public ItemStack getCosArmorSlot(EntityPlayer player, int slot)
    {
        return getCosArmorInventory(player).getStackInSlot(slot);
    }

    public boolean isSkinArmor(EntityPlayer player, int slot)
    {
        return getCosArmorInventory(player).isSkinArmor(slot);
    }

    public InventoryCosArmor getCosArmorInventory(EntityPlayer player)
    {
        // TODO
        return null;
    }

}
