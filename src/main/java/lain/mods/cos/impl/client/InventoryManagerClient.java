package lain.mods.cos.impl.client;

import java.util.UUID;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lain.mods.cos.impl.InventoryManager;
import lain.mods.cos.impl.inventory.InventoryCosArmor;

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
    public InventoryCosArmor getCosArmorInventoryClient(UUID uuid)
    {
        if (uuid == null)
            return Dummy;
        return ClientCache.getUnchecked(uuid);
    }

    @Override
    public void registerEventsClient()
    {
        Hacks.addClientDisconnectionCallback(() -> ClientCache.invalidateAll());
    }

}
