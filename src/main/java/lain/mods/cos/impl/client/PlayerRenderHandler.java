package lain.mods.cos.impl.client;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Pair;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public enum PlayerRenderHandler
{

    INSTANCE;

    public static boolean Disabled = false;

    private static final int MAXSIZE = 11;

    private static void disconnect(ITextComponent message)
    {
        Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
        mc.getConnection().handleDisconnect(new SDisconnectPacket(message));
        mc.getConnection().onDisconnect(message);
    }

    private LoadingCache<Object, Pair<NonNullList<ItemStack>, MutableInt>> cache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<Object, Pair<NonNullList<ItemStack>, MutableInt>>()
    {

        @Override
        public Pair<NonNullList<ItemStack>, MutableInt> load(Object key) throws Exception
        {
            return Pair.of(NonNullList.withSize(MAXSIZE, ItemStack.EMPTY), new MutableInt());
        }

    });

    private void handlePostRenderPlayer_High(RenderPlayerEvent.Post event)
    {
        try
        {
            PlayerEntity player = event.getPlayer();
            Pair<NonNullList<ItemStack>, MutableInt> cached = cache.getUnchecked(player);
            NonNullList<ItemStack> armorCached = cached.getLeft();
            NonNullList<ItemStack> armor = player.inventory.armorInventory;
            if (cached.getRight().intValue() != 0)
            {
                for (int i = 0; i < armor.size(); i++)
                    armor.set(i, armorCached.get(i));
                // TODO add baubles integration
                cached.getRight().setValue(0);
            }
        }
        catch (Throwable t)
        {
            disconnect(new StringTextComponent("Something went wrong during restoration after player rendering, it should be fine to reconnect."));
        }
    }

    private void handlePreRenderPlayer_High(RenderPlayerEvent.Pre event)
    {
        try
        {
            PlayerEntity player = event.getPlayer();
            Pair<NonNullList<ItemStack>, MutableInt> cached = cache.getUnchecked(player);
            NonNullList<ItemStack> armorCached = cached.getLeft();
            NonNullList<ItemStack> armor = player.inventory.armorInventory;
            if (cached.getRight().intValue() != 0)
            {
                for (int i = 0; i < armor.size(); i++)
                    armor.set(i, armorCached.get(i));
                // TODO add baubles integration
                cached.getRight().setValue(0);
            }

            for (int i = 0; i < armor.size(); i++)
                armorCached.set(i, armor.get(i));
            cached.getRight().setValue(1);
            // TODO add baubles integration

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
            // TODO add baubles integration
        }
        catch (Throwable t)
        {
            disconnect(new StringTextComponent("Something went wrong during preparation for player rendering, it should be fine to reconnect."));
        }
    }

    private void handlePreRenderPlayer_LowestCanceled(RenderPlayerEvent.Pre event)
    {
        if (!event.isCanceled())
            return;
        try
        {
            PlayerEntity player = event.getPlayer();
            Pair<NonNullList<ItemStack>, MutableInt> cached = cache.getUnchecked(player);
            NonNullList<ItemStack> armorCached = cached.getLeft();
            NonNullList<ItemStack> armor = player.inventory.armorInventory;
            if (cached.getRight().intValue() != 0)
            {
                for (int i = 0; i < armor.size(); i++)
                    armor.set(i, armorCached.get(i));
                // TODO add baubles integration
                cached.getRight().setValue(0);
            }
        }
        catch (Throwable t)
        {
            disconnect(new StringTextComponent("Something went wrong during restoration for canceled player rendering, it should be fine to reconnect."));
        }
    }

    public void registerEvents()
    {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::handlePostRenderPlayer_High);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::handlePreRenderPlayer_High);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, true, this::handlePreRenderPlayer_LowestCanceled);
        Hacks.addClientDisconnectionCallback(() -> Disabled = false);
    }

}
