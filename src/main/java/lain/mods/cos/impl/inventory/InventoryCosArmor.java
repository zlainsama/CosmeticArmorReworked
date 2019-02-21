package lain.mods.cos.impl.inventory;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryCosArmor extends CAStacksBase implements IInventory
{

    protected static final ITextComponent Name = new TextComponentString("InventoryCosArmor");
    protected static final int MINSIZE = 11;

    protected final Collection<BiConsumer<InventoryCosArmor, Integer>> listeners = new CopyOnWriteArrayList<>();

    public InventoryCosArmor()
    {
        super(MINSIZE);
    }

    @Override
    public void clear()
    {
        for (int i = 0; i < getSlots(); i++)
            setStackInSlot(i, ItemStack.EMPTY);
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
    }

    @Override
    public ItemStack decrStackSize(int slot, int num)
    {
        return extractItem(slot, num, false);
    }

    @Override
    public ITextComponent getCustomName()
    {
        return getName();
    }

    @Override
    public int getField(int id)
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
    public ITextComponent getName()
    {
        return Name;
    }

    @Override
    public int getSizeInventory()
    {
        return getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return super.getStackInSlot(slot);
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public boolean isEmpty()
    {
        for (int i = 0; i < getSlots(); i++)
            if (!getStackInSlot(i).isEmpty())
                return false;
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void markDirty()
    {
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        listeners.forEach(l -> l.accept(this, slot));
    }

    @Override
    protected void onLoad()
    {
        for (int i = 0; i < stacks.size(); i++)
        {
            int slot = i;
            listeners.forEach(l -> l.accept(this, slot));
        }
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
    }

    @Override
    public ItemStack removeStackFromSlot(int slot)
    {
        return extractItem(slot, Integer.MAX_VALUE, false);
    }

    @Override
    public void setField(int id, int value)
    {
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        setStackInSlot(slot, stack);
    }

    @Override
    public void setSize(int size)
    {
        if (size < MINSIZE)
            size = MINSIZE;
        super.setSize(size);
    }

    public boolean setUpdateListener(BiConsumer<InventoryCosArmor, Integer> listener)
    {
        if (listener == null || listeners.contains(listener))
            return false;
        return listeners.add(listener);
    }

}
