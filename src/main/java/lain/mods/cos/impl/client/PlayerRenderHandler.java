package lain.mods.cos.impl.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.ArrayDeque;
import java.util.Deque;

public enum PlayerRenderHandler {

    INSTANCE;

    public static boolean Disabled = false;

    private final LoadingCache<Object, Deque<Runnable>> cache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Object, Deque<Runnable>>() {

        @Override
        public Deque<Runnable> load(Object key) throws Exception {
            return new ArrayDeque<>();
        }

    });

    private void handleLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        Disabled = false;
    }

    private void handlePreRenderPlayer_High(RenderPlayerEvent.Pre event) {
        Player player = event.getPlayer();
        Deque<Runnable> queue = cache.getUnchecked(player);
        restoreItems(queue);
        NonNullList<ItemStack> armor = player.getInventory().armor;

        for (int i = 0; i < armor.size(); i++) {
            int slot = i;
            ItemStack stack = armor.get(slot);
            queue.add(() -> armor.set(slot, stack));
        }

        if (Disabled)
            return;

        InventoryCosArmor invCosArmor = ModObjects.invMan.getCosArmorInventoryClient(player.getUUID());
        ItemStack stack;
        for (int i = 0; i < armor.size(); i++) {
            if (invCosArmor.isSkinArmor(i))
                armor.set(i, ItemStack.EMPTY);
            else if (!(stack = invCosArmor.getStackInSlot(i)).isEmpty())
                armor.set(i, stack);
        }
    }

    private void handlePostRenderPlayer_Low(RenderPlayerEvent.Post event) {
        restoreItems(cache.getUnchecked(event.getPlayer()));
    }

    private void handlePreRenderPlayer_LowestCanceled(RenderPlayerEvent.Pre event) {
        if (!event.isCanceled())
            return;

        restoreItems(cache.getUnchecked(event.getPlayer()));
    }

    private void handleRenderHand_High(RenderHandEvent event) {
        Player player = Minecraft.getInstance().player;
        Deque<Runnable> queue = cache.getUnchecked(player);
        restoreItems(queue);
        NonNullList<ItemStack> armor = player.getInventory().armor;

        for (int i = 0; i < armor.size(); i++) {
            int slot = i;
            ItemStack stack = armor.get(slot);
            queue.add(() -> armor.set(slot, stack));
        }

        if (Disabled)
            return;

        InventoryCosArmor invCosArmor = ModObjects.invMan.getCosArmorInventoryClient(player.getUUID());
        ItemStack stack;
        for (int i = 0; i < armor.size(); i++) {
            if (invCosArmor.isSkinArmor(i))
                armor.set(i, ItemStack.EMPTY);
            else if (!(stack = invCosArmor.getStackInSlot(i)).isEmpty())
                armor.set(i, stack);
        }
    }

    private void handleRenderHand_LowestCanceled(RenderHandEvent event) {
        restoreItems(cache.getUnchecked(Minecraft.getInstance().player));
    }

    private void handleRenderArm_High(RenderArmEvent event) {
        Player player = Minecraft.getInstance().player;
        Deque<Runnable> queue = cache.getUnchecked(player);
        restoreItems(queue);
        NonNullList<ItemStack> armor = player.getInventory().armor;

        for (int i = 0; i < armor.size(); i++) {
            int slot = i;
            ItemStack stack = armor.get(slot);
            queue.add(() -> armor.set(slot, stack));
        }

        if (Disabled)
            return;

        InventoryCosArmor invCosArmor = ModObjects.invMan.getCosArmorInventoryClient(player.getUUID());
        ItemStack stack;
        for (int i = 0; i < armor.size(); i++) {
            if (invCosArmor.isSkinArmor(i))
                armor.set(i, ItemStack.EMPTY);
            else if (!(stack = invCosArmor.getStackInSlot(i)).isEmpty())
                armor.set(i, stack);
        }
    }

    private void handleRenderArm_LowestCanceled(RenderArmEvent event) {
        restoreItems(cache.getUnchecked(Minecraft.getInstance().player));
    }

    public void registerEvents() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::handlePreRenderPlayer_High);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::handlePostRenderPlayer_Low);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, true, this::handlePreRenderPlayer_LowestCanceled);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::handleRenderHand_High);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, true, this::handleRenderHand_LowestCanceled);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::handleRenderArm_High);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, true, this::handleRenderArm_LowestCanceled);
        MinecraftForge.EVENT_BUS.addListener(this::handleLoggedOut);
    }

    private void restoreItems(Deque<Runnable> queue) {
        Runnable runnable;
        while ((runnable = queue.poll()) != null) {
            try {
                runnable.run();
            } catch (Throwable e) {
                ModObjects.logger.error("Failed in restoring client player items", e);
            }
        }
    }

}
