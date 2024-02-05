package lain.mods.cos.impl.network.payload;

import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.init.ModConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public record PayloadSyncCosArmor(UUID uuid, int slot, boolean isSkinArmor,
                                  ItemStack itemCosArmor) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MODID, "sync_slot");

    public PayloadSyncCosArmor(FriendlyByteBuf buffer) {
        this(
                new UUID(buffer.readLong(), buffer.readLong()),
                buffer.readByte(),
                buffer.readBoolean(),
                buffer.readItem()
        );
    }

    public PayloadSyncCosArmor(UUID uuid, InventoryCosArmor inventory, int slot) {
        this(
                uuid,
                slot,
                inventory.isSkinArmor(slot),
                inventory.getStackInSlot(slot)
        );
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeLong(uuid.getMostSignificantBits());
        buffer.writeLong(uuid.getLeastSignificantBits());
        buffer.writeByte(slot);
        buffer.writeBoolean(isSkinArmor);
        buffer.writeItem(itemCosArmor);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

}
