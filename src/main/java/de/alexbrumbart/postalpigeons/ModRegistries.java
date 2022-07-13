package de.alexbrumbart.postalpigeons;

import de.alexbrumbart.postalpigeons.blocks.MailReceptorBlock;
import de.alexbrumbart.postalpigeons.blocks.MailReceptorBlockEntity;
import de.alexbrumbart.postalpigeons.blocks.MailReceptorContainer;
import de.alexbrumbart.postalpigeons.rendering.MailReceptorScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class ModRegistries {
    private ModRegistries() {
        throw new IllegalStateException("Utility class");
    }

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PostalPigeons.ID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PostalPigeons.ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PostalPigeons.ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, PostalPigeons.ID);

    private static final CreativeModeTab TAB = new CreativeModeTab(PostalPigeons.ID) {

        @NotNull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(MAIL_RECEPTOR_BI.get());
        }
    };

    // Mail receptor
    public static final RegistryObject<Block> MAIL_RECEPTOR = BLOCKS.register("mail_receptor", () -> new MailReceptorBlock(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<BlockEntityType<?>> MAIL_RECEPTOR_TE = TILES.register("mail_receptor", () -> BlockEntityType.Builder.of(MailReceptorBlockEntity::new, MAIL_RECEPTOR.get()).build(null));
    public static final RegistryObject<Item> MAIL_RECEPTOR_BI = ITEMS.register("mail_receptor", () -> new BlockItem(MAIL_RECEPTOR.get(), new Item.Properties().tab(TAB)));
    public static final RegistryObject<MenuType<MailReceptorContainer>> MAIL_RECEPTOR_MENU = MENUS.register("mail_receptor", () -> new MenuType<>(MailReceptorContainer::new));

    public static void registerContainerScreens() {
        MenuScreens.register(MAIL_RECEPTOR_MENU.get(), MailReceptorScreen::new);
    }
}
