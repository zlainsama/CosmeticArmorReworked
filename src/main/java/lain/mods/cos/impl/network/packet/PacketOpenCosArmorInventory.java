package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

public class PacketOpenCosArmorInventory implements NetworkPacket {

    public PacketOpenCosArmorInventory() {
    }

    @Override
    public void handlePacketClient(NetworkEvent.Context context) {
    }

    @Override
    public void handlePacketServer(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            context.getSender().openMenu(ModObjects.invMan.getCosArmorInventory(context.getSender().getUUID()));
        });
    }

    @Override
    public void readFromBuffer(FriendlyByteBuf buffer) {
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
    }

}
