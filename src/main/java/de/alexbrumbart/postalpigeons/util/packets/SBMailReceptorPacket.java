package de.alexbrumbart.postalpigeons.util.packets;

import de.alexbrumbart.postalpigeons.blocks.MailReceptorBlockEntity;
import de.alexbrumbart.postalpigeons.util.MailReceptorStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// Used to set the wanted mail receptor name on the server.
public class SBMailReceptorPacket {
    private final String name;
    private final BlockPos pos;

    public SBMailReceptorPacket(String name, BlockPos pos) {
        this.name = name;
        this.pos = pos;
    }

    public SBMailReceptorPacket(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readBlockPos());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ServerLevel level = (ServerLevel) player.level;

                if (level == level.getServer().overworld() && level.isLoaded(pos) && level.getBlockEntity(pos) instanceof MailReceptorBlockEntity tile) {
                    if (MailReceptorStorage.getInstance(level).getPosition(name) != null) {
                        String alternativeName = pos.toString();

                        MailReceptorStorage.getInstance(level).setPosition(alternativeName, pos);
                        tile.setName(alternativeName);
                    } else {
                        MailReceptorStorage.getInstance(level).setPosition(name, pos);
                        tile.setName(name);
                    }

                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
