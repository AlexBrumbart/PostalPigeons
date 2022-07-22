package de.alexbrumbart.postalpigeons.util.packets;

import de.alexbrumbart.postalpigeons.blocks.PigeonCoopBlockEntity;
import de.alexbrumbart.postalpigeons.util.MailReceptorStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SBPigeonGoalPacket {
    private final String name;
    private final BlockPos coopPos;

    public SBPigeonGoalPacket(String name, BlockPos coopPos) {
        this.name = name;
        this.coopPos = coopPos;
    }

    public SBPigeonGoalPacket(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readBlockPos());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeBlockPos(coopPos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            assert player != null;

            MailReceptorStorage storage = MailReceptorStorage.getInstance((ServerLevel) player.level);
            if (storage.hasEntry(name) && player.level.getBlockEntity(coopPos) instanceof PigeonCoopBlockEntity tile) {
                tile.sendPigeon(storage.getPosition(name));
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
