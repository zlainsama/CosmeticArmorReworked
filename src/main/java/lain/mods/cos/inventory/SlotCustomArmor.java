package lain.mods.cos.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Fix some slot issues and clean up some code with a custom slot class */
public class SlotCustomArmor extends Slot {

	private EntityPlayer owner;
	private int armorIndex;

	public SlotCustomArmor(EntityPlayer owner, IInventory inventory, int index, int xPosition, int yPosition, int armorIndex) {
		super(inventory, index, xPosition, yPosition);
		this.owner = owner;
		this.armorIndex = armorIndex;
	}

	public SlotCustomArmor(EntityPlayer owner, int index, int xPosition, int yPosition, int armorIndex) {
		this(owner, owner.inventory, index, xPosition, yPosition, armorIndex);
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getSlotTexture() {
		return ItemArmor.EMPTY_SLOT_NAMES[3 - armorIndex];
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack == null) return false;
		EntityEquipmentSlot slotCheck;
		switch(armorIndex) {
		case 0:
			slotCheck = EntityEquipmentSlot.HEAD;
			break;
		case 1:
			slotCheck = EntityEquipmentSlot.CHEST;
			break;
		case 2:
			slotCheck = EntityEquipmentSlot.LEGS;
			break;
		default:
			slotCheck = EntityEquipmentSlot.FEET;
		}
		return stack.getItem().isValidArmor(stack, slotCheck, owner);
	}

}
