package de.alexbrumbart.postalpigeons.util.packets;

import de.alexbrumbart.postalpigeons.util.MailReceptorStorage;
import de.alexbrumbart.postalpigeons.util.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Objects;
import java.util.function.Supplier;

// Requests synchronising the MailReceptorStorage from server to client.
public class SBCoopSyncPacket {
    private final long clientVersion;

    public SBCoopSyncPacket(long clientVersion) {
        this.clientVersion = clientVersion;
    }

    public SBCoopSyncPacket(FriendlyByteBuf buf) {
        this.clientVersion = buf.readLong();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(clientVersion);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = Objects.requireNonNull(ctx.get().getSender());
            MailReceptorStorage serverStorage = MailReceptorStorage.getInstance(player.getLevel());
            if (serverStorage.getVersion() != clientVersion)
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new CBCoopSyncPacket(serverStorage.save(new CompoundTag())));
        });

        ctx.get().setPacketHandled(true);
    }
}
