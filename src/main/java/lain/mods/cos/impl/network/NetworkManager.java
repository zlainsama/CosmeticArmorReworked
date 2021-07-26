package lain.mods.cos.impl.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class NetworkManager {

    protected final SimpleChannel channel;

    public NetworkManager(ResourceLocation name, String version) {
        if (name == null || version == null)
            throw new IllegalArgumentException();
        channel = NetworkRegistry.newSimpleChannel(name, () -> version, version::equals, version::equals);
    }

    public NetworkManager(ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        if (name == null || networkProtocolVersion == null || clientAcceptedVersions == null || serverAcceptedVersions == null)
            throw new IllegalArgumentException();
        channel = NetworkRegistry.newSimpleChannel(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
    }

    public <T extends NetworkPacket> void registerPacket(int discriminator, Class<T> packetClass, Supplier<T> packetSupplier) {
        if (packetClass == null || packetSupplier == null || packetSupplier.get() == null)
            throw new IllegalArgumentException();
        channel.registerMessage(discriminator, packetClass, (p, b) -> {
            p.writeToBuffer(b);
        }, (b) -> {
            T p = packetSupplier.get();
            p.readFromBuffer(b);
            return p;
        }, (p, s) -> {
            switch (s.get().getDirection().getReceptionSide()) {
                case CLIENT:
                    p.handlePacketClient(s.get());
                    s.get().setPacketHandled(true);
                    break;
                case SERVER:
                    p.handlePacketServer(s.get());
                    s.get().setPacketHandled(true);
                    break;
                default:
                    break;
            }
        });
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
