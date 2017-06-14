package lain.mods.cos.inventory;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCosArmor extends ContainerPlayer
{

    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };

    public ContainerCosArmor(InventoryPlayer invPlayer, InventoryCosArmor invCosArmor, EntityPlayer player)
    {
        super(invPlayer, !player.world.isRemote, player);

        // CosmeticArmor
        for (int i = 0; i < 4; i++)
        {
            final int j = i;
            final EntityPlayer k = player;
            addSlotToContainer(new Slot(invCosArmor, invCosArmor.getSizeInventory() - 1 - i, 98 + i * 18, 62)
            {

                @Override
                public int getSlotStackLimit()
                {
                    return 1;
                }

                @SideOnly(Side.CLIENT)
                @Override
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[VALID_EQUIPMENT_SLOTS[j].getIndex()];
                }

                @Override
                public boolean isItemValid(ItemStack stack)
                {
                    if (stack == null || stack.isEmpty())
                        return false;

                    return stack.getItem().isValidArmor(stack, VALID_EQUIPMENT_SLOTS[j], k);
                }

            });
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNumber)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = (Slot) inventorySlots.get(slotNumber);

        if ((slot != null) && (slot.getHasStack()))
        {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();
            EntityEquipmentSlot desiredSlot = EntityLiving.getSlotForItemStack(stack);

            if (slotNumber == 0) // CraftingResult
            {
                if (!mergeItemStack(stack1, 9, 45, true))
                    return ItemStack.EMPTY;

                slot.onSlotChange(stack1, stack);
            }
            else if ((slotNumber >= 1) && (slotNumber < 5)) // CraftingGrid
            {
                if (!mergeItemStack(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            }
            else if ((slotNumber >= 5) && (slotNumber < 9)) // NormalArmor
            {
                if (!mergeItemStack(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            }
            else if ((slotNumber >= 46) && (slotNumber < 50)) // CosmeticArmor
            {
                if (!mergeItemStack(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            }
            else if (desiredSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !inventorySlots.get(8 - desiredSlot.getIndex()).getHasStack()) // ItemArmor - check NormalArmor slots
            {
                int j = 8 - desiredSlot.getIndex();

                if (!mergeItemStack(stack1, j, j + 1, false))
                    return ItemStack.EMPTY;
            }
            else if (desiredSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !inventorySlots.get(49 - desiredSlot.getIndex()).getHasStack()) // ItemArmor - check CosmeticArmor slots
            {
                int j = 49 - desiredSlot.getIndex();

                if (!mergeItemStack(stack1, j, j + 1, false))
                    return ItemStack.EMPTY;
            }
            else if ((slotNumber >= 9) && (slotNumber < 36)) // PlayerInventory
            {
                if (!mergeItemStack(stack1, 36, 45, false))
                    return ItemStack.EMPTY;
            }
            else if ((slotNumber >= 36) && (slotNumber < 45)) // PlayerHotBar
            {
                if (!mergeItemStack(stack1, 9, 36, false))
                    return ItemStack.EMPTY;
            }
            else if (!mergeItemStack(stack1, 9, 45, false))
            {
                return ItemStack.EMPTY;
            }

            if (stack1.isEmpty())
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();

            if (stack1.getCount() == stack.getCount())
                return ItemStack.EMPTY;

            ItemStack stack2 = slot.onTake(player, stack1);

            if (slotNumber == 0)
                player.dropItem(stack2, false);
        }

        return stack;
    }

}
