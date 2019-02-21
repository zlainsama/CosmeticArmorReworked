package lain.mods.cos.impl.client;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import io.netty.channel.ChannelFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

class Hacks
{

    private enum Ticker
    {

        INSTANCE;

        Set<ChannelFuture> KnownFutures = Collections.newSetFromMap(new WeakHashMap<ChannelFuture, Boolean>());
        Set<Runnable> ClientDisconnectionCallbacks = ConcurrentHashMap.newKeySet();

        Ticker()
        {
            MinecraftForge.EVENT_BUS.addListener(this::tickClient);
        }

        // Note: this only gets called after client connected to a server
        void tickClient(TickEvent.ClientTickEvent event)
        {
            if (event.phase != Phase.START)
                return;
            NetHandlerPlayClient handler;
            if ((handler = LogicalSidedProvider.INSTANCE.<Minecraft>get(LogicalSide.CLIENT).getConnection()) != null && handler.getNetworkManager().isChannelOpen())
            {
                ChannelFuture future;
                if (!KnownFutures.contains(future = handler.getNetworkManager().channel().closeFuture()))
                    KnownFutures.add(future.addListener(f -> ClientDisconnectionCallbacks.forEach(Runnable::run)));
            }
        }

    }

    // TODO move to use events when relevant events are back
    static void addClientDisconnectionCallback(Runnable callback)
    {
        Ticker.INSTANCE.ClientDisconnectionCallbacks.add(callback);
    }

}
