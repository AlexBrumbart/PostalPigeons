package de.alexbrumbart.postalpigeons.data.language;

import de.alexbrumbart.postalpigeons.ModRegistries;
import de.alexbrumbart.postalpigeons.PostalPigeons;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class DELanguageGenerator extends LanguageProvider {
    public DELanguageGenerator(DataGenerator generator) {
        super(generator, PostalPigeons.ID, "de_de");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.postalpigeons", "Postal Pigeons");
        add(ModRegistries.MAIL_RECEPTOR.get(), "Briefkasten");
        add("postalpigeon.container.mail_receptor", "Briefkasten: {0}");
        add("postalpigeon.screen.mail_receptor_button", "OK");
        add(ModRegistries.PIGEON_COOP.get(), "Taubenschlag");
        add("postalpigeons.container.pigeon_coop", "Taubenschlag");
        add("postalpigeons.container.pigeon_coop.pigeons", "{0} / 8 Tauben gebunden");
        add("postalpigeons.container.pigeon_coop.available", "{0} / {1} Tauben zu Hause");
        add("postalpigeons.container.pigeon_coop.send", "Verschicke Taube");
        add("postalpigeons.container.pigeon_coop.distance", "Entfernung: {0}");
        add(ModRegistries.PIGEON.get(), "Taube");
        add(ModRegistries.PIGEON_SPAWN_EGG.get(), "Tauben Spawn Ei");
    }
}
