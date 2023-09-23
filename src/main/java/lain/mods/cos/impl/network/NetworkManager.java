package lain.mods.cos.impl.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.util.function.Supplier;

public class NetworkManager {

    protected final SimpleChannel channel;

    public NetworkManager(ResourceLocation name, int version) {
        if (name == null || version < 0)
            throw new IllegalArgumentException();
        channel = ChannelBuilder.named(name).networkProtocolVersion(version).acceptedVersions(Channel.VersionTest.exact(version)).simpleChannel();
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
        channel.send(packet, target);
    }

    public <T extends NetworkPacket> void sendTo(T packet, ServerPlayer player) {
        if (packet == null || player == null)
            throw new IllegalArgumentException();
        channel.send(packet, PacketDistributor.PLAYER.with(player));
    }

    public <T extends NetworkPacket> void sendToAll(T packet) {
        if (packet == null)
            throw new IllegalArgumentException();
        channel.send(packet, PacketDistributor.ALL.noArg());
    }

    public <T extends NetworkPacket> void sendToAllAround(T packet, PacketDistributor.TargetPoint point) {
        if (packet == null || point == null)
            throw new IllegalArgumentException();
        channel.send(packet, PacketDistributor.NEAR.with(point));
    }

    public <T extends NetworkPacket> void sendToDimension(T packet, ResourceKey<Level> dimension) {
        if (packet == null || dimension == null)
            throw new IllegalArgumentException();
        channel.send(packet, PacketDistributor.DIMENSION.with(dimension));
    }

    public <T extends NetworkPacket> void sendToServer(T packet) {
        if (packet == null)
            throw new IllegalArgumentException();
        channel.send(packet, PacketDistributor.SERVER.noArg());
    }

    public interface NetworkPacket {

        void handlePacketClient(CustomPayloadEvent.Context context);

        void handlePacketServer(CustomPayloadEvent.Context context);

        void readFromBuffer(FriendlyByteBuf buffer);

        void writeToBuffer(FriendlyByteBuf buffer);

    }

}
