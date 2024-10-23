package lain.mods.cos.impl.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;

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

    public void onExtractPlayerRenderState(AbstractClientPlayer player, PlayerRenderState state, float partialTicks) {
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

    public void onFinishPlayerRenderState(AbstractClientPlayer player, PlayerRenderState state, float partialTicks) {
        restoreItems(cache.getUnchecked(player));
    }

    private void handleLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        Disabled = false;
    }

    public void registerEvents() {
        NeoForge.EVENT_BUS.addListener(this::handleLoggedOut);
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
