package de.alexbrumbart.postalpigeons.util.packets;

import de.alexbrumbart.postalpigeons.rendering.RenderingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// Used to open the naming screen for the mail receptor on the client.
public class CBMailReceptorPacket {
    private final BlockPos pos;

    public CBMailReceptorPacket(BlockPos pos) {
        this.pos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public CBMailReceptorPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RenderingUtils.openMailReceptorNamingScreen(pos)));
        ctx.get().setPacketHandled(true);
    }
}
