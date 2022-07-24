package de.alexbrumbart.postalpigeons.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class PigeonCoopBlock extends Block implements EntityBlock {
    private static final VoxelShape shape = Stream.of(Block.box(2, 0, 2, 14, 1, 13), Block.box(1, 0, 1, 3, 13.75D, 3), Block.box(13, 0, 1, 15, 13.75D, 3), Block.box(1, 0, 12, 3, 9.25D, 14), Block.box(13, 0, 12, 15, 9.25D, 14),
            Block.box(0, 7, 12, 16, 11, 16), Block.box(0, 13, 0, 16, 17, 4), Block.box(0, 11, 4, 16, 15, 8), Block.box(0, 9, 8, 16, 13, 12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public PigeonCoopBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PigeonCoopBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;

        NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) level.getBlockEntity(pos), pos);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (level.getBlockEntity(pos) instanceof PigeonCoopBlockEntity tile) {
            tile.onRemove();
            level.removeBlockEntity(pos);
        }
    }
}
