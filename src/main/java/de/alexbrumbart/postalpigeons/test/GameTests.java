package de.alexbrumbart.postalpigeons.test;

import de.alexbrumbart.postalpigeons.PostalPigeons;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.IItemHandler;

import java.util.Objects;

@GameTestHolder(value = PostalPigeons.ID)
public class GameTests {
    private GameTests() {
        throw new IllegalStateException("Utility class!");
    }

    @GameTest(template = "mail_receptor")
    @PrefixGameTestTemplate(false)
    public static void mailReceptorCap(GameTestHelper helper) {
        helper.succeedIf(() -> {
            BlockEntity receptor = Objects.requireNonNull(helper.getBlockEntity(new BlockPos(1, 2, 1)));
            IItemHandler handler = receptor.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() -> new GameTestAssertException("handler null"));
            if (handler.getSlots() != 18) {
                throw new GameTestAssertException("handler has not correct size");
            }
        });
    }
}
