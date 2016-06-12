package lain.mods.cos.ref;

import net.minecraft.inventory.EntityEquipmentSlot;

/** Armor reference to help convert between slot indexes and enum types */
public class ArmorRef {
	
	public static int getArmorIndex(EntityEquipmentSlot slot) {
		switch(slot) {
		case HEAD:
			return 0;
		case CHEST:
			return 1;
		case LEGS:
			return 2;
		case FEET:
			return 3;
		default:
			return -1;
		}
	}
	
	public static EntityEquipmentSlot getArmorSlot(int index) {
		switch(index) {
		case 0:
			return EntityEquipmentSlot.HEAD;
		case 1:
			return EntityEquipmentSlot.CHEST;
		case 2:
			return EntityEquipmentSlot.LEGS;
		case 3:
			return EntityEquipmentSlot.FEET;
		default:
			return null;
		}
	}

}
