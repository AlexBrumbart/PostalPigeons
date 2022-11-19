package de.alexbrumbart.postalpigeons.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Forbids block placing in the nether or end.
 */
public class PlacementBlockingBlockItem extends BlockItem {
    public PlacementBlockingBlockItem(Block block, Properties properties) {
        super(block, properties);
    }


    @Override
    public InteractionResult place(BlockPlaceContext context) {
        if (context.getLevel().dimension() == Level.NETHER || context.getLevel().dimension() == Level.END)
            return InteractionResult.FAIL;

        return super.place(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("postalpigeons.forbiddenPlacement").withStyle(ChatFormatting.GRAY));
    }
}
