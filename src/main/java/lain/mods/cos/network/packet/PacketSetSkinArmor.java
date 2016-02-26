package lain.mods.cos.network.packet;

import io.netty.buffer.ByteBuf;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.InventoryCosArmor;
import lain.mods.cos.network.NetworkPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

public class PacketSetSkinArmor extends NetworkPacket
{

    int slot;
    boolean isSkinArmor;

    public PacketSetSkinArmor()
    {
    }

    public PacketSetSkinArmor(EntityPlayer player, int slot)
    {
        this.slot = slot;
        this.isSkinArmor = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(player.getUniqueID()).isSkinArmor(slot);
    }

    @Override
    public void handlePacketClient()
    {
    }

    @Override
    public void handlePacketServer(EntityPlayerMP player)
    {
        InventoryCosArmor inv = CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID());
        inv.setSkinArmor(slot, isSkinArmor);
        inv.markDirty();
    }

    @Override
    public void readFromBuffer(ByteBuf buf)
    {
        PacketBuffer pb = new PacketBuffer(buf);

        slot = pb.readByte();
        isSkinArmor = pb.readBoolean();
    }

    @Override
    public void writeToBuffer(ByteBuf buf)
    {
        PacketBuffer pb = new PacketBuffer(buf);

        pb.writeByte(slot);
        pb.writeBoolean(isSkinArmor);
    }

}
