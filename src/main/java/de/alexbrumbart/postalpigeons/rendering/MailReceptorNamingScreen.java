package de.alexbrumbart.postalpigeons.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import de.alexbrumbart.postalpigeons.util.NetworkHandler;
import de.alexbrumbart.postalpigeons.util.packets.SBMailReceptorPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class MailReceptorNamingScreen extends Screen {
    private static final Component buttonText = Component.translatable("postalpigeon.screen.mail_receptor_button");

    private final BlockPos pos;
    private EditBox editBox;

    public MailReceptorNamingScreen(BlockPos pos) {
        super(CommonComponents.EMPTY);

        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        final int x = width / 2;
        final int y = height / 2;

        editBox = addRenderableWidget(new EditBox(Minecraft.getInstance().font, x - 200, y - 30, 400, 20, editBox, Component.empty()));
        addRenderableWidget(new Button.Builder(buttonText, button -> {
            if (!editBox.getValue().isBlank())
                NetworkHandler.INSTANCE.sendToServer(new SBMailReceptorPacket(editBox.getValue(), pos));

            Minecraft.getInstance().setScreen(null);
        }).bounds(x - 100, y + 10, 200, 20).build());

        setInitialFocus(editBox);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }
}
