package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

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
    public void handlePacketClient(NetworkEvent.Context context) {
    }

    @Override
    public void handlePacketServer(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ModObjects.invMan.getCosArmorInventory(context.getSender().getUUID()).setSkinArmor(slot, isSkinArmor);
        });
    }

    @Override
    public void readFromBuffer(FriendlyByteBuf buffer) {
        slot = buffer.readByte();
        isSkinArmor = buffer.readBoolean();
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeByte(slot);
        buffer.writeBoolean(isSkinArmor);
    }

}
