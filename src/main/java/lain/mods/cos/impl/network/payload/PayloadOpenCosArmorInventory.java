package lain.mods.cos.impl.network.payload;

import lain.mods.cos.init.ModConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PayloadOpenCosArmorInventory() implements CustomPacketPayload {

    public static final Type<PayloadOpenCosArmorInventory> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModConstants.MODID, "open_cosarmor_inv"));

    public static final StreamCodec<FriendlyByteBuf, PayloadOpenCosArmorInventory> STREAM_CODEC = StreamCodec.of(PayloadOpenCosArmorInventory::encode, PayloadOpenCosArmorInventory::decode);

    private static PayloadOpenCosArmorInventory decode(FriendlyByteBuf buffer) {
        return new PayloadOpenCosArmorInventory();
    }

    private static void encode(FriendlyByteBuf buffer, PayloadOpenCosArmorInventory payload) {
    }

    @Override
    public Type<PayloadOpenCosArmorInventory> type() {
        return TYPE;
    }

}
