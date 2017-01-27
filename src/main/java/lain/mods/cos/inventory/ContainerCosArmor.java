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
import net.minecraft.item.ItemArmor;
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
        addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, 0, 154, 28));

        // CraftingGrid
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                addSlotToContainer(new Slot(craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));

        // NormalArmor
        for (int i = 0; i < 4; i++)
        {
            final int j = i;
            final EntityPlayer k = player;
            addSlotToContainer(new Slot(invPlayer, invPlayer.getSizeInventory() - 1 - invPlayer.offHandInventory.size() - i, 8, 8 + i * 18)
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

        // PlayerInventory
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlotToContainer(new Slot(invPlayer, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));

        // PlayerHotBar
        for (int i = 0; i < 9; i++)
            addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));

        // Off-Hand Slot
        addSlotToContainer(new Slot(invPlayer, 40, 77, 62)
        {

            @SideOnly(Side.CLIENT)
            @Override
            public String getSlotTexture()
            {
                return "minecraft:items/empty_armor_slot_shield";
            }

        });

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

        craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inv)
    {
        craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, player.world));
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
                if (!mergeItemStack(stack1, 13, 49, true))
                    return ItemStack.EMPTY;

                slot.onSlotChange(stack1, stack);
            }
            else if ((slotNumber >= 1) && (slotNumber < 5)) // CraftingGrid
            {
                if (!mergeItemStack(stack1, 13, 49, false))
                    return ItemStack.EMPTY;
            }
            else if ((slotNumber >= 5) && (slotNumber < 13)) // NormalArmor & CosmeticArmor
            {
                if (!mergeItemStack(stack1, 13, 49, false))
                    return ItemStack.EMPTY;
            }
            else if (desiredSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !inventorySlots.get(8 - desiredSlot.getIndex()).getHasStack()) // ItemArmor - check NormalArmor slots
            {
                int j = 8 - desiredSlot.getIndex();

                if (!mergeItemStack(stack1, j, j + 1, false))
                    return ItemStack.EMPTY;
            }
            else if (desiredSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !inventorySlots.get(12 - desiredSlot.getIndex()).getHasStack()) // ItemArmor - check CosmeticArmor slots
            {
                int j = 12 - desiredSlot.getIndex();

                if (!mergeItemStack(stack1, j, j + 1, false))
                    return ItemStack.EMPTY;
            }
            else if ((slotNumber >= 13) && (slotNumber < 40)) // PlayerInventory
            {
                if (!mergeItemStack(stack1, 40, 49, false))
                    return ItemStack.EMPTY;
            }
            else if ((slotNumber >= 40) && (slotNumber < 49)) // PlayerHotBar
            {
                if (!mergeItemStack(stack1, 13, 40, false))
                    return ItemStack.EMPTY;
            }
            else if (!mergeItemStack(stack1, 13, 49, false))
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
