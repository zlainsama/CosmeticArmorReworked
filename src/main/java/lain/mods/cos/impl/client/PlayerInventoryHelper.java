package lain.mods.cos.impl.client;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.EnumMap;
import java.util.OptionalInt;

public class PlayerInventoryHelper {

    private static final EnumMap<EquipmentSlot, OptionalInt> known = new EnumMap<>(EquipmentSlot.class);

    static {
        for (EquipmentSlot s : EquipmentSlot.values())
            getPlayerEquipmentSlotIndex(s);
    }

    public static Inventory getPlayerInventory(Player player) {
        return player.getInventory();
    }

    public static OptionalInt getPlayerEquipmentSlotIndex(EquipmentSlot equipmentSlot) {
        return known.computeIfAbsent(equipmentSlot, s -> {
            for (int i : Inventory.EQUIPMENT_SLOT_MAPPING.keySet()) {
                if (Inventory.EQUIPMENT_SLOT_MAPPING.get(i) == s)
                    return OptionalInt.of(i);
            }
            return OptionalInt.empty();
        });
    }

}