package lain.mods.cos.impl.network.payload;

import lain.mods.cos.init.ModConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PayloadSetSkinArmor(int slot, boolean isSkinArmor) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MODID, "set_skin_toggle");

    public PayloadSetSkinArmor(FriendlyByteBuf buffer) {
        this(
                buffer.readByte(),
                buffer.readBoolean()
        );
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeByte(slot);
        buffer.writeBoolean(isSkinArmor);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

}
