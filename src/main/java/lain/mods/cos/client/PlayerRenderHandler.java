package lain.mods.cos.client;

import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerRenderHandler
{

    private static class CachedInventory
    {

        NonNullList<ItemStack> stacks;
        int state;

        CachedInventory(int size)
        {
            stacks = NonNullList.withSize(size, ItemStack.EMPTY);
            state = 0;
        }

    }

    public static boolean HideCosArmor = false;

    private static final boolean isBaublesLoaded = Loader.isModLoaded("baubles");

    private final LoadingCache<EntityPlayer, CachedInventory> cache = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build(new CacheLoader<EntityPlayer, CachedInventory>()
    {

        @Override
        public CachedInventory load(EntityPlayer owner) throws Exception
        {
            return new CachedInventory(owner.inventory.armorInventory.size() + (isBaublesLoaded ? (InventoryCosArmor.MinSize - 4) : 0));
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

        if (armor.size() > cachedArmor.size())
        {
            cache.invalidate(event.getEntityPlayer());
            return; // Something went wrong, recommend a reconnection
        }

        if (cachedInv.state != 0)
        {
            for (int i = 0; i < armor.size(); i++)
                armor.set(i, cachedArmor.get(i));

            if (isBaublesLoaded)
            {
                try
                {
                    IBaublesItemHandler bh = BaublesApi.getBaublesHandler(event.getEntityPlayer());
                    boolean block = bh.isEventBlocked();
                    bh.setEventBlock(true);
                    for (int i = 0; i < bh.getSlots(); i++)
                        bh.setStackInSlot(i, cachedArmor.get(4 + i));
                    bh.setEventBlock(block);
                }
                catch (Throwable ignored)
                {
                }
            }

            cachedInv.state = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void handleEvent(RenderPlayerEvent.Post event)
    {
        CachedInventory cachedInv = cache.getUnchecked(event.getEntityPlayer());
        NonNullList<ItemStack> cachedArmor = cachedInv.stacks;
        NonNullList<ItemStack> armor = event.getEntityPlayer().inventory.armorInventory;

        if (armor.size() > cachedArmor.size())
        {
            cache.invalidate(event.getEntityPlayer());
            return; // Something went wrong, recommend a reconnection
        }

        if (cachedInv.state != 0)
        {
            for (int i = 0; i < armor.size(); i++)
                armor.set(i, cachedArmor.get(i));

            if (isBaublesLoaded)
            {
                try
                {
                    IBaublesItemHandler bh = BaublesApi.getBaublesHandler(event.getEntityPlayer());
                    boolean block = bh.isEventBlocked();
                    bh.setEventBlock(true);
                    for (int i = 0; i < bh.getSlots(); i++)
                        bh.setStackInSlot(i, cachedArmor.get(4 + i));
                    bh.setEventBlock(block);
                }
                catch (Throwable ignored)
                {
                }
            }

            cachedInv.state = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void handleEvent(RenderPlayerEvent.Pre event)
    {
        CachedInventory cachedInv = cache.getUnchecked(event.getEntityPlayer());
        NonNullList<ItemStack> cachedArmor = cachedInv.stacks;
        InventoryCosArmor cosArmor = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(event.getEntityPlayer().getUniqueID());
        NonNullList<ItemStack> armor = event.getEntityPlayer().inventory.armorInventory;

        if (armor.size() > cachedArmor.size())
        {
            cache.invalidate(event.getEntityPlayer());
            return; // Something went wrong, recommend a reconnection
        }

        if (cachedInv.state != 0)
        {
            for (int i = 0; i < armor.size(); i++)
                armor.set(i, cachedArmor.get(i));

            if (isBaublesLoaded)
            {
                try
                {
                    IBaublesItemHandler bh = BaublesApi.getBaublesHandler(event.getEntityPlayer());
                    boolean block = bh.isEventBlocked();
                    bh.setEventBlock(true);
                    for (int i = 0; i < bh.getSlots(); i++)
                        bh.setStackInSlot(i, cachedArmor.get(4 + i));
                    bh.setEventBlock(block);
                }
                catch (Throwable ignored)
                {
                }
            }

            cachedInv.state = 0;
        }

        for (int i = 0; i < armor.size(); i++)
            cachedArmor.set(i, armor.get(i));

        if (isBaublesLoaded)
        {
            try
            {
                IBaublesItemHandler bh = BaublesApi.getBaublesHandler(event.getEntityPlayer());
                for (int i = 0; i < bh.getSlots(); i++)
                    cachedArmor.set(4 + i, bh.getStackInSlot(i));
            }
            catch (Throwable ignored)
            {
            }
        }

        cachedInv.state = 1;

        if (HideCosArmor)
            return;

        for (int i = 0; i < armor.size(); i++)
        {
            if (i < 4)
            {
                if (cosArmor.isSkinArmor(i))
                    armor.set(i, ItemStack.EMPTY);
                else if (!cosArmor.getStackInSlot(i).isEmpty())
                    armor.set(i, cosArmor.getStackInSlot(i));
            }
        }

        if (isBaublesLoaded)
        {
            try
            {
                IBaublesItemHandler bh = BaublesApi.getBaublesHandler(event.getEntityPlayer());
                boolean block = bh.isEventBlocked();
                bh.setEventBlock(true);
                for (int i = 0; i < bh.getSlots(); i++)
                {
                    if (cosArmor.isSkinArmor(4 + i))
                        bh.setStackInSlot(i, ItemStack.EMPTY);
                    else if (!cosArmor.getStackInSlot(4 + i).isEmpty())
                        bh.setStackInSlot(i, cosArmor.getStackInSlot(4 + i));
                }
                bh.setEventBlock(block);
            }
            catch (Throwable ignored)
            {
            }
        }
    }

}
