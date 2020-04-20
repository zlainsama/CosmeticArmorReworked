package lain.mods.cos.impl.client;

import java.util.UUID;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lain.mods.cos.impl.InventoryManager;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class InventoryManagerClient extends InventoryManager
{

    protected final LoadingCache<UUID, InventoryCosArmor> ClientCache = CacheBuilder.newBuilder().build(new CacheLoader<UUID, InventoryCosArmor>()
    {

        @Override
        public InventoryCosArmor load(UUID key) throws Exception
        {
            return new InventoryCosArmor();
        }

    });

    @Override
    public ContainerCosArmor createContainerClient(int windowId, PlayerInventory invPlayer, PacketBuffer extraData)
    {
        PlayerEntity player = LogicalSidedProvider.INSTANCE.<Minecraft>get(LogicalSide.CLIENT).player;
        return new ContainerCosArmor(invPlayer, getCosArmorInventoryClient(player.getUniqueID()), player, windowId);
    }

    @Override
    public InventoryCosArmor getCosArmorInventoryClient(UUID uuid)
    {
        if (uuid == null)
            return Dummy;
        return ClientCache.getUnchecked(uuid);
    }

    private void handleLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event)
    {
        ClientCache.invalidateAll();
    }

    @Override
    public void registerEventsClient()
    {
        MinecraftForge.EVENT_BUS.addListener(this::handleLoggedOut);
    }

}
