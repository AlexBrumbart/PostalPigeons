package de.alexbrumbart.postalpigeons.data;

import com.mojang.datafixers.util.Pair;
import de.alexbrumbart.postalpigeons.ModRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LootTableGenerator extends LootTableProvider {
    public LootTableGenerator(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return List.of(Pair.of(BlockLootGenerator::new, LootContextParamSets.BLOCK), Pair.of(EntityLootGenerator::new, LootContextParamSets.ENTITY));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
        map.forEach((rl, lootTable) -> LootTables.validate(validationContext, rl, lootTable));
    }

    private static class BlockLootGenerator extends BlockLoot {

        @Override
        protected void addTables() {
            dropSelf(ModRegistries.MAIL_RECEPTOR.get());
            dropSelf(ModRegistries.PIGEON_COOP.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModRegistries.BLOCKS.getEntries().stream().map(RegistryObject::get).toList();
        }
    }

    private static class EntityLootGenerator extends EntityLoot {

        @Override
        protected void addTables() {
            add(ModRegistries.PIGEON.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(
                    LootItem.lootTableItem(Items.FEATHER).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))));
        }

        @Override
        protected Iterable<EntityType<?>> getKnownEntities() {
            return ModRegistries.ENTITIES.getEntries().stream().map(RegistryObject::get).collect(Collectors.toSet());
        }
    }
}
