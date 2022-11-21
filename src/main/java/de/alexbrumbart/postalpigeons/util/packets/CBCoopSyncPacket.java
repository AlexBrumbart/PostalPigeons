package de.alexbrumbart.postalpigeons.util.packets;

import de.alexbrumbart.postalpigeons.util.MailReceptorStorage;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// Synchronises the contents of the server MailReceptorStorage to the client.
public class CBCoopSyncPacket {
    private final CompoundTag storageData;
    private final ResourceKey<Level> dimension;

    public CBCoopSyncPacket(CompoundTag storageData, ResourceKey<Level> dimension) {
        this.storageData = storageData;
        this.dimension = dimension;
    }

    public CBCoopSyncPacket(FriendlyByteBuf buf) {
        this.storageData = buf.readAnySizeNbt();
        this.dimension = buf.readResourceKey(Registry.DIMENSION_REGISTRY);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(storageData);
        buf.writeResourceKey(dimension);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> MailReceptorStorage.getClientInstanceUnsynced(dimension).refreshClientPositions(storageData));
        ctx.get().setPacketHandled(true);
    }
}
