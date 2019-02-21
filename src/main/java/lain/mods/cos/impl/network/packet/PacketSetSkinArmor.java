package lain.mods.cos.impl.network.packet;

import java.util.UUID;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class PacketSetSkinArmor implements NetworkPacket
{

    int slot;
    boolean isSkinArmor;

    public PacketSetSkinArmor()
    {
    }

    public PacketSetSkinArmor(UUID uuid, int slot)
    {
        if (uuid == null)
            throw new IllegalArgumentException();
        this.slot = slot;
        this.isSkinArmor = ModObjects.invMan.getCosArmorInventoryClient(uuid).isSkinArmor(slot);
    }

    @Override
    public void handlePacketClient()
    {
    }

    @Override
    public void handlePacketServer(EntityPlayerMP player)
    {
        IThreadListener scheduler = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
        if (!scheduler.isCallingFromMinecraftThread())
        {
            scheduler.addScheduledTask(() -> handlePacketServer(player));
        }
        else
        {
            ModObjects.invMan.getCosArmorInventory(player.getUniqueID()).setSkinArmor(slot, isSkinArmor);
        }
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
