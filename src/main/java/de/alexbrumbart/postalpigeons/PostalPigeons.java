package de.alexbrumbart.postalpigeons;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import de.alexbrumbart.postalpigeons.data.BlockStateGenerator;
import de.alexbrumbart.postalpigeons.data.ItemModelGenerator;
import de.alexbrumbart.postalpigeons.data.language.CNLanguageGenerator;
import de.alexbrumbart.postalpigeons.data.language.DELanguageGenerator;
import de.alexbrumbart.postalpigeons.data.language.ENLanguageGenerator;
import de.alexbrumbart.postalpigeons.data.CraftingRecipeGenerator;
import de.alexbrumbart.postalpigeons.data.LootTableGenerator;
import de.alexbrumbart.postalpigeons.util.NetworkHandler;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddSpawnsBiomeModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

@Mod(PostalPigeons.ID)
public class PostalPigeons {
    public static final String ID = "postalpigeons";

    public PostalPigeons() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
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
        event.enqueueWork(() -> {
            ModRegistries.registerContainerScreens();
        });
    }

    // Automatic data & asset generation
    private void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new BlockStateGenerator(generator, existingFileHelper));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(generator, existingFileHelper));
        generator.addProvider(event.includeClient(), new ENLanguageGenerator(generator));
        generator.addProvider(event.includeClient(), new DELanguageGenerator(generator));
        generator.addProvider(event.includeClient(), new CNLanguageGenerator(generator));
        generator.addProvider(event.includeServer(), new CraftingRecipeGenerator(generator));
        generator.addProvider(event.includeServer(), new LootTableGenerator(generator));

        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());
        BiomeModifier pigeonSpawn = AddSpawnsBiomeModifier.singleSpawn(new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).orElseThrow(), BiomeTags.IS_FOREST), new SpawnerData(ModRegistries.PIGEON.get(), 20, 1, 6));
        generator.addProvider(event.includeServer(), JsonCodecProvider.forDatapackRegistry(generator, existingFileHelper, ID, ops, ForgeRegistries.Keys.BIOME_MODIFIERS, Map.of(new ResourceLocation(ID, "add_pigeon_spawn"), pigeonSpawn)));
    }
}
