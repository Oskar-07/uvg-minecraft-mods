package com.postedsignage;

import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;

/** Thin connected glass; arms meet without internal rails, corners retain a post. */
public final class CitGlassBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction.Axis> AXIS=BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty N=BlockStateProperties.NORTH,E=BlockStateProperties.EAST,S=BlockStateProperties.SOUTH,W=BlockStateProperties.WEST;
    public CitGlassBlock(Properties p){super(p.dynamicShape());registerDefaultState(stateDefinition.any().setValue(AXIS,Direction.Axis.X).setValue(N,false).setValue(E,false).setValue(S,false).setValue(W,false));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(CitGlassBlock::new);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState>b){b.add(AXIS,N,E,S,W);}
    public static BooleanProperty property(Direction d){return switch(d){case NORTH->N;case EAST->E;case SOUTH->S;default->W;};}
    public static boolean connects(BlockState neighbor,Direction d){
        if(neighbor.getBlock() instanceof CitGlassBlock)return true;
        return neighbor.getBlock() instanceof CitDoorBlock&&neighbor.getValue(CitDoorBlock.FACING).getAxis()!=d.getAxis();
    }
    private BlockState connections(BlockState s,LevelAccessor l,BlockPos p){for(Direction d:Direction.Plane.HORIZONTAL)s=s.setValue(property(d),connects(l.getBlockState(p.relative(d)),d));return s;}
    @Override protected void onPlace(BlockState s,Level l,BlockPos p,BlockState previous,boolean moving){
        if(!l.isClientSide&&!previous.is(this))l.setBlock(p,connections(s,l,p),UPDATE_ALL);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext c){return connections(defaultBlockState().setValue(AXIS,c.getHorizontalDirection().getClockWise().getAxis()),c.getLevel(),c.getClickedPos());}
    @Override protected BlockState updateShape(BlockState s,Direction d,BlockState n,LevelAccessor l,BlockPos p,BlockPos q){return connections(s,l,p);}
    public static Set<Direction> arms(BlockState s){
        Set<Direction> out=EnumSet.noneOf(Direction.class);
        for(Direction d:Direction.Plane.HORIZONTAL)if(s.getValue(property(d)))out.add(d);
        if(out.isEmpty()){out.add(s.getValue(AXIS)==Direction.Axis.X?Direction.EAST:Direction.NORTH);out.add(out.iterator().next().getOpposite());}
        else if(out.size()==1)out.add(out.iterator().next().getOpposite());
        return out;
    }
    private static boolean covers(BlockGetter l,BlockPos p,Direction arm){
        BlockState n=l.getBlockState(p);
        return n.getBlock() instanceof CitGlassBlock?arms(n).contains(arm):n.getBlock() instanceof CitDoorBlock&&n.getValue(CitDoorBlock.FACING).getAxis()!=arm.getAxis();
    }
    public static List<CitDoorGeometry.Piece> pieces(BlockState s,BlockGetter l,BlockPos p){
        List<CitDoorGeometry.Piece> out=new ArrayList<>();Set<Direction> arms=arms(s);
        for(Direction d:arms){
            boolean x=d.getAxis()==Direction.Axis.X;
            double lo=d==Direction.EAST||d==Direction.SOUTH?.5:0,hi=lo+.5;
            out.add(new CitDoorGeometry.Piece(x?new AABB(lo,0,.48,hi,1,.52):new AABB(.48,0,lo,.52,1,hi),true));
            if(!covers(l,p.above(),d))out.add(new CitDoorGeometry.Piece(x?new AABB(lo,.90,.40,hi,1,.60):new AABB(.40,.90,lo,.60,1,hi),false));
            if(!covers(l,p.below(),d))out.add(new CitDoorGeometry.Piece(x?new AABB(lo,0,.40,hi,.10,.60):new AABB(.40,0,lo,.60,.10,hi),false));
            if(!s.getValue(property(d))) {
                double a=lo==0?0:.90,b=lo==0?.10:1;
                out.add(new CitDoorGeometry.Piece(x?new AABB(a,0,.40,b,1,.60):new AABB(.40,0,a,.60,1,b),false));
            }
        }
        if(arms.stream().anyMatch(d->d.getAxis()==Direction.Axis.X)&&arms.stream().anyMatch(d->d.getAxis()==Direction.Axis.Z))
            out.add(new CitDoorGeometry.Piece(new AABB(.40,0,.40,.60,1,.60),false));
        return out;
    }
    @Override protected VoxelShape getShape(BlockState s,BlockGetter l,BlockPos p,CollisionContext c){VoxelShape shape=Shapes.empty();for(var piece:pieces(s,l,p))shape=Shapes.or(shape,Shapes.create(piece.box()));return shape;}
    @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new CitGlassEntity(p,s);}
    @Override protected RenderShape getRenderShape(BlockState s){return RenderShape.ENTITYBLOCK_ANIMATED;}
}
