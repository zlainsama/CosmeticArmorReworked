package lain.mods.cos.impl.network.payload;

import lain.mods.cos.init.ModConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PayloadOpenCosArmorInventory() implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MODID, "open_cosarmor_inv");

    public PayloadOpenCosArmorInventory(FriendlyByteBuf buffer) {
        this();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

}
