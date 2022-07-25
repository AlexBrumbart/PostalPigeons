package de.alexbrumbart.postalpigeons.rendering;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.alexbrumbart.postalpigeons.ModRegistries;
import de.alexbrumbart.postalpigeons.PostalPigeons;
import de.alexbrumbart.postalpigeons.blocks.PigeonCoopContainer;
import de.alexbrumbart.postalpigeons.entity.Pigeon;
import de.alexbrumbart.postalpigeons.util.NetworkHandler;
import de.alexbrumbart.postalpigeons.util.packets.SBPigeonGoalPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Objects;

public class PigeonCoopScreen extends AbstractContainerScreen<PigeonCoopContainer> {
    private static final ResourceLocation texture = new ResourceLocation(PostalPigeons.ID, "textures/gui/pigeon_coop.png");
    private static final Component sendPigeonComponent = Component.translatable("postalpigeons.container.pigeon_coop.send");

    private EditBox editBox;
    private Pigeon pigeon;

    public PigeonCoopScreen(PigeonCoopContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = 256;
        this.imageHeight = 251;
    }

    @Override
    protected void init() {
        super.init();

        editBox = addRenderableWidget(new EditBox(Minecraft.getInstance().font, leftPos + 86, topPos + 74, 165, 20, editBox, Component.empty()));
        addRenderableWidget(new Button(leftPos + 118, topPos + 100, 100, 20, sendPigeonComponent, button -> NetworkHandler.INSTANCE.sendToServer(new SBPigeonGoalPacket(editBox.getValue(), menu.getPos()))));
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

        if (pigeon == null && minecraft != null)
            pigeon = ModRegistries.PIGEON.get().create(Objects.requireNonNull(minecraft.level));

        if (pigeon != null) {
            poseStack.pushPose();
            poseStack.translate(leftPos + 31D, topPos + 67D, 0);
            poseStack.scale(60, 60, 60);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(30));

            EntityRenderDispatcher erd = Minecraft.getInstance().getEntityRenderDispatcher();
            MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
            erd.setRenderShadow(false);
            erd.render(pigeon, 0, 0, 0, 0, 1, poseStack, immediate, 0xF000F0);
            erd.setRenderShadow(true);
            immediate.endBatch();

            poseStack.popPose();
        }
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        font.draw(poseStack, title, titleLabelX, titleLabelY, 4210752);
        font.draw(poseStack, playerInventoryTitle, 49, 157, 4210752);
        font.draw(poseStack, Component.translatable("postalpigeons.container.pigeon_coop.pigeons", menu.getPigeonAmount()), 88, 17, 4210752);
        font.draw(poseStack, Component.translatable("postalpigeons.container.pigeon_coop.available", menu.getRemainingPigeonAmount(), menu.getPigeonAmount()), 88, 28, 4210752);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
        if (Minecraft.getInstance().options.keyInventory.isActiveAndMatches(mouseKey))
            return false;

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
