package lain.mods.cos.impl.client;

import java.util.ArrayDeque;
import java.util.Deque;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.capability.ICurioItemHandler;
import top.theillusivec4.curios.api.inventory.CurioStackHandler;

public enum PlayerRenderHandler
{

    INSTANCE;

    private static final boolean isCuriosLoaded = ModList.get().isLoaded("curios");

    public static boolean Disabled = false;

    private LoadingCache<Object, Deque<Runnable>> cache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Object, Deque<Runnable>>()
    {

        @Override
        public Deque<Runnable> load(Object key) throws Exception
        {
            return new ArrayDeque<>();
        }

    });

    private void handleLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event)
    {
        Disabled = false;
    }

    private void handlePostRenderPlayer_Low(RenderPlayerEvent.Post event)
    {
        restoreItems(cache.getUnchecked(event.getPlayer()));
    }

    private void handlePreRenderPlayer_High(RenderPlayerEvent.Pre event)
    {
        PlayerEntity player = event.getPlayer();
        Deque<Runnable> queue = cache.getUnchecked(player);
        restoreItems(queue);
        NonNullList<ItemStack> armor = player.inventory.armorInventory;

        for (int i = 0; i < armor.size(); i++)
        {
            int slot = i;
            ItemStack stack = armor.get(slot);
            queue.add(() -> armor.set(slot, stack));
        }

        if (isCuriosLoaded)
        {
            CuriosAPI.getCuriosHandler(player).map(ICurioItemHandler::getCurioMap).ifPresent(map -> {
                for (String id : map.keySet())
                {
                    CurioStackHandler handler = map.get(id);
                    for (int i = 0; i < handler.getSlots(); i++)
                    {
                        int slot = i;
                        ItemStack stack = handler.getStackInSlot(slot);
                        queue.add(() -> handler.setStackInSlot(slot, stack));
                    }
                }
            });
        }

        if (Disabled)
            return;

        InventoryCosArmor invCosArmor = ModObjects.invMan.getCosArmorInventoryClient(player.getUniqueID());
        ItemStack stack;
        for (int i = 0; i < armor.size(); i++)
        {
            if (invCosArmor.isSkinArmor(i))
                armor.set(i, ItemStack.EMPTY);
            else if (!(stack = invCosArmor.getStackInSlot(i)).isEmpty())
                armor.set(i, stack);
        }

        if (isCuriosLoaded)
        {
            CuriosAPI.getCuriosHandler(player).map(ICurioItemHandler::getCurioMap).ifPresent(map -> {
                for (String id : map.keySet())
                {
                    CurioStackHandler handler = map.get(id);
                    for (int slot = 0; slot < handler.getSlots(); slot++)
                    {
                        String identifier = id + "#" + slot;
                        if (invCosArmor.isHidden("curios", identifier))
                            handler.setStackInSlot(slot, ItemStack.EMPTY);
                    }
                }
            });
        }
    }

    private void handlePreRenderPlayer_LowestCanceled(RenderPlayerEvent.Pre event)
    {
        if (!event.isCanceled())
            return;

        restoreItems(cache.getUnchecked(event.getPlayer()));
    }

    public void registerEvents()
    {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::handlePostRenderPlayer_Low);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::handlePreRenderPlayer_High);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, true, this::handlePreRenderPlayer_LowestCanceled);
        MinecraftForge.EVENT_BUS.addListener(this::handleLoggedOut);
    }

    private void restoreItems(Deque<Runnable> queue)
    {
        Runnable runnable;
        while ((runnable = queue.poll()) != null)
        {
            try
            {
                runnable.run();
            }
            catch (Throwable e)
            {
                ModObjects.logger.error("Failed in restoring client player items", e);
            }
        }
    }

}
