package com.postedsignage;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Nine persistent slots; held stacks are stored and the menu provides explicit disposal. */
public final class TrashBinBlockEntity extends BaseContainerBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

    public TrashBinBlockEntity(BlockPos pos, BlockState state) { super(PostedSignage.TRASH_BIN_ENTITY.get(), pos, state); }
    @Override public int getContainerSize() { return 9; }
    @Override protected NonNullList<ItemStack> getItems() { return items; }
    @Override protected void setItems(NonNullList<ItemStack> items) { this.items = items; }
    @Override protected Component getDefaultName() { return Component.translatable("block.posted_signage.trash_bin"); }
    @Override protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new TrashBinMenu(id, inventory, this);
    }
    @Override public ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = super.removeItemNoUpdate(slot);
        if (!removed.isEmpty()) setChanged();
        return removed;
    }
    @Override public void clearContent() { super.clearContent(); setChanged(); }
    public boolean canAccess(Player player) {
        return level != null && !level.isClientSide() && player.mayBuild() && stillValid(player)
                && level.mayInteract(player, worldPosition) && canOpen(player);
    }
    public int storeHeldItems(Player player, InteractionHand hand) {
        if (!canAccess(player)) return 0;
        ItemStack held = player.getItemInHand(hand);
        int before = held.getCount();
        // Fill compatible stacks first, then vacant slots. Never consume overflow.
        for (int pass = 0; pass < 2 && !held.isEmpty(); pass++) {
            for (int slot = 0; slot < getContainerSize() && !held.isEmpty(); slot++) {
                ItemStack stored = getItem(slot);
                if (pass == 0 && !stored.isEmpty() && ItemStack.isSameItemSameComponents(stored, held)) {
                    int moved = Math.min(held.getCount(), Math.max(0,
                            Math.min(getMaxStackSize(), stored.getMaxStackSize()) - stored.getCount()));
                    stored.grow(moved);
                    held.shrink(moved);
                } else if (pass == 1 && stored.isEmpty()) {
                    int moved = Math.min(held.getCount(), Math.min(getMaxStackSize(), held.getMaxStackSize()));
                    setItem(slot, held.split(moved));
                }
            }
        }
        int inserted = before - held.getCount();
        if (inserted > 0) {
            setChanged();
            player.getInventory().setChanged();
            player.containerMenu.broadcastChanges();
        }
        return inserted;
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(9, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
    }
}
