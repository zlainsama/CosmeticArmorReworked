package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketOpenNormalInventory implements NetworkPacket {

    public PacketOpenNormalInventory() {
    }

    @Override
    public void handlePacketClient(Context context) {
    }

    @Override
    public void handlePacketServer(Context context) {
        context.enqueueWork(() -> {
            context.getSender().closeContainer();
        });
    }

    @Override
    public void readFromBuffer(PacketBuffer buffer) {
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer) {
    }

}
