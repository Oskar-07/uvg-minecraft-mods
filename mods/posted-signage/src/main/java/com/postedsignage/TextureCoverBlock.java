package com.postedsignage;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.*;

/** A removable, non-colliding skin. FACING points away from its supporting face. */
public final class TextureCoverBlock extends BaseEntityBlock {
    public static final MapCodec<TextureCoverBlock> CODEC = simpleCodec(TextureCoverBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public TextureCoverBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new TextureCoverBlockEntity(pos, state); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction face = state.getValue(FACING);
        BlockPos support = pos.relative(face.getOpposite());
        return level.getBlockState(support).isFaceSturdy(level, support, face);
    }
    @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
    }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> box(0, 0, 15.98, 16, 16, 16);
            case SOUTH -> box(0, 0, 0, 16, 16, 0.02);
            case WEST -> box(15.98, 0, 0, 16, 16, 16);
            case EAST -> box(0, 0, 0, 0.02, 16, 16);
            case UP -> box(0, 0, 0, 16, 0.02, 16);
            case DOWN -> box(0, 15.98, 0, 16, 16, 16);
        };
    }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return rotate(state, mirror.getRotation(state.getValue(FACING))); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
}
