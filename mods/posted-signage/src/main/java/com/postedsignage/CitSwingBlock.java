package com.postedsignage;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
public final class CitSwingBlock extends BaseEntityBlock {
    public CitSwingBlock(Properties p){super(p.dynamicShape());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(CitSwingBlock::new);}
    @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new CitSwingEntity(p,s);}
    @Override protected RenderShape getRenderShape(BlockState s){return RenderShape.INVISIBLE;}
    @Override protected VoxelShape getShape(BlockState s,BlockGetter l,BlockPos p,CollisionContext c){
        CitDoorEntity e=CitDoorEntity.find(l,p);return e==null?Shapes.empty():e.leafCells(true).getOrDefault(p,Shapes.empty());
    }
    @Override protected InteractionResult useWithoutItem(BlockState s,Level l,BlockPos p,Player player,BlockHitResult hit){
        CitDoorEntity e=CitDoorEntity.find(l,p);
        if(e!=null)return ((CitDoorBlock)e.getBlockState().getBlock()).useWithoutItem(e.getBlockState(),l,e.getBlockPos(),player,hit);
        return InteractionResult.PASS;
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState s,Level l,BlockPos p,Player player,InteractionHand hand,BlockHitResult hit){useWithoutItem(s,l,p,player,hit);return ItemInteractionResult.sidedSuccess(l.isClientSide);}
    @Override public BlockState playerWillDestroy(Level l,BlockPos p,BlockState s,Player player){CitDoorEntity e=CitDoorEntity.find(l,p);if(!l.isClientSide&&e!=null)e.dismantle(!player.isCreative());return super.playerWillDestroy(l,p,s,player);}
    @Override protected void onRemove(BlockState s,Level l,BlockPos p,BlockState n,boolean moving){if(!s.is(n.getBlock())&&!l.isClientSide){CitDoorEntity e=CitDoorEntity.find(l,p);if(e!=null)e.dismantle(true);}super.onRemove(s,l,p,n,moving);}
}
