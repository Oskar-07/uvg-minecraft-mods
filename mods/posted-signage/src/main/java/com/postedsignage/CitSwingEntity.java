package com.postedsignage;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public final class CitSwingEntity extends BlockEntity {
    private BlockPos owner;
    public CitSwingEntity(BlockPos p,BlockState s){super(PostedSignage.CIT_SWING_ENTITY.get(),p,s);}
    public BlockPos owner(){return owner;}
    public void setOwner(BlockPos p){owner=p.immutable();setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2);}
    @Override protected void saveAdditional(CompoundTag t,HolderLookup.Provider r){super.saveAdditional(t,r);if(owner!=null)t.putLong("Owner",owner.asLong());}
    @Override protected void loadAdditional(CompoundTag t,HolderLookup.Provider r){super.loadAdditional(t,r);owner=t.contains("Owner")?BlockPos.of(t.getLong("Owner")):null;}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider r){return saveWithoutMetadata(r);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
