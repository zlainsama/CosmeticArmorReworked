package lain.mods.cos.client;

import java.util.concurrent.TimeUnit;
import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlayerRenderHandler
{

    private final LoadingCache<EntityPlayer, ItemStack[]> cache = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build(new CacheLoader<EntityPlayer, ItemStack[]>()
    {

        @Override
        public ItemStack[] load(EntityPlayer key) throws Exception
        {
            return new ItemStack[4];
        }

    });

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void handleCanceledEvent(RenderPlayerEvent.Pre event)
    {
        if (!event.isCanceled())
            return;

        ItemStack[] cachedArmor = cache.getUnchecked(event.entityPlayer);
        ItemStack[] armor = event.entityPlayer.inventory.armorInventory;

        if (armor == null || armor.length != 4)
            return; // Incompatible

        for (int i = 0; i < 4; i++)
        {
            if (cachedArmor[i] != null)
                armor[i] = cachedArmor[i];
            cachedArmor[i] = null;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void handleEvent(RenderPlayerEvent.Post event)
    {
        ItemStack[] cachedArmor = cache.getUnchecked(event.entityPlayer);
        ItemStack[] armor = event.entityPlayer.inventory.armorInventory;

        if (armor == null || armor.length != 4)
            return; // Incompatible

        for (int i = 0; i < 4; i++)
        {
            if (cachedArmor[i] != null)
                armor[i] = cachedArmor[i];
            cachedArmor[i] = null;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void handleEvent(RenderPlayerEvent.Pre event)
    {
        ItemStack[] cachedArmor = cache.getUnchecked(event.entityPlayer);
        ItemStack[] cosArmor = InventoryCosArmor.getCosArmor(event.entityPlayer);
        ItemStack[] armor = event.entityPlayer.inventory.armorInventory;

        if (armor == null || armor.length != 4)
            return; // Incompatible

        if (!InventoryCosArmor.isDisabled() && cosArmor != null)
        {
            for (int i = 0; i < 4; i++)
            {
                if (cachedArmor[i] != null)
                    armor[i] = cachedArmor[i];
                cachedArmor[i] = null;

                if (InventoryCosArmor.isSkinCosArmor(event.entityPlayer, i))
                {
                    cachedArmor[i] = armor[i];
                    armor[i] = null;
                }
                else if (cosArmor[i] != null)
                {
                    cachedArmor[i] = armor[i];
                    armor[i] = cosArmor[i];
                }
            }
        }
    }

}
