package lain.mods.cos.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryCosArmor implements IInventory
{

    NonNullList<ItemStack> stacks = NonNullList.func_191197_a(4, ItemStack.field_190927_a);
    boolean[] isSkinArmor = new boolean[4];
    boolean isDirty = false;

    @Override
    public void clear()
    {
        for (int i = 0; i < stacks.size(); i++)
            stacks.set(i, ItemStack.field_190927_a);
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
    }

    @Override
    public ItemStack decrStackSize(int slot, int num)
    {
        if (stacks == null || slot < 0 || slot >= stacks.size())
            return ItemStack.field_190927_a;

        ItemStack stack = stacks.get(slot);

        if (stack.func_190926_b())
            return ItemStack.field_190927_a;

        if (stack.func_190916_E() <= num)
            stacks.set(slot, ItemStack.field_190927_a);
        else
            stack = stack.splitStack(num);

        return stack;
    }

    @Override
    public boolean func_191420_l()
    {
        for (int i = 0; i < stacks.size(); i++)
            if (!stacks.get(i).func_190926_b())
                return false;
        return true;
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

    public NonNullList<ItemStack> getInventory()
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
        return stacks == null ? 0 : stacks.size();
    }

    public boolean[] getSkinArmor()
    {
        return isSkinArmor;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (stacks == null || slot < 0 || slot >= stacks.size())
            return ItemStack.field_190927_a;

        return stacks.get(slot);
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
        stacks = NonNullList.func_191197_a(compound.getInteger("CosArmor.Inventory.Size"), ItemStack.field_190927_a);
        isSkinArmor = new boolean[stacks.size()];
        NBTTagList tagList = compound.getTagList("CosArmor.Inventory", 10);
        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound invSlot = (NBTTagCompound) tagList.getCompoundTagAt(i);
            int j = invSlot.getByte("Slot") & 255;
            ItemStack stack = new ItemStack(invSlot);
            if (stack != null)
                stacks.set(j, stack);
            isSkinArmor[j] = invSlot.getBoolean("isSkinArmor");
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int slot)
    {
        if (stacks == null || slot < 0 || slot >= stacks.size())
            return null;

        ItemStack stack = stacks.get(slot);
        stacks.set(slot, ItemStack.field_190927_a);
        return stack;
    }

    @Override
    public void setField(int arg0, int arg1)
    {
    }

    public void setInventory(NonNullList<ItemStack> stacks)
    {
        this.stacks = stacks;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (stacks == null || slot < 0 || slot >= stacks.size())
            return;

        stacks.set(slot, stack);
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
        compound.setInteger("CosArmor.Inventory.Size", stacks.size());
        NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < stacks.size(); i++)
        {
            NBTTagCompound invSlot = new NBTTagCompound();
            invSlot.setByte("Slot", (byte) i);
            if (!stacks.get(i).func_190926_b())
                stacks.get(i).writeToNBT(invSlot);
            invSlot.setBoolean("isSkinArmor", isSkinArmor[i]);
            tagList.appendTag(invSlot);
        }
        compound.setTag("CosArmor.Inventory", tagList);
    }

}
