package de.alexbrumbart.postalpigeons.util.packets;

import de.alexbrumbart.postalpigeons.util.MailReceptorStorage;
import de.alexbrumbart.postalpigeons.util.NetworkHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Objects;
import java.util.function.Supplier;

// Requests synchronising the MailReceptorStorage from server to client.
public class SBCoopSyncPacket {
    private final long clientVersion;
    private final ResourceKey<Level> dimension;

    public SBCoopSyncPacket(long clientVersion, ResourceKey<Level> dimension) {
        this.clientVersion = clientVersion;
        this.dimension = dimension;
    }

    public SBCoopSyncPacket(FriendlyByteBuf buf) {
        this.clientVersion = buf.readLong();
        this.dimension = buf.readResourceKey(Registries.DIMENSION);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(clientVersion);
        buf.writeResourceKey(dimension);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = Objects.requireNonNull(ctx.get().getSender());
            MailReceptorStorage serverStorage = MailReceptorStorage.getInstance(Objects.requireNonNull(player.server.getLevel(dimension)));
            if (serverStorage.getVersion() != clientVersion)
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new CBCoopSyncPacket(serverStorage.save(new CompoundTag()), dimension));
        });

        ctx.get().setPacketHandled(true);
    }
}
