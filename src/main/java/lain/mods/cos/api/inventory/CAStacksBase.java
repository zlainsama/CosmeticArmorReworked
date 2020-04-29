package lain.mods.cos.api.inventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

/**
 * This is the actual inventory associated with the player. <br>
 * Changes made to server side CAStacks will be sync to the clients. <br>
 * Do not make changes to client side CAStacks, it is not expected, and can cause problems. <br>
 * <br>
 * This class extends {@link ItemStackHandler}. <br>
 * <br>
 * CosmeticArmorReworked uses 11 slots. <br>
 * Slot 0-3 are {@link net.minecraft.inventory.EntityEquipmentSlot#FEET FEET}, {@link net.minecraft.inventory.EntityEquipmentSlot#LEGS LEGS}, {@link net.minecraft.inventory.EntityEquipmentSlot#CHEST CHEST}, {@link net.minecraft.inventory.EntityEquipmentSlot#HEAD HEAD}. <br>
 * Slot 4-10 are Baubles, the player can only setSkinArmor for them. <br>
 */
public class CAStacksBase extends ItemStackHandler
{

    protected boolean[] isSkinArmor;
    protected final Map<String, Set<String>> hidden = new HashMap<>();

    public CAStacksBase()
    {
        this(4);
    }

    public CAStacksBase(int size)
    {
        super(size);
        isSkinArmor = new boolean[stacks.size()];
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        setSize(nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : stacks.size());
        ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++)
        {
            CompoundNBT itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < stacks.size())
            {
                if (itemTags.contains("id"))
                    stacks.set(slot, ItemStack.read(itemTags));
                if (itemTags.contains("isSkinArmor"))
                    isSkinArmor[slot] = itemTags.getBoolean("isSkinArmor");
            }
        }
        hidden.clear();
        Arrays.stream(nbt.getString("Hidden").split("\0")).forEach(str -> {
            int i = str.indexOf(":");
            if (i != -1)
                hidden.computeIfAbsent(str.substring(0, i), key -> new HashSet<>()).add(str.substring(i + 1));
        });
        onLoad();
    }

    public void forEachHidden(BiConsumer<String, String> consumer)
    {
        for (String modid : hidden.keySet())
            for (String identifier : hidden.get(modid))
                consumer.accept(modid, identifier);
    }

    public boolean isHidden(String modid, String identifier)
    {
        return hidden.getOrDefault(modid, Collections.emptySet()).contains(identifier);
    }

    public boolean isSkinArmor(int slot)
    {
        validateSlotIndex(slot);
        return isSkinArmor[slot];
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        ListNBT nbtTagList = new ListNBT();
        for (int i = 0; i < stacks.size(); i++)
        {
            if (!stacks.get(i).isEmpty() || isSkinArmor[i])
            {
                CompoundNBT itemTag = new CompoundNBT();
                itemTag.putInt("Slot", i);
                if (!stacks.get(i).isEmpty())
                    stacks.get(i).write(itemTag);
                if (isSkinArmor[i])
                    itemTag.putBoolean("isSkinArmor", true);
                nbtTagList.add(itemTag);
            }
        }
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", stacks.size());
        // writeUTF limit = a 16-bit unsigned integer = 65535 - Should be enough
        nbt.putString("Hidden", hidden.entrySet().stream().map(entry -> entry.getValue().stream().map(value -> entry.getKey() + ":" + value).collect(Collectors.joining("\0"))).collect(Collectors.joining("\0")));
        return nbt;
    }

    public boolean setHidden(String modid, String identifier, boolean set)
    {
        if (set)
            return hidden.computeIfAbsent(modid, key -> new HashSet<>()).add(identifier);
        else
            return hidden.getOrDefault(modid, Collections.emptySet()).remove(identifier);
    }

    @Override
    public void setSize(int size)
    {
        super.setSize(size);
        isSkinArmor = new boolean[stacks.size()];
    }

    public void setSkinArmor(int slot, boolean enabled)
    {
        validateSlotIndex(slot);
        if (isSkinArmor[slot] == enabled)
            return;
        isSkinArmor[slot] = enabled;
        onContentsChanged(slot);
    }

}
