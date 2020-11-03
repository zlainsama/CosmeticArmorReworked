package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkHooks;

public class PacketOpenCosArmorInventory implements NetworkPacket {

    public PacketOpenCosArmorInventory() {
    }

    @Override
    public void handlePacketClient(Context context) {
    }

    @Override
    public void handlePacketServer(Context context) {
        context.enqueueWork(() -> {
            NetworkHooks.openGui(context.getSender(), ModObjects.invMan.getCosArmorInventory(context.getSender().getUniqueID()));
        });
    }

    @Override
    public void readFromBuffer(PacketBuffer buffer) {
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer) {
    }

}
