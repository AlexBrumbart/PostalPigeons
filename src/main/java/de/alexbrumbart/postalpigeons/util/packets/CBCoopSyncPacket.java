package de.alexbrumbart.postalpigeons.util.packets;

import de.alexbrumbart.postalpigeons.util.MailReceptorStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// Synchronises the contents of the server MailReceptorStorage to the client.
public class CBCoopSyncPacket {
    private final CompoundTag storageData;

    public CBCoopSyncPacket(CompoundTag storageData) {
        this.storageData = storageData;
    }

    public CBCoopSyncPacket(FriendlyByteBuf buf) {
        this.storageData = buf.readAnySizeNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(storageData);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> MailReceptorStorage.getClientInstance().refreshClientPositions(storageData));
        ctx.get().setPacketHandled(true);
    }
}
