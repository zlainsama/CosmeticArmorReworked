package lain.mods.cos.impl.inventory;

import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class InventoryCosArmor extends CAStacksBase implements Container, MenuProvider {

    protected static final Component Name = Component.literal("InventoryCosArmor");
    protected static final int MINSIZE = 11;

    protected final Collection<Object> listeners = new CopyOnWriteArrayList<>();

    public InventoryCosArmor() {
        super(MINSIZE);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getSlots(); i++)
            setStackInSlot(i, ItemStack.EMPTY);
    }

    @Override
    public void stopOpen(Player player) {
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory invPlayer, Player player) {
        return new ContainerCosArmor(invPlayer, this, player, windowId);
    }

    @Override
    public ItemStack removeItem(int slot, int num) {
        return extractItem(slot, num, false);
    }

    @Override
    public Component getDisplayName() {
        return Name;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public int getContainerSize() {
        return getSlots();
    }

//    @Override
//    public ItemStack getStackInSlot(int slot) {
//        return super.getStackInSlot(slot);
//    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getSlots(); i++)
            if (!getStackInSlot(i).isEmpty())
                return false;
        return true;
    }

    @Override
    public ItemStack getItem(int p_70301_1_) {
        return getStackInSlot(p_70301_1_);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void setChanged() {
    }

    @Override
    protected void onContentsChanged(int slot) {
        listeners.stream().filter(ContentsChangeListener.class::isInstance).map(ContentsChangeListener.class::cast).forEach(l -> l.accept(this, slot));
    }

    @Override
    protected void onLoad() {
        for (int i = 0; i < stacks.size(); i++) {
            int slot = i;
            listeners.stream().filter(ContentsChangeListener.class::isInstance).map(ContentsChangeListener.class::cast).forEach(l -> l.accept(this, slot));
        }
        for (String modid : hidden.keySet()) {
            for (String identifier : hidden.get(modid)) {
                listeners.stream().filter(HiddenFlagsChangeListener.class::isInstance).map(HiddenFlagsChangeListener.class::cast).forEach(l -> l.accept(this, modid, identifier));
            }
        }
    }

    @Override
    public void startOpen(Player player) {
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return extractItem(slot, Integer.MAX_VALUE, false);
    }

    @Override
    public boolean setHidden(String modid, String identifier, boolean set) {
        boolean changed;
        if (changed = super.setHidden(modid, identifier, set))
            listeners.stream().filter(HiddenFlagsChangeListener.class::isInstance).map(HiddenFlagsChangeListener.class::cast).forEach(l -> l.accept(this, modid, identifier));
        return changed;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        setStackInSlot(slot, stack);
    }

    @Override
    public void setSize(int size) {
        if (size < MINSIZE)
            size = MINSIZE;
        super.setSize(size);
    }

    public boolean setUpdateListener(ContentsChangeListener listener) {
        if (listener == null || listeners.contains(listener))
            return false;
        return listeners.add(listener);
    }

    public boolean setUpdateListener(HiddenFlagsChangeListener listener) {
        if (listener == null || listeners.contains(listener))
            return false;
        return listeners.add(listener);
    }

    @FunctionalInterface
    public interface ContentsChangeListener {

        void accept(InventoryCosArmor inv, int slot);

    }

    @FunctionalInterface
    public interface HiddenFlagsChangeListener {

        void accept(InventoryCosArmor inv, String modid, String identifier);

    }

}
