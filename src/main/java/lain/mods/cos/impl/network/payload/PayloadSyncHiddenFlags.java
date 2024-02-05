package lain.mods.cos.impl.network.payload;

import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.init.ModConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record PayloadSyncHiddenFlags(UUID uuid, String modid, String identifier,
                                     boolean hidden) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ModConstants.MODID, "sync_flag");

    public PayloadSyncHiddenFlags(FriendlyByteBuf buffer) {
        this(
                new UUID(buffer.readLong(), buffer.readLong()),
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readBoolean()
        );
    }

    public PayloadSyncHiddenFlags(UUID uuid, InventoryCosArmor inventory, String modid, String identifier) {
        this(
                uuid,
                modid,
                identifier,
                inventory.isHidden(modid, identifier)
        );
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeLong(uuid.getMostSignificantBits());
        buffer.writeLong(uuid.getLeastSignificantBits());
        buffer.writeUtf(modid);
        buffer.writeUtf(identifier);
        buffer.writeBoolean(hidden);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

}
