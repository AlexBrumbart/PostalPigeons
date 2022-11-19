package de.alexbrumbart.postalpigeons.data.language;

import de.alexbrumbart.postalpigeons.ModRegistries;
import de.alexbrumbart.postalpigeons.PostalPigeons;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class ENLanguageGenerator extends LanguageProvider {
    public ENLanguageGenerator(DataGenerator generator) {
        super(generator, PostalPigeons.ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.postalpigeons", "Postal Pigeons");
        add(ModRegistries.MAIL_RECEPTOR.get(), "Mail Receptor");
        add("postalpigeon.container.mail_receptor", "Mail Receptor: {0}");
        add("postalpigeon.screen.mail_receptor_button", "OK");
        add(ModRegistries.PIGEON_COOP.get(), "Pigeon Coop");
        add("postalpigeons.container.pigeon_coop", "Pigeon Coop");
        add("postalpigeons.container.pigeon_coop.pigeons", "{0} / 8 Pigeons bound");
        add("postalpigeons.container.pigeon_coop.available", "{0} / {1} Pigeons at home");
        add("postalpigeons.container.pigeon_coop.send", "Send Pigeon");
        add("postalpigeons.container.pigeon_coop.distance", "Distance: {0}");
        add(ModRegistries.PIGEON.get(), "Pigeon");
        add(ModRegistries.PIGEON_SPAWN_EGG.get(), "Pigeon Spawn Egg");

        add("postalpigeons.forbiddenPlacement", "Can not be placed in the Nether or End.");
    }
}
