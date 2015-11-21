package lain.mods.cos.client;

import java.util.concurrent.TimeUnit;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.PlayerUtils;
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

    public static boolean HideCosArmor = false;

    private final LoadingCache<EntityPlayer, ItemStack[]> cache = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build(new CacheLoader<EntityPlayer, ItemStack[]>()
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
            armor[i] = cachedArmor[i];
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void handleEvent(RenderPlayerEvent.Post event)
    {
        ItemStack[] cachedArmor = cache.getUnchecked(event.entityPlayer);
        ItemStack[] armor = event.entityPlayer.inventory.armorInventory;

        if (armor == null || armor.length != cachedArmor.length)
            return; // Incompatible

        for (int i = 0; i < cachedArmor.length; i++)
            armor[i] = cachedArmor[i];
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void handleEvent(RenderPlayerEvent.Pre event)
    {
        ItemStack[] cachedArmor = cache.getUnchecked(event.entityPlayer);
        ItemStack[] cosArmor = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(PlayerUtils.getPlayerID(event.entityPlayer)).getInventory();
        ItemStack[] armor = event.entityPlayer.inventory.armorInventory;

        if (armor == null || armor.length != cachedArmor.length)
            return; // Incompatible

        for (int i = 0; i < cachedArmor.length; i++)
            cachedArmor[i] = armor[i];

        if (HideCosArmor)
            return;

        if (cosArmor != null)
        {
            for (int i = 0; i < cachedArmor.length; i++)
            {
                if (CosmeticArmorReworked.invMan.getCosArmorInventoryClient(PlayerUtils.getPlayerID(event.entityPlayer)).isSkinArmor(i))
                    armor[i] = null;
                else if (cosArmor[i] != null)
                    armor[i] = cosArmor[i];
            }
        }
    }

}
