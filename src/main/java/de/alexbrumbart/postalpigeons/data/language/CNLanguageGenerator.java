package de.alexbrumbart.postalpigeons.data.language;

import de.alexbrumbart.postalpigeons.ModRegistries;
import de.alexbrumbart.postalpigeons.PostalPigeons;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class CNLanguageGenerator extends LanguageProvider {
    public CNLanguageGenerator(DataGenerator generator) {
        super(generator, PostalPigeons.ID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.postalpigeons", "信鸽");
        add(ModRegistries.MAIL_RECEPTOR.get(), "邮件接收器");
        add("postalpigeon.container.mail_receptor", "邮件接收器: {0}");
        add("postalpigeon.screen.mail_receptor_button", "OK");
        add(ModRegistries.PIGEON_COOP.get(), "鸽舍");
        add("postalpigeons.container.pigeon_coop", "鸽舍");
        add("postalpigeons.container.pigeon_coop.pigeons", "{0} / 8 只鸽子已绑定");
        add("postalpigeons.container.pigeon_coop.available", "{0} / {1} 只鸽子在家");
        add("postalpigeons.container.pigeon_coop.send", "发送鸽子");
        add("postalpigeons.container.pigeon_coop.distance", "距离: {0}");
        add(ModRegistries.PIGEON.get(), "鸽子");
        add(ModRegistries.PIGEON_SPAWN_EGG.get(), "鸽子刷怪蛋");
    }
}
