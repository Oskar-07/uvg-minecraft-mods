package com.postedsignage.client;

import com.postedsignage.TrashBinMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class TrashBinScreen extends AbstractContainerScreen<TrashBinMenu> {
    private Button emptyButton;
    public TrashBinScreen(TrashBinMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageHeight = 170;
        inventoryLabelY = 76;
    }
    @Override protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
        emptyButton = addRenderableWidget(Button.builder(
                Component.translatable("screen.posted_signage.bin_empty"), button -> {
                    if (minecraft != null && minecraft.gameMode != null)
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
                }).bounds(leftPos + 8, topPos + 44, 160, 20)
                .tooltip(Tooltip.create(Component.translatable("screen.posted_signage.bin_empty_tooltip"))).build());
        updateButton();
    }
    private void updateButton() {
        emptyButton.active = menu.slots.subList(0, 9).stream().anyMatch(slot -> slot.hasItem());
    }
    @Override protected void containerTick() { super.containerTick(); updateButton(); }
    @Override protected void renderBg(GuiGraphics graphics, float tick, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF373C3A);
        graphics.fill(leftPos + 2, topPos + 2, leftPos + imageWidth - 2, topPos + imageHeight - 2, 0xFFD7DDD9);
        for (var slot : menu.slots) {
            int x = leftPos + slot.x, y = topPos + slot.y;
            graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xFF606863);
            graphics.fill(x, y, x + 16, y + 16, 0xFFAFB8B2);
        }
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float tick) {
        super.render(graphics, mouseX, mouseY, tick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
