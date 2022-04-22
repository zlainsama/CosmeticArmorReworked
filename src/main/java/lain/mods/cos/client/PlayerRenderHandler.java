package lain.mods.cos.client;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lain.mods.cos.CosmeticArmorReworked;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class PlayerRenderHandler
{

    public static boolean HideCosArmor = false;

    private final ItemStack[] EMPTY = new ItemStack[0];

    private final LoadingCache<EntityPlayer, ItemStack[]> cache = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build(new CacheLoader<EntityPlayer, ItemStack[]>()
    {

        @Override
        public ItemStack[] load(EntityPlayer owner) throws Exception
        {
            return EMPTY;
        }

    });

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void handleCanceledEvent(RenderHandEvent event)
    {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null)
            return;

        restoreIfCached(player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void handleCanceledEvent(RenderPlayerEvent.Pre event)
    {
        if (!event.isCanceled())
            return;

        restoreIfCached(event.entityPlayer);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void handleEvent(RenderHandEvent event)
    {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null)
            return;

        restoreIfCached(player);

        if (HideCosArmor)
            return;

        ItemStack[] armor = player.inventory.armorInventory;
        cache.put(player, Arrays.copyOf(armor, armor.length));

        ItemStack[] cosArmor = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(player.getUniqueID()).getInventory();

        if (cosArmor != null)
        {
            for (int i = 0; i < cosArmor.length; i++)
            {
                if (CosmeticArmorReworked.invMan.getCosArmorInventoryClient(player.getUniqueID()).isSkinArmor(i))
                    armor[i] = null;
                else if (cosArmor[i] != null)
                    armor[i] = cosArmor[i];
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void handleEvent(RenderPlayerEvent.Post event)
    {
        restoreIfCached(event.entityPlayer);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void handleEvent(RenderPlayerEvent.Pre event)
    {
        restoreIfCached(event.entityPlayer);

        if (HideCosArmor)
            return;

        ItemStack[] armor = event.entityPlayer.inventory.armorInventory;
        cache.put(event.entityPlayer, Arrays.copyOf(armor, armor.length));

        ItemStack[] cosArmor = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(event.entityPlayer.getUniqueID()).getInventory();

        if (cosArmor != null)
        {
            for (int i = 0; i < cosArmor.length; i++)
            {
                if (CosmeticArmorReworked.invMan.getCosArmorInventoryClient(event.entityPlayer.getUniqueID()).isSkinArmor(i))
                    armor[i] = null;
                else if (cosArmor[i] != null)
                    armor[i] = cosArmor[i];
            }
        }
    }

    private void restoreIfCached(EntityPlayer player)
    {
        ItemStack[] cachedArmor = cache.getUnchecked(player);
        if (cachedArmor != EMPTY)
        {
            ItemStack[] armor = player.inventory.armorInventory;
            if (armor != null && cachedArmor.length == armor.length)
            {
                for (int i = 0; i < cachedArmor.length; i++)
                    armor[i] = cachedArmor[i];
                cache.put(player, EMPTY);
            }
        }
    }

}
