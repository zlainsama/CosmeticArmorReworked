package lain.mods.cos.impl.network.payload;

import lain.mods.cos.init.ModConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PayloadSetHiddenFlags(String modid, String identifier, boolean hidden) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MODID, "set_flag");

    public PayloadSetHiddenFlags(FriendlyByteBuf buffer) {
        this(
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readBoolean()
        );
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(modid, Short.MAX_VALUE);
        buffer.writeUtf(identifier, Short.MAX_VALUE);
        buffer.writeBoolean(hidden);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

}
