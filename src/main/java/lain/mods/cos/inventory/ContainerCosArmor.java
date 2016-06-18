package lain.mods.cos.inventory;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerCosArmor extends Container
{

    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };

    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public IInventory craftResult = new InventoryCraftResult();
    public EntityPlayer player;

    public ContainerCosArmor(InventoryPlayer invPlayer, InventoryCosArmor invCosArmor, EntityPlayer player)
    {
        this.player = player;

        // CraftingResult
        addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, 0, 144, 36));

        // CraftingGrid
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                addSlotToContainer(new Slot(craftMatrix, j + i * 2, 106 + j * 18, 26 + i * 18));

        // NormalArmor
        for (int i = 0; i < 4; i++)
        {
            final int j = i;
            final EntityPlayer k = player;
            addSlotToContainer(new Slot(invPlayer, invPlayer.getSizeInventory() - 1 - invPlayer.offHandInventory.length - i, 8, 8 + i * 18)
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
                    return net.minecraft.item.ItemArmor.EMPTY_SLOT_NAMES[VALID_EQUIPMENT_SLOTS[j].getIndex()];
                }

                @Override
                public boolean isItemValid(ItemStack stack)
                {
                    if (stack == null)
                        return false;

                    return stack.getItem().isValidArmor(stack, VALID_EQUIPMENT_SLOTS[j], k);
                }

            });
        }

        // CosmeticArmor
        for (int i = 0; i < 4; i++)
        {
            final int j = i;
            final EntityPlayer k = player;
            addSlotToContainer(new Slot(invCosArmor, invCosArmor.getSizeInventory() - 1 - i, 80, 8 + i * 18)
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
                    return net.minecraft.item.ItemArmor.EMPTY_SLOT_NAMES[VALID_EQUIPMENT_SLOTS[j].getIndex()];
                }

                @Override
                public boolean isItemValid(ItemStack stack)
                {
                    if (stack == null)
                        return false;

                    return stack.getItem().isValidArmor(stack, VALID_EQUIPMENT_SLOTS[j], k);
                }

            });
        }

        // PlayerInventory
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlotToContainer(new Slot(invPlayer, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));

        // PlayerHotBar
        for (int i = 0; i < 9; i++)
            addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));

        onCraftMatrixChanged(craftMatrix);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot)
    {
        return (slot.inventory != craftResult) && (super.canMergeSlot(stack, slot));
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);

        for (int i = 0; i < 4; i++)
        {
            ItemStack stack = craftMatrix.removeStackFromSlot(i);

            if (stack != null)
                player.dropItem(stack, false);
        }

        craftResult.setInventorySlotContents(0, null);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inv)
    {
        craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, player.worldObj));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNumber)
    {
        ItemStack stack = null;
        Slot slot = (Slot) inventorySlots.get(slotNumber);

        if ((slot != null) && (slot.getHasStack()))
        {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();
            EntityEquipmentSlot desiredSlot = EntityLiving.getSlotForItemStack(stack);

            if (slotNumber == 0) // CraftingResult
            {
                if (!mergeItemStack(stack1, 13, 49, true))
                    return null;

                slot.onSlotChange(stack1, stack);
            }
            else if ((slotNumber >= 1) && (slotNumber < 5)) // CraftingGrid
            {
                if (!mergeItemStack(stack1, 13, 49, false))
                    return null;
            }
            else if ((slotNumber >= 5) && (slotNumber < 13)) // NormalArmor & CosmeticArmor
            {
                if (!mergeItemStack(stack1, 13, 49, false))
                    return null;
            }
            else if (desiredSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !inventorySlots.get(8 - desiredSlot.getIndex()).getHasStack()) // ItemArmor - check NormalArmor slots
            {
                int j = 8 - desiredSlot.getIndex();

                if (!mergeItemStack(stack1, j, j + 1, false))
                    return null;
            }
            else if (desiredSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !inventorySlots.get(12 - desiredSlot.getIndex()).getHasStack()) // ItemArmor - check CosmeticArmor slots
            {
                int j = 12 - desiredSlot.getIndex();

                if (!mergeItemStack(stack1, j, j + 1, false))
                    return null;
            }
            else if ((slotNumber >= 13) && (slotNumber < 40)) // PlayerInventory
            {
                if (!mergeItemStack(stack1, 40, 49, false))
                    return null;
            }
            else if ((slotNumber >= 40) && (slotNumber < 49)) // PlayerHotBar
            {
                if (!mergeItemStack(stack1, 13, 40, false))
                    return null;
            }
            else if (!mergeItemStack(stack1, 13, 49, false))
            {
                return null;
            }

            if (stack1.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();

            if (stack1.stackSize == stack.stackSize)
                return null;

            slot.onPickupFromSlot(player, stack1);
        }

        return stack;
    }
}
