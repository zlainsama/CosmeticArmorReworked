package lain.mods.cos.impl.network.packet;

import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.NetworkManager.NetworkPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.UUID;

public class PacketSyncCosArmor implements NetworkPacket {

    UUID uuid;
    int slot;
    boolean isSkinArmor;
    ItemStack itemCosArmor;

    public PacketSyncCosArmor() {
    }

    public PacketSyncCosArmor(UUID uuid, InventoryCosArmor inventory, int slot) {
        if (uuid == null)
            throw new IllegalArgumentException();
        this.uuid = uuid;
        this.slot = slot;
        this.isSkinArmor = inventory.isSkinArmor(slot);
        this.itemCosArmor = inventory.getStackInSlot(slot);
    }

    @Override
    public void handlePacketClient(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ModObjects.invMan.getCosArmorInventoryClient(uuid).setStackInSlot(slot, itemCosArmor);
            ModObjects.invMan.getCosArmorInventoryClient(uuid).setSkinArmor(slot, isSkinArmor);
        });
    }

    @Override
    public void handlePacketServer(CustomPayloadEvent.Context context) {
    }

    @Override
    public void readFromBuffer(RegistryFriendlyByteBuf buffer) {
        uuid = new UUID(buffer.readLong(), buffer.readLong());
        slot = buffer.readByte();
        isSkinArmor = buffer.readBoolean();
        itemCosArmor = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
    }

    @Override
    public void writeToBuffer(RegistryFriendlyByteBuf buffer) {
        buffer.writeLong(uuid.getMostSignificantBits());
        buffer.writeLong(uuid.getLeastSignificantBits());
        buffer.writeByte(slot);
        buffer.writeBoolean(isSkinArmor);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, itemCosArmor);
    }

}
