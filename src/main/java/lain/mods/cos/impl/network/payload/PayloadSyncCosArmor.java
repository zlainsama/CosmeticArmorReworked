package lain.mods.cos.impl.network.payload;

import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.init.ModConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public record PayloadSyncCosArmor(UUID uuid, int slot, boolean isSkinArmor,
                                  ItemStack itemCosArmor) implements CustomPacketPayload {

    public static final Type<PayloadSyncCosArmor> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModConstants.MODID, "sync_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PayloadSyncCosArmor> STREAM_CODEC = StreamCodec.of(PayloadSyncCosArmor::encode, PayloadSyncCosArmor::decode);

    public PayloadSyncCosArmor(UUID uuid, InventoryCosArmor inventory, int slot) {
        this(
                uuid,
                slot,
                inventory.isSkinArmor(slot),
                inventory.getStackInSlot(slot)
        );
    }

    private static PayloadSyncCosArmor decode(RegistryFriendlyByteBuf buffer) {
        return new PayloadSyncCosArmor(
                new UUID(buffer.readLong(), buffer.readLong()),
                buffer.readByte(),
                buffer.readBoolean(),
                ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer)
        );
    }

    private static void encode(RegistryFriendlyByteBuf buffer, PayloadSyncCosArmor payload) {
        buffer.writeLong(payload.uuid().getMostSignificantBits());
        buffer.writeLong(payload.uuid().getLeastSignificantBits());
        buffer.writeByte(payload.slot());
        buffer.writeBoolean(payload.isSkinArmor());
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, payload.itemCosArmor());
    }

    @Override
    public Type<PayloadSyncCosArmor> type() {
        return TYPE;
    }

}
