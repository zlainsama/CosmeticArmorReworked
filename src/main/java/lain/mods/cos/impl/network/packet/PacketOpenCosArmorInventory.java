package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class PacketOpenCosArmorInventory implements NetworkPacket {

    public PacketOpenCosArmorInventory() {
    }

    @Override
    public void handlePacketClient(CustomPayloadEvent.Context context) {
    }

    @Override
    public void handlePacketServer(CustomPayloadEvent.Context context) {
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
