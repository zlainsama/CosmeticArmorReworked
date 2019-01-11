package lain.mods.cos.client;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import lain.mods.cos.InventoryManager;
import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

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

    Map<UUID, UUID> map = Maps.newHashMap();

    @Override
    public InventoryCosArmor getCosArmorInventoryClient(UUID uuid)
    {
        if (map.isEmpty())
        {
            Minecraft mc = FMLClientHandler.instance().getClient();
            if (mc.player != null)
                map.put(UUID.nameUUIDFromBytes(("OfflinePlayer:" + mc.player.getGameProfile().getName()).getBytes(StandardCharsets.UTF_8)), mc.player.getUniqueID());
        }
        if (map.containsKey(uuid))
            uuid = map.get(uuid);
        return cacheClient.getUnchecked(uuid);
    }

    @SubscribeEvent
    public void handleEvent(ClientDisconnectionFromServerEvent event)
    {
        PlayerRenderHandler.HideCosArmor = false;
        cacheClient.invalidateAll();
        map.clear();
    }

}
