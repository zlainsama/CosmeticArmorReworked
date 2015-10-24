package lain.mods.cos.client;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lain.mods.cos.InventoryManager;
import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class InventoryManagerClient extends InventoryManager
{

    LoadingCache<UUID, InventoryCosArmor> cacheClient = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build(new CacheLoader<UUID, InventoryCosArmor>()
    {

        @Override
        public InventoryCosArmor load(UUID key) throws Exception
        {
            return new InventoryCosArmor();
        }

    });

    @Override
    public InventoryCosArmor getCosArmorInventory(EntityPlayer player)
    {
        if (player instanceof AbstractClientPlayer)
            return cacheClient.getUnchecked(player.getUniqueID());
        return super.getCosArmorInventory(player);
    }

    @SubscribeEvent
    public void handleEvent(ClientConnectedToServerEvent event)
    {
        PlayerRenderHandler.HideCosArmor = false;
        cacheClient.invalidateAll();
    }

}
