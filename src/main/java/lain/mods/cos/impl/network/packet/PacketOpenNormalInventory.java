package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class PacketOpenNormalInventory implements NetworkPacket {

    public PacketOpenNormalInventory() {
    }

    @Override
    public void handlePacketClient(CustomPayloadEvent.Context context) {
    }

    @Override
    public void handlePacketServer(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            context.getSender().doCloseContainer();
        });
    }

    @Override
    public void readFromBuffer(RegistryFriendlyByteBuf buffer) {
    }

    @Override
    public void writeToBuffer(RegistryFriendlyByteBuf buffer) {
    }

}
