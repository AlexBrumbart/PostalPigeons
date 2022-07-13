package de.alexbrumbart.postalpigeons.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class RenderingUtils {
    private RenderingUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void openMailReceptorNamingScreen(BlockPos pos) {
        Minecraft.getInstance().setScreen(new MailReceptorNamingScreen(pos));
    }
}
