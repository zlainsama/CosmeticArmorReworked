package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.GuiHandler;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent.Context;

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
    public void handlePacketClient(Context context)
    {
        if (windowId == -1)
            return;
        context.enqueueWork(() -> {
            Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
            mc.displayGuiScreen(GuiHandler.INSTANCE.buildGui(InventoryCosArmor.GuiID));
            mc.player.openContainer.windowId = windowId;
        });
    }

    @Override
    public void handlePacketServer(Context context)
    {
        if (windowId != -1)
            return;
        context.enqueueWork(() -> {
            EntityPlayerMP player = context.getSender();
            player.closeContainer();
            player.getNextWindowId();
            player.openContainer = ModObjects.invMan.getCosArmorInventory(player.getUniqueID()).createContainer(player.inventory, player);
            player.openContainer.windowId = player.currentWindowId;
            player.openContainer.addListener(player);
            MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
            ModObjects.network.sendTo(new PacketOpenCosArmorInventory(player.currentWindowId), player);
        });
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
