package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class PacketOpenCosArmorInventory implements NetworkPacket
{

    int windowId;

    public PacketOpenCosArmorInventory()
    {
        this.windowId = -1;
    }

    public PacketOpenCosArmorInventory(int windowId)
    {
        this.windowId = windowId;
    }

    @Override
    public void handlePacketClient()
    {
        if (windowId == -1)
            return;
        IThreadListener scheduler = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.CLIENT);
        if (!scheduler.isCallingFromMinecraftThread())
        {
            scheduler.addScheduledTask(() -> handlePacketClient());
        }
        else
        {
            Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
            GuiCosArmorInventory newGui = new GuiCosArmorInventory(new ContainerCosArmor(mc.player.inventory, ModObjects.invMan.getCosArmorInventoryClient(mc.player.getUniqueID()), mc.player));
            GuiScreen gui = mc.currentScreen;
            if (gui instanceof GuiInventory)
            {
                newGui.oldMouseX = ((GuiInventory) gui).oldMouseX;
                newGui.oldMouseY = ((GuiInventory) gui).oldMouseY;
            }
            mc.displayGuiScreen(newGui);
            mc.player.openContainer.windowId = windowId;
        }
    }

    @Override
    public void handlePacketServer(EntityPlayerMP player)
    {
        if (windowId != -1)
            return;
        IThreadListener scheduler = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
        if (!scheduler.isCallingFromMinecraftThread())
        {
            scheduler.addScheduledTask(() -> handlePacketServer(player));
        }
        else
        {
            if (!(player.openContainer instanceof ContainerPlayer))
                player.openContainer.onContainerClosed(player);
            player.getNextWindowId();
            ModObjects.network.sendTo(new PacketOpenCosArmorInventory(player.currentWindowId), player);
            player.openContainer = new ContainerCosArmor(player.inventory, ModObjects.invMan.getCosArmorInventory(player.getUniqueID()), player);
            player.openContainer.windowId = player.currentWindowId;
            player.openContainer.addListener(player);
            MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
        }
    }

    @Override
    public void readFromBuffer(PacketBuffer buffer)
    {
        windowId = buffer.readByte();
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer)
    {
        buffer.writeByte(windowId);
    }

}
