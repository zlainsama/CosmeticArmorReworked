package lain.mods.cos.client;

import java.util.UUID;
import lain.mods.cos.InventoryManager;
import lain.mods.cos.PlayerUtils;
import lain.mods.cos.inventory.InventoryCosArmor;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class InventoryManagerClient extends InventoryManager
{

    LoadingCache<UUID, InventoryCosArmor> cacheClient = CacheBuilder.newBuilder().build(new CacheLoader<UUID, InventoryCosArmor>()
    {

        @Override
        public InventoryCosArmor load(UUID owner) throws Exception
        {
            return new InventoryCosArmor();
        }

    });

    @Override
    public InventoryCosArmor getCosArmorInventoryClient(UUID uuid)
    {
        return cacheClient.getUnchecked(PlayerUtils.getOfflineID(uuid));
    }

    @SubscribeEvent
    public void handleEvent(ClientDisconnectionFromServerEvent event)
    {
        PlayerRenderHandler.HideCosArmor = false;
        cacheClient.invalidateAll();
    }

}
