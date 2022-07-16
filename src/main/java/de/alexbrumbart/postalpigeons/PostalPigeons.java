package de.alexbrumbart.postalpigeons;

import de.alexbrumbart.postalpigeons.util.NetworkHandler;
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
}
