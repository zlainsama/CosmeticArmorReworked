package lain.mods.cos.integrations;

import com.m4thg33k.tombmanygraves.api.GraveInventoryHelper;
import com.m4thg33k.tombmanygraves.api.GraveRegistry;
import com.m4thg33k.tombmanygraves.api.IGraveInventory;
import com.m4thg33k.tombmanygraves.api.TempInventory;
import lain.mods.cos.api.CosArmorAPI;
import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@GraveRegistry(id = "cosmeticarmor", name = "Cosmetic Armor", overridable = false, reqMod = "cosmeticarmorreworked")
public class TombManyGravesIntegration implements IGraveInventory
{

    @Override
    public TempInventory getItems(EntityPlayer player)
    {
        CAStacksBase stacks = CosArmorAPI.getCAStacks(player.getUniqueID());
        TempInventory copy = new TempInventory(stacks.getSlots());
        for (int i = 0; i < stacks.getSlots(); i++)
            copy.setInventorySlotContents(i, stacks.getStackInSlot(i).copy());
        return copy;
    }

    @Override
    public void insertInventory(EntityPlayer player, TempInventory graveItems, boolean shouldForce)
    {
        CAStacksBase stacks = CosArmorAPI.getCAStacks(player.getUniqueID());
        for (int i = 0; i < graveItems.getSizeInventory(); i++)
        {
            ItemStack graveItem = graveItems.getStackInSlot(i);
            if (!graveItem.isEmpty())
            {
                ItemStack playerItem = stacks.getStackInSlot(i).copy();
                if (playerItem.isEmpty())
                {
                    stacks.setStackInSlot(i, graveItem);
                }
                else if (shouldForce)
                {
                    stacks.setStackInSlot(i, graveItem);
                    GraveInventoryHelper.dropItem(player, playerItem);
                }
                else
                {
                    GraveInventoryHelper.dropItem(player, graveItem);
                }
            }
        }
    }

    @Override
    public boolean pregrabLogic(EntityPlayer player)
    {
        return true;
    }

}
