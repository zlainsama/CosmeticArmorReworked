package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class PacketOpenNormalInventory implements NetworkPacket
{

    public PacketOpenNormalInventory()
    {
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
            if (!(player.openContainer instanceof ContainerPlayer))
                player.openContainer.onContainerClosed(player);
            player.openContainer = player.inventoryContainer;
        }
    }

    @Override
    public void readFromBuffer(PacketBuffer buffer)
    {
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer)
    {
    }

}
