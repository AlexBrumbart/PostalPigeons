package de.alexbrumbart.postalpigeons.rendering;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.alexbrumbart.postalpigeons.ModRegistries;
import de.alexbrumbart.postalpigeons.PostalPigeons;
import de.alexbrumbart.postalpigeons.blocks.PigeonCoopContainer;
import de.alexbrumbart.postalpigeons.entity.Pigeon;
import de.alexbrumbart.postalpigeons.util.MailReceptorStorage;
import de.alexbrumbart.postalpigeons.util.NetworkHandler;
import de.alexbrumbart.postalpigeons.util.packets.SBPigeonGoalPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PigeonCoopScreen extends AbstractContainerScreen<PigeonCoopContainer> {
    private static final ResourceLocation texture = new ResourceLocation(PostalPigeons.ID, "textures/gui/pigeon_coop.png");
    private static final ResourceLocation textureExtra = new ResourceLocation(PostalPigeons.ID, "textures/gui/pigeon_coop_extra.png");
    private static final Component sendPigeonComponent = Component.translatable("postalpigeons.container.pigeon_coop.send");
    private static final ItemStack seeds = new ItemStack(Items.WHEAT_SEEDS);

    private final ResourceKey<Level> dimension;
    private final Pigeon pigeon;
    private Set<Map.Entry<String, BlockPos>> unmodifiedEntries;
    private List<Map.Entry<String, BlockPos>> entries;
    private int page = 1;
    private int maxPages;

    private EditBox searchBox;
    private String lastSearchString = "";
    private PageButton backwardButton;
    private PageButton forwardButton;

    @SuppressWarnings("ConstantConditions")
    public PigeonCoopScreen(PigeonCoopContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = 256;
        this.imageHeight = 251;

        this.dimension = playerInventory.player.level.dimension();
        this.pigeon = ModRegistries.PIGEON.get().create(Minecraft.getInstance().level);
        this.unmodifiedEntries = MailReceptorStorage.getClientInstance(dimension).getEntries();
        this.entries = unmodifiedEntries.stream().toList();
        this.maxPages = ((entries.size() - 1) / 3) + 1;
    }

    @Override
    protected void init() {
        super.init();

        searchBox = addRenderableWidget(new EditBox(Minecraft.getInstance().font, leftPos + 89, topPos + 42, 120, 20, searchBox, Component.empty()));
        backwardButton = addRenderableWidget(new PageButton(leftPos + 210, topPos + 45, false, b -> {
            page--;
            updateButtons();
        }, true));
        forwardButton = addRenderableWidget(new PageButton(leftPos + 228, topPos + 45, true, b -> {
            page++;
            updateButtons();
        }, true));
        updateButtons();
    }

    // Update unmodified entry list when MailReceptorStorage sync completes.
    public void updateEntries() {
        unmodifiedEntries = MailReceptorStorage.getClientInstanceUnsynced(dimension).getEntries();
        entries = unmodifiedEntries.stream().filter(entry -> entry.getKey().toLowerCase().contains(searchBox.getValue().toLowerCase())).toList();

        maxPages = ((entries.size() - 1) / 3) + 1;
        page = Math.min(page, maxPages);

        updateButtons();
    }

    private void updateButtons() {
        backwardButton.visible = page != 1;
        forwardButton.visible = page != maxPages;
    }

    @Override
    protected void containerTick() {
        if (!searchBox.getValue().equals(lastSearchString)) {
            entries = unmodifiedEntries.stream().filter(entry -> entry.getKey().toLowerCase().contains(searchBox.getValue().toLowerCase())).toList();
            lastSearchString = searchBox.getValue();

            maxPages = ((entries.size() - 1) / 3) + 1;
            page = Math.min(page, maxPages);

            updateButtons();
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderTooltip(poseStack, mouseX, mouseY);

        for (int i = 0; i < 3; i++) {
            if (i + (page - 1) * 3 < entries.size()) {
                if (mouseX > leftPos + 88 && mouseX < leftPos + 249 && mouseY > topPos + 65 + 25 * i && mouseY < topPos + 89 + 25 * i)
                    renderTooltip(poseStack, sendPigeonComponent, mouseX, mouseY);
            } else {
                break;
            }
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Background
        RenderSystem.setShaderTexture(0, texture);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Mail receptor positions
        for (int i = 0; i < 3; i++) {
            int j = i + (page - 1) * 3;

            if (j < entries.size()) {
                RenderSystem.setShaderTexture(0, textureExtra);
                blit(poseStack, leftPos + 88, topPos + 65 + 25 * i, 0, 0, 161, 24);

                int distance = (int) Math.sqrt(entries.get(j).getValue().distSqr(menu.getPos()));
                int neededSeeds = Math.max(5, Math.min(distance / 24, 32));

                font.draw(poseStack, entries.get(j).getKey(), leftPos + 91F, topPos + 68F + 25 * i, 4210752);
                font.draw(poseStack, Component.translatable("postalpigeons.container.pigeon_coop.distance", distance), leftPos + 91F, topPos + 79F + 25 * i, 4210752);

                itemRenderer.renderGuiItem(seeds, leftPos + 229, topPos + 69 + 25 * i);
                itemRenderer.renderGuiItemDecorations(font, seeds, leftPos + 229, topPos + 69 + 25 * i, String.valueOf(neededSeeds));
            } else {
                break;
            }
        }

        // Pigeon
        poseStack.pushPose();
        poseStack.translate(leftPos + 32D, topPos + 67D, 0);
        poseStack.scale(60, 60, 60);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(35));

        EntityRenderDispatcher erd = Minecraft.getInstance().getEntityRenderDispatcher();
        MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
        erd.setRenderShadow(false);
        erd.render(pigeon, 0, 0, 0, 0, 1, poseStack, immediate, 0xF000F0);
        erd.setRenderShadow(true);
        immediate.endBatch();

        poseStack.popPose();
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < 3; i++) {
                if (i + (page - 1) * 3 < entries.size()) {
                    if (mouseX > leftPos + 88 && mouseX < leftPos + 249 && mouseY > topPos + 65 + 25 * i && mouseY < topPos + 89 + 25 * i) {
                        NetworkHandler.INSTANCE.sendToServer(new SBPigeonGoalPacket(entries.get(i + (page - 1) * 3).getKey(), menu.getPos()));
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

                        return true;
                    }
                } else {
                    break;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
