package com.postedsignage;

import java.util.UUID;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Nine persistent slots; disposal requires two explicit empty-hand crouch interactions. */
public final class TrashBinBlockEntity extends BaseContainerBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
    private UUID confirmingPlayer;
    private long confirmUntil;

    public TrashBinBlockEntity(BlockPos pos, BlockState state) { super(PostedSignage.TRASH_BIN_ENTITY.get(), pos, state); }
    @Override public int getContainerSize() { return 9; }
    @Override protected NonNullList<ItemStack> getItems() { return items; }
    @Override protected void setItems(NonNullList<ItemStack> items) { this.items = items; }
    @Override protected Component getDefaultName() { return Component.translatable("block.posted_signage.trash_bin"); }
    @Override protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        cancelConfirmation();
        return new DispenserMenu(id, inventory, this);
    }
    private void cancelConfirmation() { confirmingPlayer = null; confirmUntil = 0; }
    @Override public void setChanged() { cancelConfirmation(); super.setChanged(); }
    @Override public ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = super.removeItemNoUpdate(slot);
        if (!removed.isEmpty()) setChanged();
        return removed;
    }
    @Override public void clearContent() { super.clearContent(); setChanged(); }
    public boolean confirmEmpty(Player player) {
        if (level == null || level.isClientSide() || !player.mayBuild() || !stillValid(player)
                || !level.mayInteract(player, worldPosition) || !canOpen(player)
                || !player.isShiftKeyDown() || !player.getMainHandItem().isEmpty()
                || !player.getOffhandItem().isEmpty()) return false;
        if (isEmpty()) { cancelConfirmation(); return false; }
        long now = level.getGameTime();
        if (player.getUUID().equals(confirmingPlayer) && now <= confirmUntil) {
            clearContent();
            setChanged();
            player.displayClientMessage(Component.translatable("message.posted_signage.bin_emptied"), true);
            return true;
        }
        confirmingPlayer = player.getUUID();
        confirmUntil = now + 100;
        player.displayClientMessage(Component.translatable("message.posted_signage.bin_confirm"), true);
        return false;
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        cancelConfirmation();
    }
}
