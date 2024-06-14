package lain.mods.cos.impl.network.payload;

import lain.mods.cos.init.ModConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PayloadSetHiddenFlags(String modid, String identifier, boolean hidden) implements CustomPacketPayload {

    public static final Type<PayloadSetHiddenFlags> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModConstants.MODID, "set_flag"));

    public static final StreamCodec<FriendlyByteBuf, PayloadSetHiddenFlags> STREAM_CODEC = StreamCodec.of(PayloadSetHiddenFlags::encode, PayloadSetHiddenFlags::decode);

    private static PayloadSetHiddenFlags decode(FriendlyByteBuf buffer) {
        return new PayloadSetHiddenFlags(
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readBoolean()
        );
    }

    private static void encode(FriendlyByteBuf buffer, PayloadSetHiddenFlags payload) {
        buffer.writeUtf(payload.modid(), Short.MAX_VALUE);
        buffer.writeUtf(payload.identifier(), Short.MAX_VALUE);
        buffer.writeBoolean(payload.hidden());
    }

    @Override
    public Type<PayloadSetHiddenFlags> type() {
        return TYPE;
    }

}
