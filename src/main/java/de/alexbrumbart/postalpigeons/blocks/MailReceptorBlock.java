package de.alexbrumbart.postalpigeons.blocks;

import de.alexbrumbart.postalpigeons.util.MailReceptorStorage;
import de.alexbrumbart.postalpigeons.util.NetworkHandler;
import de.alexbrumbart.postalpigeons.util.packets.CBMailReceptorPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class MailReceptorBlock extends Block implements EntityBlock {
    public MailReceptorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MailReceptorBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) level.getBlockEntity(pos));
        return InteractionResult.CONSUME;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide && placer != null)
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) placer), new CBMailReceptorPacket(pos));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) { // Method is only called on the server.
        MailReceptorBlockEntity receptor = (MailReceptorBlockEntity) level.getBlockEntity(pos);
        if (receptor == null)
            return;

        MailReceptorStorage.getInstance((ServerLevel) level).removePosition(receptor.getName());
        receptor.onRemove();
        level.removeBlockEntity(pos);
    }
}
