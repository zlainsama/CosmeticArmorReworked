package lain.mods.cos.network.packet;

import io.netty.buffer.ByteBuf;
import lain.mods.cos.network.NetworkPacket;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketOpenNormalInventory extends NetworkPacket
{

    @Override
    public void handlePacketClient()
    {
    }

    @Override
    public void handlePacketServer(EntityPlayerMP player)
    {
        player.openContainer.onContainerClosed(player);
        player.openContainer = player.inventoryContainer;
    }

    @Override
    public void readFromBuffer(ByteBuf buf)
    {
    }

    @Override
    public void writeToBuffer(ByteBuf buf)
    {
    }

}
