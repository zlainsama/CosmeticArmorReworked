package lain.mods.cos.impl.inventory;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class InventoryCosArmor extends CAStacksBase implements IInventory, INamedContainerProvider
{

    @FunctionalInterface
    public interface ContentsChangeListener
    {

        void accept(InventoryCosArmor inv, int slot);

    }

    @FunctionalInterface
    public interface HiddenFlagsChangeListener
    {

        void accept(InventoryCosArmor inv, String modid, String identifier);

    }

    protected static final ITextComponent Name = new StringTextComponent("InventoryCosArmor");
    protected static final int MINSIZE = 11;

    protected final Collection<Object> listeners = new CopyOnWriteArrayList<>();

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
    public void closeInventory(PlayerEntity player)
    {
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory invPlayer, PlayerEntity player)
    {
        return new ContainerCosArmor(invPlayer, this, player, windowId);
    }

    @Override
    public ItemStack decrStackSize(int slot, int num)
    {
        return extractItem(slot, num, false);
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return Name;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
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
    public boolean isUsableByPlayer(PlayerEntity player)
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
        listeners.stream().filter(ContentsChangeListener.class::isInstance).map(ContentsChangeListener.class::cast).forEach(l -> l.accept(this, slot));
    }

    @Override
    protected void onLoad()
    {
        for (int i = 0; i < stacks.size(); i++)
        {
            int slot = i;
            listeners.stream().filter(ContentsChangeListener.class::isInstance).map(ContentsChangeListener.class::cast).forEach(l -> l.accept(this, slot));
        }
        for (String modid : hidden.keySet())
        {
            for (String identifier : hidden.get(modid))
            {
                listeners.stream().filter(HiddenFlagsChangeListener.class::isInstance).map(HiddenFlagsChangeListener.class::cast).forEach(l -> l.accept(this, modid, identifier));
            }
        }
    }

    @Override
    public void openInventory(PlayerEntity player)
    {
    }

    @Override
    public ItemStack removeStackFromSlot(int slot)
    {
        return extractItem(slot, Integer.MAX_VALUE, false);
    }

    @Override
    public boolean setHidden(String modid, String identifier, boolean set)
    {
        boolean changed;
        if (changed = super.setHidden(modid, identifier, set))
            listeners.stream().filter(HiddenFlagsChangeListener.class::isInstance).map(HiddenFlagsChangeListener.class::cast).forEach(l -> l.accept(this, modid, identifier));
        return changed;
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

    public boolean setUpdateListener(ContentsChangeListener listener)
    {
        if (listener == null || listeners.contains(listener))
            return false;
        return listeners.add(listener);
    }

    public boolean setUpdateListener(HiddenFlagsChangeListener listener)
    {
        if (listener == null || listeners.contains(listener))
            return false;
        return listeners.add(listener);
    }

}
