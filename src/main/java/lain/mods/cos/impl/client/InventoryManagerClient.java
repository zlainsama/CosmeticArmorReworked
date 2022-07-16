package lain.mods.cos.impl.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lain.mods.cos.impl.InventoryManager;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.UUID;

public class InventoryManagerClient extends InventoryManager {

    protected final LoadingCache<UUID, InventoryCosArmor> ClientCache = CacheBuilder.newBuilder().build(new CacheLoader<UUID, InventoryCosArmor>() {

        @Override
        public InventoryCosArmor load(UUID key) throws Exception {
            return new InventoryCosArmor();
        }

    });

    @Override
    public ContainerCosArmor createContainerClient(int windowId, Inventory invPlayer, FriendlyByteBuf extraData) {
        Player player = Minecraft.getInstance().player;
        return new ContainerCosArmor(invPlayer, getCosArmorInventoryClient(player.getUUID()), player, windowId);
    }

    @Override
    public InventoryCosArmor getCosArmorInventoryClient(UUID uuid) {
        if (uuid == null)
            return Dummy;
        return ClientCache.getUnchecked(uuid);
    }

    private void handleLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientCache.invalidateAll();
    }

    @Override
    public void registerEventsClient() {
        MinecraftForge.EVENT_BUS.addListener(this::handleLoggedOut);
    }

}
