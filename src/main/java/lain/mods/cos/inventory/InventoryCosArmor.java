package lain.mods.cos.inventory;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class InventoryCosArmor implements IInventory
{

    public static class CAStacks implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound>
    {

        protected NonNullList<ItemStack> stacks;
        protected boolean[] isSkinArmor;

        public CAStacks()
        {
            this(MINSIZE);
        }

        public CAStacks(int size)
        {
            setSize(size);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            setSize(nbt.hasKey("CosArmor.Inventory.Size", Constants.NBT.TAG_INT) ? nbt.getInteger("CosArmor.Inventory.Size") : stacks.size());
            NBTTagList tagList = nbt.getTagList("CosArmor.Inventory", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < tagList.tagCount(); i++)
            {
                NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
                int slot = itemTags.getByte("Slot") & 255;

                if (slot >= 0 && slot < stacks.size())
                {
                    stacks.set(slot, new ItemStack(itemTags));
                    isSkinArmor[slot] = itemTags.getBoolean("isSkinArmor");
                }
            }
            onLoad();
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if (amount == 0)
                return ItemStack.EMPTY;

            validateSlotIndex(slot);

            ItemStack existing = this.stacks.get(slot);

            if (existing.isEmpty())
                return ItemStack.EMPTY;

            int toExtract = Math.min(amount, existing.getMaxStackSize());

            if (existing.getCount() <= toExtract)
            {
                if (!simulate)
                {
                    this.stacks.set(slot, ItemStack.EMPTY);
                    onContentsChanged(slot);
                }
                return existing;
            }
            else
            {
                if (!simulate)
                {
                    this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                    onContentsChanged(slot);
                }

                return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
            }
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return 64;
        }

        @Override
        public int getSlots()
        {
            return stacks.size();
        }

        @Override
        public ItemStack getStackInSlot(int slot)
        {
            validateSlotIndex(slot);
            return this.stacks.get(slot);
        }

        protected int getStackLimit(int slot, @Nonnull ItemStack stack)
        {
            return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
        }

        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if (stack.isEmpty())
                return ItemStack.EMPTY;

            validateSlotIndex(slot);

            ItemStack existing = this.stacks.get(slot);

            int limit = getStackLimit(slot, stack);

            if (!existing.isEmpty())
            {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                    return stack;

                limit -= existing.getCount();
            }

            if (limit <= 0)
                return stack;

            boolean reachedLimit = stack.getCount() > limit;

            if (!simulate)
            {
                if (existing.isEmpty())
                {
                    this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                }
                else
                {
                    existing.grow(reachedLimit ? limit : stack.getCount());
                }
                onContentsChanged(slot);
            }

            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
        }

        public boolean isSkinArmor(int slot)
        {
            validateSlotIndex(slot);
            return isSkinArmor[slot];
        }

        protected void onContentsChanged(int slot)
        {
        }

        protected void onLoad()
        {
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagList nbtTagList = new NBTTagList();
            for (int i = 0; i < stacks.size(); i++)
            {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                if (!stacks.get(i).isEmpty())
                    stacks.get(i).writeToNBT(itemTag);
                itemTag.setBoolean("isSkinArmor", isSkinArmor[i]);
                nbtTagList.appendTag(itemTag);
            }
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setTag("CosArmor.Inventory", nbtTagList);
            nbt.setInteger("CosArmor.Inventory.Size", stacks.size());
            return nbt;
        }

        public void setSize(int size)
        {
            if (size < MINSIZE)
                size = MINSIZE;
            stacks = NonNullList.withSize(size, ItemStack.EMPTY);
            isSkinArmor = new boolean[size];
        }

        public void setSkinArmor(int slot, boolean enabled)
        {
            validateSlotIndex(slot);
            if (isSkinArmor[slot] == enabled)
                return;
            isSkinArmor[slot] = enabled;
            onContentsChanged(slot);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack)
        {
            validateSlotIndex(slot);
            if (ItemStack.areItemStacksEqual(stacks.get(slot), stack))
                return;
            stacks.set(slot, stack);
            onContentsChanged(slot);
        }

        protected void validateSlotIndex(int slot)
        {
            if (slot < 0 || slot >= stacks.size())
                throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
        }

    }

    public static final int MINSIZE = 11;

    CAStacks stacks = new CAStacks();
    boolean isDirty = false;

    @Override
    public void clear()
    {
        for (int i = 0; i < stacks.getSlots(); i++)
            stacks.setStackInSlot(i, ItemStack.EMPTY);
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
    }

    @Override
    public ItemStack decrStackSize(int slot, int num)
    {
        return stacks.extractItem(slot, num, false);
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

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public String getName()
    {
        return "";
    }

    @Override
    public int getSizeInventory()
    {
        return stacks.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return stacks.getStackInSlot(slot);
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
    public boolean isEmpty()
    {
        for (int i = 0; i < stacks.getSlots(); i++)
            if (!stacks.getStackInSlot(i).isEmpty())
                return false;
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return true;
    }

    public boolean isSkinArmor(int slot)
    {
        return stacks.isSkinArmor(slot);
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
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
        stacks.deserializeNBT(compound);
    }

    @Override
    public ItemStack removeStackFromSlot(int slot)
    {
        return stacks.extractItem(slot, Integer.MAX_VALUE, false);
    }

    @Override
    public void setField(int arg0, int arg1)
    {
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        stacks.setStackInSlot(slot, stack);
    }

    public void setSkinArmor(int slot, boolean enabled)
    {
        stacks.setSkinArmor(slot, enabled);
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        compound.merge(stacks.serializeNBT());
    }

}
