package de.alexbrumbart.postalpigeons.data.loot;

import de.alexbrumbart.postalpigeons.ModRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;

public class BlockLootGenerator extends BlockLootSubProvider {
    public BlockLootGenerator() {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(ModRegistries.MAIL_RECEPTOR.get());
        dropSelf(ModRegistries.PIGEON_COOP.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModRegistries.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
    }
}
