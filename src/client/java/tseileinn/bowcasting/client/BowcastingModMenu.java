package tseileinn.bowcasting.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;
import tseileinn.bowcasting.Bowcasting;

public class BowcastingModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.literal("Bowcasting Config"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            ConfigCategory general = builder.getOrCreateCategory(
                    Component.literal("General")
            );

            general.addEntry(
                    entryBuilder
                            .startIntSlider(
                                    Component.literal("Scale Multiplier"),
                                    (int)(Bowcasting.CONFIG.scaleMultiplier * 10), 1, 100
                            )
                            .setDefaultValue(10)
                            .setMin(1)
                            .setMax(100)
                            .setSaveConsumer(value ->
                                    Bowcasting.CONFIG.scaleMultiplier = value / 10.0f
                            )
                            .build()
            );

            general.addEntry(
                    entryBuilder
                            .startIntSlider(
                                    Component.literal("Rotation Speed Multiplier"),
                                    (int) (Bowcasting.CONFIG.rotationSpeedMultiplier * 10), 1, 100
                            )
                            .setDefaultValue(10)
                            .setMin(1)
                            .setMax(100)
                            .setSaveConsumer(value ->
                                    Bowcasting.CONFIG.rotationSpeedMultiplier = value / 10.0f
                            )
                            .build()
            );

            builder.setSavingRunnable(() -> Bowcasting.CONFIG.save());

            return builder.build();
        };
    }
}