package lain.mods.cos.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryCosArmor implements IInventory
{

    ItemStack[] stacks = new ItemStack[4];
    boolean isDirty = false;

    @Override
    public void closeInventory()
    {
    }

    @Override
    public ItemStack decrStackSize(int slot, int num)
    {
        if (stacks == null || slot < 0 || slot >= stacks.length)
            return null;

        ItemStack stack = stacks[slot];

        if (stack == null)
            return null;

        if (stack.stackSize <= num)
            stacks[slot] = null;
        else
            stack = stack.splitStack(num);

        return stack;
    }

    @Override
    public String getInventoryName()
    {
        return "";
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    @Override
    public int getSizeInventory()
    {
        return stacks == null ? 0 : stacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (stacks == null || slot < 0 || slot >= stacks.length)
            return null;

        return stacks[slot];
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (stacks == null || slot < 0 || slot >= stacks.length)
            return null;

        ItemStack stack = stacks[slot];
        stacks[slot] = null;
        return stack;
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    public boolean isDirty()
    {
        return isDirty;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    public void markClean()
    {
        isDirty = false;
    }

    @Override
    public void markDirty()
    {
        isDirty = true;
    }

    @Override
    public void openInventory()
    {
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (stacks == null || slot < 0 || slot >= stacks.length)
            return;

        stacks[slot] = stack;
    }

}
