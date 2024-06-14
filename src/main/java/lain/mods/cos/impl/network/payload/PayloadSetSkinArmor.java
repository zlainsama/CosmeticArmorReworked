package lain.mods.cos.impl.network.payload;

import lain.mods.cos.init.ModConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PayloadSetSkinArmor(int slot, boolean isSkinArmor) implements CustomPacketPayload {

    public static final Type<PayloadSetSkinArmor> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModConstants.MODID, "set_skin_toggle"));

    public static final StreamCodec<FriendlyByteBuf, PayloadSetSkinArmor> STREAM_CODEC = StreamCodec.of(PayloadSetSkinArmor::encode, PayloadSetSkinArmor::decode);

    private static PayloadSetSkinArmor decode(FriendlyByteBuf buffer) {
        return new PayloadSetSkinArmor(
                buffer.readByte(),
                buffer.readBoolean()
        );
    }

    private static void encode(FriendlyByteBuf buffer, PayloadSetSkinArmor payload) {
        buffer.writeByte(payload.slot());
        buffer.writeBoolean(payload.isSkinArmor());
    }

    @Override
    public Type<PayloadSetSkinArmor> type() {
        return TYPE;
    }

}
