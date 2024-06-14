package lain.mods.cos.impl.network.payload;

import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.init.ModConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record PayloadSyncHiddenFlags(UUID uuid, String modid, String identifier,
                                     boolean hidden) implements CustomPacketPayload {

    public static final Type<PayloadSyncHiddenFlags> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModConstants.MODID, "sync_flag"));

    public static final StreamCodec<FriendlyByteBuf, PayloadSyncHiddenFlags> STREAM_CODEC = StreamCodec.of(PayloadSyncHiddenFlags::encode, PayloadSyncHiddenFlags::decode);

    public PayloadSyncHiddenFlags(UUID uuid, InventoryCosArmor inventory, String modid, String identifier) {
        this(
                uuid,
                modid,
                identifier,
                inventory.isHidden(modid, identifier)
        );
    }

    private static PayloadSyncHiddenFlags decode(FriendlyByteBuf buffer) {
        return new PayloadSyncHiddenFlags(
                new UUID(buffer.readLong(), buffer.readLong()),
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readBoolean()
        );
    }

    private static void encode(FriendlyByteBuf buffer, PayloadSyncHiddenFlags payload) {
        buffer.writeLong(payload.uuid().getMostSignificantBits());
        buffer.writeLong(payload.uuid().getLeastSignificantBits());
        buffer.writeUtf(payload.modid());
        buffer.writeUtf(payload.identifier());
        buffer.writeBoolean(payload.hidden());
    }

    @Override
    public Type<PayloadSyncHiddenFlags> type() {
        return TYPE;
    }

}
