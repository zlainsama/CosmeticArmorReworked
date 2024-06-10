package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class PacketSetSkinArmor implements NetworkPacket {

    int slot;
    boolean isSkinArmor;

    public PacketSetSkinArmor() {
    }

    public PacketSetSkinArmor(int slot, boolean isSkinArmor) {
        this.slot = slot;
        this.isSkinArmor = isSkinArmor;
    }

    @Override
    public void handlePacketClient(CustomPayloadEvent.Context context) {
    }

    @Override
    public void handlePacketServer(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ModObjects.invMan.getCosArmorInventory(context.getSender().getUUID()).setSkinArmor(slot, isSkinArmor);
        });
    }

    @Override
    public void readFromBuffer(RegistryFriendlyByteBuf buffer) {
        slot = buffer.readByte();
        isSkinArmor = buffer.readBoolean();
    }

    @Override
    public void writeToBuffer(RegistryFriendlyByteBuf buffer) {
        buffer.writeByte(slot);
        buffer.writeBoolean(isSkinArmor);
    }

}
