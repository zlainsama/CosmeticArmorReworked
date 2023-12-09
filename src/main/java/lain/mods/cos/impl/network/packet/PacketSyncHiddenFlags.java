package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.InventoryManager;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.UUID;

public class PacketSyncHiddenFlags implements NetworkPacket {

    UUID uuid;
    String modid;
    String identifier;
    boolean hidden;

    public PacketSyncHiddenFlags() {
    }

    public PacketSyncHiddenFlags(UUID uuid, InventoryCosArmor inventory, String modid, String identifier) {
        this.uuid = uuid;
        this.modid = modid;
        this.identifier = identifier;
        this.hidden = inventory.isHidden(modid, identifier);
    }

    @Override
    public void handlePacketClient(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (InventoryManager.checkIdentifier(modid, identifier)) {
                ModObjects.invMan.getCosArmorInventoryClient(uuid).setHidden(modid, identifier, hidden);
            }
        });
    }

    @Override
    public void handlePacketServer(NetworkEvent.Context context) {
    }

    @Override
    public void readFromBuffer(FriendlyByteBuf buffer) {
        uuid = new UUID(buffer.readLong(), buffer.readLong());
        modid = buffer.readUtf(Short.MAX_VALUE);
        identifier = buffer.readUtf(Short.MAX_VALUE);
        hidden = buffer.readBoolean();
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeLong(uuid.getMostSignificantBits());
        buffer.writeLong(uuid.getLeastSignificantBits());
        buffer.writeUtf(modid, Short.MAX_VALUE);
        buffer.writeUtf(identifier, Short.MAX_VALUE);
        buffer.writeBoolean(hidden);
    }

}
