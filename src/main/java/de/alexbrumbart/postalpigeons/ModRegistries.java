package de.alexbrumbart.postalpigeons;

import de.alexbrumbart.postalpigeons.blocks.MailReceptorBlock;
import de.alexbrumbart.postalpigeons.blocks.MailReceptorBlockEntity;
import de.alexbrumbart.postalpigeons.blocks.MailReceptorContainer;
import de.alexbrumbart.postalpigeons.blocks.PigeonCoopBlock;
import de.alexbrumbart.postalpigeons.blocks.PigeonCoopBlockEntity;
import de.alexbrumbart.postalpigeons.blocks.PigeonCoopContainer;
import de.alexbrumbart.postalpigeons.entity.Pigeon;
import de.alexbrumbart.postalpigeons.items.PlacementBlockingBlockItem;
import de.alexbrumbart.postalpigeons.rendering.MailReceptorScreen;
import de.alexbrumbart.postalpigeons.rendering.PigeonCoopScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRegistries {
    private ModRegistries() {
        throw new IllegalStateException("Utility class");
    }

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PostalPigeons.ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PostalPigeons.ID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PostalPigeons.ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, PostalPigeons.ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PostalPigeons.ID);

    // Mail receptor
    public static final RegistryObject<Block> MAIL_RECEPTOR = BLOCKS.register("mail_receptor", () -> new MailReceptorBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<BlockEntityType<?>> MAIL_RECEPTOR_TE = TILES.register("mail_receptor", () -> BlockEntityType.Builder.of(MailReceptorBlockEntity::new, MAIL_RECEPTOR.get()).build(null));
    public static final RegistryObject<Item> MAIL_RECEPTOR_BI = ITEMS.register("mail_receptor", () -> new PlacementBlockingBlockItem(MAIL_RECEPTOR.get(), new Item.Properties()));
    public static final RegistryObject<MenuType<MailReceptorContainer>> MAIL_RECEPTOR_MENU = MENUS.register("mail_receptor", () -> new MenuType<>(MailReceptorContainer::new));

    // Pigeon Coop
    public static final RegistryObject<Block> PIGEON_COOP = BLOCKS.register("pigeon_coop", () -> new PigeonCoopBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F, 3.0F)));
    public static final RegistryObject<BlockEntityType<?>> PIGEON_COOP_TE = TILES.register("pigeon_coop", () -> BlockEntityType.Builder.of(PigeonCoopBlockEntity::new, PIGEON_COOP.get()).build(null));
    public static final RegistryObject<Item> PIGEON_COOP_BI = ITEMS.register("pigeon_coop", () -> new PlacementBlockingBlockItem(PIGEON_COOP.get(), new Item.Properties()));
    public static final RegistryObject<MenuType<PigeonCoopContainer>> PIGEON_COOP_MENU = MENUS.register("pigeon_coop", () -> new MenuType<>(((IContainerFactory<PigeonCoopContainer>)PigeonCoopContainer::new)));

    // Pigeon
    public static final RegistryObject<EntityType<Pigeon>> PIGEON = ENTITIES.register("pigeon", () -> EntityType.Builder.of(Pigeon::new, MobCategory.CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10).build("pigeon"));
    public static final RegistryObject<Item> PIGEON_SPAWN_EGG = ITEMS.register("pigeon_spawn_egg", () -> new ForgeSpawnEggItem(PIGEON, 0xff0000, 0x00ff00, new Item.Properties()));

    public static void registerContainerScreens() {
        MenuScreens.register(MAIL_RECEPTOR_MENU.get(), MailReceptorScreen::new);
        MenuScreens.register(PIGEON_COOP_MENU.get(), PigeonCoopScreen::new);
    }
}
