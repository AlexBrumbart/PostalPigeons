package de.alexbrumbart.postalpigeons.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class PigeonCoopBlock extends Block implements EntityBlock {
    private static final DirectionProperty facing = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape shape_north = Stream.of(Block.box(2, 0, 2, 14, 1, 14), Block.box(1, 0, 1, 3, 13.75, 3), Block.box(13, 0, 1, 15, 13.75, 3), Block.box(1, 0, 13, 3, 9.25, 15), Block.box(13, 0, 13, 15, 9.25, 15),
            Block.box(0, 7, 12, 16, 11, 16), Block.box(0, 13, 0, 16, 17, 4), Block.box(0, 11, 4, 16, 15, 8), Block.box(0, 9, 8, 16, 13, 12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape shape_south = Stream.of(Block.box(2, 0, 2, 14, 1, 14), Block.box(1, 0, 13, 3, 13.75, 15), Block.box(13, 0, 13, 15, 13.75, 15), Block.box(1, 0, 1, 3, 9.25, 3), Block.box(13, 0, 1, 15, 9.25, 3),
            Block.box(0, 7, 0, 16, 11, 4), Block.box(0, 13, 12, 16, 17, 16), Block.box(0, 11, 8, 16, 15, 12), Block.box(0, 9, 4, 16, 13, 8)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape shape_west = Stream.of(Block.box(2, 0, 2, 14, 1, 14), Block.box(1, 0, 1, 3, 13.75, 3), Block.box(1, 0, 13, 3, 13.75, 15), Block.box(13, 0, 1, 15, 9.25, 3), Block.box(13, 0, 13, 15, 9.25, 15),
            Block.box(12, 7, 0, 16, 11, 16), Block.box(0, 13, 0, 4, 17, 16), Block.box(4, 11, 0, 8, 15, 16), Block.box(8, 9, 0, 12, 13, 16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape shape_east = Stream.of(Block.box(2, 0, 2, 14, 1, 14), Block.box(13, 0, 1, 15, 13.75, 3), Block.box(13, 0, 13, 15, 13.75, 15), Block.box(1, 0, 1, 3, 9.25, 3), Block.box(1, 0, 13, 3, 9.25, 15),
            Block.box(0, 7, 0, 4, 11, 16), Block.box(12, 13, 0, 16, 17, 16), Block.box(8, 11, 0, 12, 15, 16), Block.box(4, 9, 0, 8, 13, 16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public PigeonCoopBlock(Properties properties) {
        super(properties);

        registerDefaultState(getStateDefinition().any().setValue(facing, Direction.NORTH));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PigeonCoopBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(facing)) {
            case NORTH -> shape_north;
            case SOUTH -> shape_south;
            case WEST -> shape_west;
            case EAST -> shape_east;
            default -> throw new IllegalStateException();
        };
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(facing, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(facing, rotation.rotate(state.getValue(facing)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(facing)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(facing);
    }
}
