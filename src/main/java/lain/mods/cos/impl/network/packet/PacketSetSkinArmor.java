package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketSetSkinArmor implements NetworkPacket
{

    int slot;
    boolean isSkinArmor;

    public PacketSetSkinArmor()
    {
    }

    public PacketSetSkinArmor(int slot, boolean isSkinArmor)
    {
        this.slot = slot;
        this.isSkinArmor = isSkinArmor;
    }

    @Override
    public void handlePacketClient(Context context)
    {
    }

    @Override
    public void handlePacketServer(Context context)
    {
        context.enqueueWork(() -> {
            ModObjects.invMan.getCosArmorInventory(context.getSender().getUniqueID()).setSkinArmor(slot, isSkinArmor);
        });
    }

    @Override
    public void readFromBuffer(PacketBuffer buffer)
    {
        slot = buffer.readByte();
        isSkinArmor = buffer.readBoolean();
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer)
    {
        buffer.writeByte(slot);
        buffer.writeBoolean(isSkinArmor);
    }

}
