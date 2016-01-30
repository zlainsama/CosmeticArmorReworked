package lain.mods.cos.client;

import java.util.UUID;
import lain.mods.cos.InventoryManager;
import lain.mods.cos.PlayerUtils;
import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.client.Minecraft;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cpw.mods.fml.client.FMLClientHandler;
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

    boolean forceCached = false;

    @Override
    public InventoryCosArmor getCosArmorInventoryClient(UUID uuid)
    {
        if (!forceCached)
        {
            Minecraft mc = FMLClientHandler.instance().getClient();
            if (mc.thePlayer != null)
            {
                PlayerUtils.getPlayerID(mc.thePlayer); // This will make sure the client has offline info for the current user
                forceCached = true;
            }
        }
        return cacheClient.getUnchecked(PlayerUtils.getOfflineID(uuid));
    }

    @SubscribeEvent
    public void handleEvent(ClientDisconnectionFromServerEvent event)
    {
        PlayerRenderHandler.HideCosArmor = false;
        cacheClient.invalidateAll();
        forceCached = false;
    }

}
