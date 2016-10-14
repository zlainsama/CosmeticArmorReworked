package lain.mods.cos.client;

import java.util.concurrent.TimeUnit;
import lain.mods.cos.CosmeticArmorReworked;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class PlayerRenderHandler
{

    private static class CachedInventory
    {

        ItemStack[] stacks;
        int state;

        CachedInventory(int size)
        {
            stacks = new ItemStack[size];
            state = 0;
        }

    }

    public static boolean HideCosArmor = false;

    private final LoadingCache<EntityPlayer, CachedInventory> cache = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build(new CacheLoader<EntityPlayer, CachedInventory>()
    {

        @Override
        public CachedInventory load(EntityPlayer owner) throws Exception
        {
            return new CachedInventory(owner.inventory.armorInventory.length);
        }

    });

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void handleCanceledEvent(RenderPlayerEvent.Pre event)
    {
        if (!event.isCanceled())
            return;

        CachedInventory cachedInv = cache.getUnchecked(event.getEntityPlayer());
        ItemStack[] cachedArmor = cachedInv.stacks;
        ItemStack[] armor = event.getEntityPlayer().inventory.armorInventory;

        if (armor == null || armor.length != cachedArmor.length)
            return; // Incompatible

        if (cachedInv.state != 0)
        {
            for (int i = 0; i < cachedArmor.length; i++)
                armor[i] = cachedArmor[i];
            cachedInv.state = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void handleEvent(RenderPlayerEvent.Post event)
    {
        CachedInventory cachedInv = cache.getUnchecked(event.getEntityPlayer());
        ItemStack[] cachedArmor = cachedInv.stacks;
        ItemStack[] armor = event.getEntityPlayer().inventory.armorInventory;

        if (armor == null || armor.length != cachedArmor.length)
            return; // Incompatible

        if (cachedInv.state != 0)
        {
            for (int i = 0; i < cachedArmor.length; i++)
                armor[i] = cachedArmor[i];
            cachedInv.state = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void handleEvent(RenderPlayerEvent.Pre event)
    {
        CachedInventory cachedInv = cache.getUnchecked(event.getEntityPlayer());
        ItemStack[] cachedArmor = cachedInv.stacks;
        ItemStack[] cosArmor = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(event.getEntityPlayer().getUniqueID()).getInventory();
        ItemStack[] armor = event.getEntityPlayer().inventory.armorInventory;

        if (armor == null || armor.length != cachedArmor.length)
            return; // Incompatible

        if (cachedInv.state != 0)
        {
            for (int i = 0; i < cachedArmor.length; i++)
                armor[i] = cachedArmor[i];
            cachedInv.state = 0;
        }

        for (int i = 0; i < cachedArmor.length; i++)
            cachedArmor[i] = armor[i];
        cachedInv.state = 1;

        if (HideCosArmor)
            return;

        if (cosArmor != null)
        {
            for (int i = 0; i < cachedArmor.length; i++)
            {
                if (i >= cosArmor.length)
                    break;

                if (CosmeticArmorReworked.invMan.getCosArmorInventoryClient(event.getEntityPlayer().getUniqueID()).isSkinArmor(i))
                    armor[i] = null;
                else if (cosArmor[i] != null)
                    armor[i] = cosArmor[i];
            }
        }
    }

}
