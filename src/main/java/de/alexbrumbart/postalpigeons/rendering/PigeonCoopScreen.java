package de.alexbrumbart.postalpigeons.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.alexbrumbart.postalpigeons.PostalPigeons;
import de.alexbrumbart.postalpigeons.blocks.PigeonCoopContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PigeonCoopScreen extends AbstractContainerScreen<PigeonCoopContainer> {
    private static final ResourceLocation texture = new ResourceLocation(PostalPigeons.ID, "textures/gui/pigeon_coop.png");
    private static final Component sendPigeonComponent = Component.translatable("postalpigeons.container.pigeon_coop.send");

    private EditBox editBox;

    public PigeonCoopScreen(PigeonCoopContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = 256;
        this.imageHeight = 251;
    }

    @Override
    protected void init() {
        super.init();

        editBox = addRenderableWidget(new EditBox(Minecraft.getInstance().font, leftPos + 86, topPos + 74, 165, 20, editBox, Component.empty()));
        addRenderableWidget(new Button(leftPos + 118, topPos + 100, 100, 20, sendPigeonComponent, b -> { /* TODO Taube senden */ }));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);

        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        font.draw(poseStack, title, titleLabelX, titleLabelY, 4210752);
        font.draw(poseStack, playerInventoryTitle, 49, 157, 4210752);
        font.draw(poseStack, Component.translatable("postalpigeons.container.pigeon_coop.pigeons", menu.getPigeonAmount()), 88, 17, 4210752);
        font.draw(poseStack, Component.translatable("postalpigeons.container.pigeon_coop.available", menu.getRemainingPigeonAmount(), menu.getPigeonAmount()), 88, 28, 4210752);
    }
}
