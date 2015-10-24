package lain.mods.cos.client;

import java.util.concurrent.TimeUnit;
import lain.mods.cos.CosmeticArmorReworked;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlayerRenderHandler
{

    public static boolean HideCosArmor = false;

    private final LoadingCache<EntityPlayer, ItemStack[]> cache = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).removalListener(new RemovalListener<EntityPlayer, ItemStack[]>()
    {

        @Override
        public void onRemoval(RemovalNotification<EntityPlayer, ItemStack[]> notification)
        {
            EntityPlayer owner = notification.getKey();
            ItemStack[] cachedArmor = notification.getValue();

            if (owner == null || cachedArmor == null)
                return;

            ItemStack[] armor = owner.inventory.armorInventory;

            if (armor == null || armor.length != cachedArmor.length)
                return; // Incompatible

            for (int i = 0; i < cachedArmor.length; i++)
            {
                if (cachedArmor[i] != null)
                    armor[i] = cachedArmor[i];
                cachedArmor[i] = null;
            }
        }

    }).build(new CacheLoader<EntityPlayer, ItemStack[]>()
    {

        @Override
        public ItemStack[] load(EntityPlayer owner) throws Exception
        {
            return new ItemStack[owner.inventory.armorInventory.length];
        }

    });

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void handleCanceledEvent(RenderPlayerEvent.Pre event)
    {
        if (!event.isCanceled())
            return;

        ItemStack[] cachedArmor = cache.getUnchecked(event.entityPlayer);
        ItemStack[] armor = event.entityPlayer.inventory.armorInventory;

        if (armor == null || armor.length != cachedArmor.length)
            return; // Incompatible

        for (int i = 0; i < cachedArmor.length; i++)
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

        if (armor == null || armor.length != cachedArmor.length)
            return; // Incompatible

        for (int i = 0; i < cachedArmor.length; i++)
        {
            if (cachedArmor[i] != null)
                armor[i] = cachedArmor[i];
            cachedArmor[i] = null;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void handleEvent(RenderPlayerEvent.Pre event)
    {
        if (HideCosArmor)
            return;

        ItemStack[] cachedArmor = cache.getUnchecked(event.entityPlayer);
        ItemStack[] cosArmor = CosmeticArmorReworked.invMan.getCosArmor(event.entityPlayer);
        ItemStack[] armor = event.entityPlayer.inventory.armorInventory;

        if (armor == null || armor.length != cachedArmor.length)
            return; // Incompatible

        if (cosArmor != null)
        {
            for (int i = 0; i < cachedArmor.length; i++)
            {
                if (cachedArmor[i] != null)
                    armor[i] = cachedArmor[i];
                cachedArmor[i] = null;

                if (CosmeticArmorReworked.invMan.isSkinArmor(event.entityPlayer, i))
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
