package de.alexbrumbart.postalpigeons;

import de.alexbrumbart.postalpigeons.data.BlockStateGenerator;
import de.alexbrumbart.postalpigeons.data.ItemModelGenerator;
import de.alexbrumbart.postalpigeons.data.LanguageGenerator;
import de.alexbrumbart.postalpigeons.data.CraftingRecipeGenerator;
import de.alexbrumbart.postalpigeons.data.LootTableGenerator;
import de.alexbrumbart.postalpigeons.util.NetworkHandler;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
        generator.addProvider(event.includeClient(), new LanguageGenerator(generator));
        generator.addProvider(event.includeServer(), new CraftingRecipeGenerator(generator));
        generator.addProvider(event.includeServer(), new LootTableGenerator(generator));
    }
}
