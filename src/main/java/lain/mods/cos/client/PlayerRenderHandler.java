package lain.mods.cos.client;

import java.util.concurrent.TimeUnit;
import lain.mods.cos.CosmeticArmorReworked;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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

        NonNullList<ItemStack> stacks;
        int state;

        CachedInventory(int size)
        {
            stacks = NonNullList.func_191197_a(size, ItemStack.field_190927_a);
            state = 0;
        }

    }

    public static boolean HideCosArmor = false;

    private final LoadingCache<EntityPlayer, CachedInventory> cache = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build(new CacheLoader<EntityPlayer, CachedInventory>()
    {

        @Override
        public CachedInventory load(EntityPlayer owner) throws Exception
        {
            return new CachedInventory(owner.inventory.armorInventory.size());
        }

    });

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void handleCanceledEvent(RenderPlayerEvent.Pre event)
    {
        if (!event.isCanceled())
            return;

        CachedInventory cachedInv = cache.getUnchecked(event.getEntityPlayer());
        NonNullList<ItemStack> cachedArmor = cachedInv.stacks;
        NonNullList<ItemStack> armor = event.getEntityPlayer().inventory.armorInventory;

        if (armor == null || armor.size() != cachedArmor.size())
            return; // Incompatible

        if (cachedInv.state != 0)
        {
            for (int i = 0; i < cachedArmor.size(); i++)
                armor.set(i, cachedArmor.get(i));
            cachedInv.state = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void handleEvent(RenderPlayerEvent.Post event)
    {
        CachedInventory cachedInv = cache.getUnchecked(event.getEntityPlayer());
        NonNullList<ItemStack> cachedArmor = cachedInv.stacks;
        NonNullList<ItemStack> armor = event.getEntityPlayer().inventory.armorInventory;

        if (armor == null || armor.size() != cachedArmor.size())
            return; // Incompatible

        if (cachedInv.state != 0)
        {
            for (int i = 0; i < cachedArmor.size(); i++)
                armor.set(i, cachedArmor.get(i));
            cachedInv.state = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void handleEvent(RenderPlayerEvent.Pre event)
    {
        CachedInventory cachedInv = cache.getUnchecked(event.getEntityPlayer());
        NonNullList<ItemStack> cachedArmor = cachedInv.stacks;
        NonNullList<ItemStack> cosArmor = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(event.getEntityPlayer().getUniqueID()).getInventory();
        NonNullList<ItemStack> armor = event.getEntityPlayer().inventory.armorInventory;

        if (armor == null || armor.size() != cachedArmor.size())
            return; // Incompatible

        if (cachedInv.state != 0)
        {
            for (int i = 0; i < cachedArmor.size(); i++)
                armor.set(i, cachedArmor.get(i));
            cachedInv.state = 0;
        }

        for (int i = 0; i < cachedArmor.size(); i++)
            cachedArmor.set(i, armor.get(i));
        cachedInv.state = 1;

        if (HideCosArmor)
            return;

        if (cosArmor != null)
        {
            for (int i = 0; i < cachedArmor.size(); i++)
            {
                if (i >= cosArmor.size())
                    break;

                if (CosmeticArmorReworked.invMan.getCosArmorInventoryClient(event.getEntityPlayer().getUniqueID()).isSkinArmor(i))
                    armor.set(i, ItemStack.field_190927_a);
                else if (!cosArmor.get(i).func_190926_b())
                    armor.set(i, cosArmor.get(i));
            }
        }
    }
}
