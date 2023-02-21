package de.alexbrumbart.postalpigeons;

import de.alexbrumbart.postalpigeons.data.BlockStateGenerator;
import de.alexbrumbart.postalpigeons.data.CraftingRecipeGenerator;
import de.alexbrumbart.postalpigeons.data.ItemModelGenerator;
import de.alexbrumbart.postalpigeons.data.language.CNLanguageGenerator;
import de.alexbrumbart.postalpigeons.data.language.DELanguageGenerator;
import de.alexbrumbart.postalpigeons.data.language.ENLanguageGenerator;
import de.alexbrumbart.postalpigeons.data.loot.BlockLootGenerator;
import de.alexbrumbart.postalpigeons.data.loot.EntityLootGenerator;
import de.alexbrumbart.postalpigeons.util.NetworkHandler;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddSpawnsBiomeModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mod(PostalPigeons.ID)
public class PostalPigeons {
    public static final String ID = "postalpigeons";

    public PostalPigeons() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::createCreativeTab);
        modEventBus.addListener(this::gatherData);

        ModRegistries.BLOCKS.register(modEventBus);
        ModRegistries.ITEMS.register(modEventBus);
        ModRegistries.TILES.register(modEventBus);
        ModRegistries.MENUS.register(modEventBus);
        ModRegistries.ENTITIES.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.registerPackets();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ModRegistries::registerContainerScreens);
    }

    // Create Creative Tab
    private void createCreativeTab(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(ID, ID), builder ->
                builder.title(Component.translatable("itemGroup." + ID))
                        .icon(() -> new ItemStack(ModRegistries.MAIL_RECEPTOR.get()))
                        .displayItems((enabledFeatures, output, hasPermission) -> {
                            output.accept(ModRegistries.MAIL_RECEPTOR_BI.get());
                            output.accept(ModRegistries.PIGEON_COOP_BI.get());
                            output.accept(ModRegistries.PIGEON_SPAWN_EGG.get());
                        }));
    }

    // Automatic data & asset generation
    private void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), (DataProvider.Factory<BlockStateGenerator>) output -> new BlockStateGenerator(output, existingFileHelper));
        generator.addProvider(event.includeClient(), (DataProvider.Factory<ItemModelGenerator>) output -> new ItemModelGenerator(output, existingFileHelper));
        generator.addProvider(event.includeClient(), (DataProvider.Factory<ENLanguageGenerator>) ENLanguageGenerator::new);
        generator.addProvider(event.includeClient(), (DataProvider.Factory<DELanguageGenerator>) DELanguageGenerator::new);
        generator.addProvider(event.includeClient(), (DataProvider.Factory<CNLanguageGenerator>) CNLanguageGenerator::new);
        generator.addProvider(event.includeServer(), (DataProvider.Factory<CraftingRecipeGenerator>) CraftingRecipeGenerator::new);
        generator.addProvider(event.includeServer(), (DataProvider.Factory<LootTableProvider>) output -> new LootTableProvider(output, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(BlockLootGenerator::new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(EntityLootGenerator::new, LootContextParamSets.ENTITY))));

        generator.addProvider(event.includeServer(), (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(),
                new RegistrySetBuilder().add(ForgeRegistries.Keys.BIOME_MODIFIERS, ctx -> ctx.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(ID, "add_pigeon_spawn")),
                        AddSpawnsBiomeModifier.singleSpawn(ctx.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_FOREST), new SpawnerData(ModRegistries.PIGEON.get(), 20, 1, 6)))), Set.of(ID)));
    }
}
