package de.alexbrumbart.postalpigeons.data;

import de.alexbrumbart.postalpigeons.ModRegistries;
import de.alexbrumbart.postalpigeons.PostalPigeons;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStateGenerator extends BlockStateProvider {
    public BlockStateGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, PostalPigeons.ID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void registerStatesAndModels() {
        String mailReceptorPath = ModRegistries.MAIL_RECEPTOR.getKey().location().getPath();
        simpleBlock(ModRegistries.MAIL_RECEPTOR.get(), models().cubeBottomTop(mailReceptorPath, modLoc("block/mail_receptor_side"), modLoc("block/mail_receptor_bottom"), modLoc("block/mail_receptor_top")));
        horizontalBlock(ModRegistries.PIGEON_COOP.get(), models().getExistingFile(ModelLocationUtils.getModelLocation(ModRegistries.PIGEON_COOP.get())));
    }
}
