package de.alexbrumbart.postalpigeons;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PostalPigeons.ID)
public class PostalPigeons {
    public static final String ID = "postalpigeons";

    public PostalPigeons() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);

        ModRegistries.BLOCKS.register(modEventBus);
        ModRegistries.TILES.register(modEventBus);
        ModRegistries.ITEMS.register(modEventBus);
        ModRegistries.MENUS.register(modEventBus);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModRegistries.registerContainerScreens();
        });
    }
}
