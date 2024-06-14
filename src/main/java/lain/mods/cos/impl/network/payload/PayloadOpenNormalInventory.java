package lain.mods.cos.impl.network.payload;

import lain.mods.cos.init.ModConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PayloadOpenNormalInventory() implements CustomPacketPayload {

    public static final Type<PayloadOpenNormalInventory> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModConstants.MODID, "open_normal_inv"));

    public static final StreamCodec<FriendlyByteBuf, PayloadOpenNormalInventory> STREAM_CODEC = StreamCodec.of(PayloadOpenNormalInventory::encode, PayloadOpenNormalInventory::decode);

    private static PayloadOpenNormalInventory decode(FriendlyByteBuf buffer) {
        return new PayloadOpenNormalInventory();
    }

    private static void encode(FriendlyByteBuf buffer, PayloadOpenNormalInventory payload) {
    }

    @Override
    public Type<PayloadOpenNormalInventory> type() {
        return TYPE;
    }

}
