package lain.mods.cos.network.packet;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.UUID;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.InventoryCosArmor;
import lain.mods.cos.network.NetworkPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class PacketSyncCosArmor extends NetworkPacket
{

    UUID uuid;
    int slot;
    boolean isSkinArmor;
    ItemStack itemCosArmor;

    public PacketSyncCosArmor()
    {
    }

    public PacketSyncCosArmor(EntityPlayer player, int slot)
    {
        this.uuid = player.getUniqueID();
        this.slot = slot;
        this.isSkinArmor = CosmeticArmorReworked.invMan.getCosArmorInventory(this.uuid).isSkinArmor(slot);
        this.itemCosArmor = CosmeticArmorReworked.invMan.getCosArmorInventory(this.uuid).getStackInSlot(slot);
    }

    @Override
    public void handlePacketClient()
    {
        InventoryCosArmor inv = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(uuid);
        inv.setInventorySlotContents(slot, itemCosArmor);
        inv.setSkinArmor(slot, isSkinArmor);
        inv.markDirty();
    }

    @Override
    public void handlePacketServer(EntityPlayerMP player)
    {
    }

    @Override
    public void readFromBuffer(ByteBuf buf)
    {
        PacketBuffer pb = new PacketBuffer(buf);

        uuid = new UUID(pb.readLong(), pb.readLong());
        slot = pb.readByte();
        isSkinArmor = pb.readBoolean();
        try
        {
            itemCosArmor = pb.readItemStack();
        }
        catch (IOException ignored)
        {
        }
    }

    @Override
    public void writeToBuffer(ByteBuf buf)
    {
        PacketBuffer pb = new PacketBuffer(buf);

        pb.writeLong(uuid.getMostSignificantBits());
        pb.writeLong(uuid.getLeastSignificantBits());
        pb.writeByte(slot);
        pb.writeBoolean(isSkinArmor);
        pb.writeItemStack(itemCosArmor);
    }

}
