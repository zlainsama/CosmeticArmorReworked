package lain.mods.cos.api.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
    public void deserializeNBT(NBTTagCompound nbt)
    {
        setSize(nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : stacks.size());
        NBTTagList tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++)
        {
            NBTTagCompound itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < stacks.size())
            {
                if (itemTags.hasKey("id"))
                    stacks.set(slot, ItemStack.read(itemTags));
                if (itemTags.hasKey("isSkinArmor"))
                    isSkinArmor[slot] = itemTags.getBoolean("isSkinArmor");
            }
        }
        onLoad();
    }

    public boolean isSkinArmor(int slot)
    {
        validateSlotIndex(slot);
        return isSkinArmor[slot];
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < stacks.size(); i++)
        {
            if (!stacks.get(i).isEmpty() || isSkinArmor[i])
            {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setInt("Slot", i);
                if (!stacks.get(i).isEmpty())
                    stacks.get(i).write(itemTag);
                if (isSkinArmor[i])
                    itemTag.setBoolean("isSkinArmor", true);
                nbtTagList.add(itemTag);
            }
        }
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Items", nbtTagList);
        nbt.setInt("Size", stacks.size());
        return nbt;
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
