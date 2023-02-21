package de.alexbrumbart.postalpigeons.data;

import de.alexbrumbart.postalpigeons.ModRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class CraftingRecipeGenerator extends RecipeProvider {
    public CraftingRecipeGenerator(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ModRegistries.MAIL_RECEPTOR.get())
                .define('#', ItemTags.PLANKS).define('X', ItemTags.WOODEN_SLABS).define('Y', Items.IRON_INGOT)
                .pattern("#Y#").pattern("# #").pattern("#X#")
                .unlockedBy("has_planks", has(ItemTags.PLANKS)).unlockedBy("has_wood_slab", has(ItemTags.WOODEN_SLABS)).unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ModRegistries.PIGEON_COOP.get())
                .define('#', ItemTags.PLANKS).define('X', ItemTags.LOGS).define('Y', Items.HAY_BLOCK)
                .pattern("###").pattern("X X").pattern("XYX")
                .unlockedBy("has_planks", has(ItemTags.PLANKS)).unlockedBy("has_wood_log", has(ItemTags.LOGS)).unlockedBy("has_hay_block", has(Items.HAY_BLOCK))
                .save(consumer);
    }
}
