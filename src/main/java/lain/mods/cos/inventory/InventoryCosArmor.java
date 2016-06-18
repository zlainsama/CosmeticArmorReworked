package lain.mods.cos.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryCosArmor implements IInventory
{

    ItemStack[] stacks = new ItemStack[4];
    boolean[] isSkinArmor = new boolean[4];
    boolean isDirty = false;

    @Override
    public void clear()
    {
        for (int i = 0; i < stacks.length; i++)
            stacks[i] = null;
    }

    @Override
    public void closeInventory(EntityPlayer player)
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
    public ITextComponent getDisplayName()
    {
        if (hasCustomName())
        {
            return new TextComponentString(getName());
        }
        return new TextComponentTranslation(getName());
    }

    @Override
    public int getField(int arg0)
    {
        return 0;
    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    public ItemStack[] getInventory()
    {
        return stacks;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    @Override
    public String getName()
    {
        return "";
    }

    @Override
    public int getSizeInventory()
    {
        return stacks == null ? 0 : stacks.length;
    }

    public boolean[] getSkinArmor()
    {
        return isSkinArmor;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (stacks == null || slot < 0 || slot >= stacks.length)
            return null;

        return stacks[slot];
    }

    @Override
    public boolean hasCustomName()
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

    public boolean isSkinArmor(int slot)
    {
        if (isSkinArmor == null || slot < 0 || slot >= isSkinArmor.length)
            return false;

        return isSkinArmor[slot];
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
    public void openInventory(EntityPlayer player)
    {
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        stacks = new ItemStack[compound.getInteger("CosArmor.Inventory.Size")];
        isSkinArmor = new boolean[stacks.length];
        NBTTagList tagList = compound.getTagList("CosArmor.Inventory", 10);
        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound invSlot = (NBTTagCompound) tagList.getCompoundTagAt(i);
            int j = invSlot.getByte("Slot") & 255;
            ItemStack stack = ItemStack.loadItemStackFromNBT(invSlot);
            if (stack != null)
                stacks[j] = stack;
            isSkinArmor[j] = invSlot.getBoolean("isSkinArmor");
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int slot)
    {
        if (stacks == null || slot < 0 || slot >= stacks.length)
            return null;

        ItemStack stack = stacks[slot];
        stacks[slot] = null;
        return stack;
    }

    @Override
    public void setField(int arg0, int arg1)
    {
    }

    public void setInventory(ItemStack[] stacks)
    {
        this.stacks = stacks;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (stacks == null || slot < 0 || slot >= stacks.length)
            return;

        stacks[slot] = stack;
    }

    public void setSkinArmor(boolean[] isSkinArmor)
    {
        this.isSkinArmor = isSkinArmor;
    }

    public void setSkinArmor(int slot, boolean enabled)
    {
        if (isSkinArmor == null || slot < 0 || slot >= isSkinArmor.length)
            return;

        isSkinArmor[slot] = enabled;
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger("CosArmor.Inventory.Size", stacks.length);
        NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < stacks.length; i++)
        {
            NBTTagCompound invSlot = new NBTTagCompound();
            invSlot.setByte("Slot", (byte) i);
            if (stacks[i] != null)
                stacks[i].writeToNBT(invSlot);
            invSlot.setBoolean("isSkinArmor", isSkinArmor[i]);
            tagList.appendTag(invSlot);
        }
        compound.setTag("CosArmor.Inventory", tagList);
    }

}
