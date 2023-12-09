package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.InventoryManager;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

public class PacketSetHiddenFlags implements NetworkPacket {

    String modid;
    String identifier;
    boolean set;

    public PacketSetHiddenFlags() {
    }

    public PacketSetHiddenFlags(String modid, String identifier, boolean set) {
        this.modid = modid;
        this.identifier = identifier;
        this.set = set;
    }

    @Override
    public void handlePacketClient(NetworkEvent.Context context) {
    }

    @Override
    public void handlePacketServer(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (InventoryManager.checkIdentifier(modid, identifier)) {
                ModObjects.invMan.getCosArmorInventory(context.getSender().getUUID()).setHidden(modid, identifier, set);
            }
        });
    }

    @Override
    public void readFromBuffer(FriendlyByteBuf buffer) {
        modid = buffer.readUtf(Short.MAX_VALUE);
        identifier = buffer.readUtf(Short.MAX_VALUE);
        set = buffer.readBoolean();
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeUtf(modid, Short.MAX_VALUE);
        buffer.writeUtf(identifier, Short.MAX_VALUE);
        buffer.writeBoolean(set);
    }

}
