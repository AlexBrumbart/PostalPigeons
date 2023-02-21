package de.alexbrumbart.postalpigeons.data;

import de.alexbrumbart.postalpigeons.ModRegistries;
import de.alexbrumbart.postalpigeons.PostalPigeons;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider {
    public ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PostalPigeons.ID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void registerModels() {
        String mailReceptorPath = ModRegistries.MAIL_RECEPTOR_BI.getKey().location().getPath();
        withExistingParent(mailReceptorPath, modLoc("block/" + mailReceptorPath));
        String pigeonCoopPath = ModRegistries.PIGEON_COOP_BI.getKey().location().getPath();
        withExistingParent(pigeonCoopPath, modLoc("block/" + pigeonCoopPath));
        String pigeonSpawnEggPath = ModRegistries.PIGEON_SPAWN_EGG.getKey().location().getPath();
        withExistingParent(pigeonSpawnEggPath, mcLoc("item/template_spawn_egg"));
    }
}
