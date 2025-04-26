package lain.mods.cos.api.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * This is the actual inventory associated with the player. <br>
 * Changes made to server side CAStacks will be sync to the clients. <br>
 * Do not make changes to client side CAStacks, it is not expected, and can cause problems. <br>
 * <br>
 * This class extends {@link ItemStackHandler}. <br>
 * <br>
 * CosmeticArmorReworked uses 4 slots. <br>
 * Slot 0-3 are {@link net.minecraft.world.entity.EquipmentSlot#FEET FEET}, {@link net.minecraft.world.entity.EquipmentSlot#LEGS LEGS}, {@link net.minecraft.world.entity.EquipmentSlot#CHEST CHEST}, {@link net.minecraft.world.entity.EquipmentSlot#HEAD HEAD}. <br>
 * <br>
 * For toggling visibilities of other mods, use these methods: <br>
 * {@link #setHidden(String, String, boolean) setHidden}, {@link #isHidden(String, String) isHidden}, {@link #forEachHidden(BiConsumer) forEachHidden}.
 */
public class CAStacksBase extends ItemStackHandler {

    protected final Map<String, Set<String>> hidden = new HashMap<>();

    protected boolean[] isSkinArmor;

    public CAStacksBase() {
        this(4);
    }

    public CAStacksBase(int size) {
        super(size);
        isSkinArmor = new boolean[stacks.size()];
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookup, CompoundTag nbt) {
        setSize(nbt.getInt("Size").orElse(stacks.size()));
        nbt.getList("Items").ifPresent(tagList -> {
            for (int i = 0; i < tagList.size(); i++) {
                tagList.getCompound(i).ifPresent(itemTags -> {
                    itemTags.getInt("Slot").ifPresent(slot -> {
                        if (slot >= 0 && slot < stacks.size()) {
                            if (itemTags.contains("id"))
                                ItemStack.parse(lookup, itemTags).ifPresent(stack -> stacks.set(slot, stack));
                            itemTags.getBoolean("isSkinArmor").ifPresent(b -> isSkinArmor[slot] = b);
                        }
                    });
                });
            }
        });
        hidden.clear();
        nbt.getString("Hidden").ifPresent(h -> {
            Arrays.stream(h.split("\0")).forEach(str -> {
                int i = str.indexOf(":");
                if (i != -1)
                    hidden.computeIfAbsent(str.substring(0, i), key -> new HashSet<>()).add(str.substring(i + 1));
            });
        });
        onLoad();
    }

    /**
     * Iterates through all set hidden other mods' things.
     *
     * @param consumer the consumer that will be accepting pairs of modid and identifier
     */
    public void forEachHidden(BiConsumer<String, String> consumer) {
        for (String modid : hidden.keySet())
            for (String identifier : hidden.get(modid))
                consumer.accept(modid, identifier);
    }

    /**
     * Checks to see if something should be hidden when rendering.
     *
     * @param modid      the modid of the related mod (example: curios)
     * @param identifier the identifier of the related slot (format: slotId#slotIndex) (example: ring#0)
     * @return true if the item in the related slot should be hidden when rendering
     */
    public boolean isHidden(String modid, String identifier) {
        return hidden.getOrDefault(modid, Collections.emptySet()).contains(identifier);
    }

    public boolean isSkinArmor(int slot) {
        validateSlotIndex(slot);
        return isSkinArmor[slot];
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookup) {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty() || isSkinArmor[i]) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                if (!stacks.get(i).isEmpty())
                    itemTag = (CompoundTag) stacks.get(i).save(lookup, itemTag);
                if (isSkinArmor[i])
                    itemTag.putBoolean("isSkinArmor", true);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", stacks.size());
        // writeUTF limit = a 16-bit unsigned integer = 65535 - Should be enough
        nbt.putString("Hidden", hidden.entrySet().stream().map(entry -> entry.getValue().stream().map(value -> entry.getKey() + ":" + value).collect(Collectors.joining("\0"))).collect(Collectors.joining("\0")));
        return nbt;
    }

    /**
     * Sets or removes something from hidden when rendering.
     *
     * @param modid      the modid of the related mod (example: curios)
     * @param identifier the identifer of the related slot (format: slotId#slotIndex) (example: ring#0)
     * @param set        true for set, false for remove
     * @return if something changed due to this invocation
     */
    public boolean setHidden(String modid, String identifier, boolean set) {
        if (set)
            return hidden.computeIfAbsent(modid, key -> new HashSet<>()).add(identifier);
        else
            return hidden.getOrDefault(modid, Collections.emptySet()).remove(identifier);
    }

    @Override
    public void setSize(int size) {
        super.setSize(size);
        isSkinArmor = new boolean[stacks.size()];
    }

    public void setSkinArmor(int slot, boolean enabled) {
        validateSlotIndex(slot);
        if (isSkinArmor[slot] == enabled)
            return;
        isSkinArmor[slot] = enabled;
        onContentsChanged(slot);
    }

}
