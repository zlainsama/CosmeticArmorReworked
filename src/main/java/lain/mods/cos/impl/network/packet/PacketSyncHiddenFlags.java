package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.InventoryManager;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

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
    public void handlePacketClient(Context context) {
        context.enqueueWork(() -> {
            if (InventoryManager.checkIdentifier(modid, identifier)) {
                ModObjects.invMan.getCosArmorInventoryClient(uuid).setHidden(modid, identifier, hidden);
            }
        });
    }

    @Override
    public void handlePacketServer(Context context) {
    }

    @Override
    public void readFromBuffer(PacketBuffer buffer) {
        uuid = new UUID(buffer.readLong(), buffer.readLong());
        modid = buffer.readString(Short.MAX_VALUE);
        identifier = buffer.readString(Short.MAX_VALUE);
        hidden = buffer.readBoolean();
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer) {
        buffer.writeLong(uuid.getMostSignificantBits());
        buffer.writeLong(uuid.getLeastSignificantBits());
        buffer.writeString(modid, Short.MAX_VALUE);
        buffer.writeString(identifier, Short.MAX_VALUE);
        buffer.writeBoolean(hidden);
    }

}
