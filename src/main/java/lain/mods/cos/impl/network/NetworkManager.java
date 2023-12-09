package lain.mods.cos.impl.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class NetworkManager {

    protected final SimpleChannel channel;

    public NetworkManager(ResourceLocation name, String version) {
        if (name == null || version == null)
            throw new IllegalArgumentException();
        channel = NetworkRegistry.newSimpleChannel(name, () -> version, version::equals, version::equals);
    }

    public <T extends NetworkPacket> void registerPacket(int discriminator, Class<T> packetClass, Supplier<T> packetSupplier) {
        if (packetClass == null || packetSupplier == null || packetSupplier.get() == null)
            throw new IllegalArgumentException();
        channel.messageBuilder(packetClass, discriminator).decoder((b) -> {
            T p = packetSupplier.get();
            p.readFromBuffer(b);
            return p;
        }).encoder((p, b) -> {
            p.writeToBuffer(b);
        }).consumerNetworkThread((p, c) -> {
            switch (c.getDirection().getReceptionSide()) {
                case CLIENT:
                    p.handlePacketClient(c);
                    c.setPacketHandled(true);
                    break;
                case SERVER:
                    p.handlePacketServer(c);
                    c.setPacketHandled(true);
                    break;
                default:
                    break;
            }
        }).add();
    }

    public <T extends NetworkPacket> void send(T packet, PacketDistributor.PacketTarget target) {
        if (packet == null || target == null)
            throw new IllegalArgumentException();
        channel.send(target, packet);
    }

    public <T extends NetworkPacket> void sendTo(T packet, ServerPlayer player) {
        if (packet == null || player == null)
            throw new IllegalArgumentException();
        channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public <T extends NetworkPacket> void sendToAll(T packet) {
        if (packet == null)
            throw new IllegalArgumentException();
        channel.send(PacketDistributor.ALL.noArg(), packet);
    }

    public <T extends NetworkPacket> void sendToAllAround(T packet, PacketDistributor.TargetPoint point) {
        if (packet == null || point == null)
            throw new IllegalArgumentException();
        channel.send(PacketDistributor.NEAR.with(() -> point), packet);
    }

    public <T extends NetworkPacket> void sendToDimension(T packet, ResourceKey<Level> dimension) {
        if (packet == null || dimension == null)
            throw new IllegalArgumentException();
        channel.send(PacketDistributor.DIMENSION.with(() -> dimension), packet);
    }

    public <T extends NetworkPacket> void sendToServer(T packet) {
        if (packet == null)
            throw new IllegalArgumentException();
        channel.send(PacketDistributor.SERVER.noArg(), packet);
    }

    public interface NetworkPacket {

        void handlePacketClient(NetworkEvent.Context context);

        void handlePacketServer(NetworkEvent.Context context);

        void readFromBuffer(FriendlyByteBuf buffer);

        void writeToBuffer(FriendlyByteBuf buffer);

    }

}
