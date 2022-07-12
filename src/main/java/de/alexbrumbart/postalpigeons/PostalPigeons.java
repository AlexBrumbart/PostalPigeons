package de.alexbrumbart.postalpigeons;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PostalPigeons.ID)
public class PostalPigeons {
    public static final String ID = "postalpigeons";

    public PostalPigeons() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModRegistries.BLOCKS.register(modEventBus);
        ModRegistries.TILES.register(modEventBus);
        ModRegistries.ITEMS.register(modEventBus);
    }
}
