package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.InventoryManager;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PacketSetHiddenFlags implements NetworkPacket
{

    String modid;
    String identifier;
    boolean set;

    public PacketSetHiddenFlags()
    {
    }

    public PacketSetHiddenFlags(String modid, String identifier, boolean set)
    {
        this.modid = modid;
        this.identifier = identifier;
        this.set = set;
    }

    @Override
    public void handlePacketClient(Context context)
    {
    }

    @Override
    public void handlePacketServer(Context context)
    {
        context.enqueueWork(() -> {
            if (InventoryManager.checkIdentifier(modid, identifier))
            {
                ModObjects.invMan.getCosArmorInventory(context.getSender().getUniqueID()).setHidden(modid, identifier, set);
            }
        });
    }

    @Override
    public void readFromBuffer(PacketBuffer buffer)
    {
        modid = buffer.readString(Short.MAX_VALUE);
        identifier = buffer.readString(Short.MAX_VALUE);
        set = buffer.readBoolean();
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer)
    {
        buffer.writeString(modid, Short.MAX_VALUE);
        buffer.writeString(identifier, Short.MAX_VALUE);
        buffer.writeBoolean(set);
    }

}
