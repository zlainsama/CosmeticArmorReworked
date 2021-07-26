package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class PacketOpenCosArmorInventory implements NetworkPacket {

    public PacketOpenCosArmorInventory() {
    }

    @Override
    public void handlePacketClient(NetworkEvent.Context context) {
    }

    @Override
    public void handlePacketServer(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            NetworkHooks.openGui(context.getSender(), ModObjects.invMan.getCosArmorInventory(context.getSender().getUUID()));
        });
    }

    @Override
    public void readFromBuffer(FriendlyByteBuf buffer) {
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
    }

}
