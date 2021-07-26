package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class PacketOpenNormalInventory implements NetworkPacket {

    public PacketOpenNormalInventory() {
    }

    @Override
    public void handlePacketClient(NetworkEvent.Context context) {
    }

    @Override
    public void handlePacketServer(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            context.getSender().doCloseContainer();
        });
    }

    @Override
    public void readFromBuffer(FriendlyByteBuf buffer) {
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
    }

}
