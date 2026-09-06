package tseileinn.bowcasting.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import tseileinn.bowcasting.Bowcasting;

public class BowcastingClothConfig {

    public static Screen create(Screen parent) {
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
                                Component.literal("Bow Stage 1 Scale Multiplier"),
                                (int) (Bowcasting.CONFIG.scaleMultiplier1 * 10),
                                1,
                                100
                        )
                        .setDefaultValue(10)
                        .setMin(1)
                        .setMax(100)
                        .setSaveConsumer(value ->
                                Bowcasting.CONFIG.scaleMultiplier1 = value / 10.0f
                        )
                        .build()
        );

        general.addEntry(
                entryBuilder
                        .startIntSlider(
                                Component.literal("Bow Stage 2 Scale Multiplier"),
                                (int) (Bowcasting.CONFIG.scaleMultiplier2 * 10),
                                1,
                                100
                        )
                        .setDefaultValue(10)
                        .setMin(1)
                        .setMax(100)
                        .setSaveConsumer(value ->
                                Bowcasting.CONFIG.scaleMultiplier2 = value / 10.0f
                        )
                        .build()
        );

        general.addEntry(
                entryBuilder
                        .startIntSlider(
                                Component.literal("Bow Stage 3 Scale Multiplier"),
                                (int) (Bowcasting.CONFIG.scaleMultiplier3 * 10),
                                1,
                                100
                        )
                        .setDefaultValue(10)
                        .setMin(1)
                        .setMax(100)
                        .setSaveConsumer(value ->
                                Bowcasting.CONFIG.scaleMultiplier3 = value / 10.0f
                        )
                        .build()
        );

        general.addEntry(
                entryBuilder
                        .startIntSlider(
                                Component.literal("Crossbow Stage 1 Scale Multiplier"),
                                (int) (Bowcasting.CONFIG.xbowScaleMultiplier1 * 10),
                                1,
                                100
                        )
                        .setDefaultValue(10)
                        .setMin(1)
                        .setMax(100)
                        .setSaveConsumer(value ->
                                Bowcasting.CONFIG.xbowScaleMultiplier1 = value / 10.0f
                        )
                        .build()
        );

        general.addEntry(
                entryBuilder
                        .startIntSlider(
                                Component.literal("Crossbow Stage 2 Scale Multiplier"),
                                (int) (Bowcasting.CONFIG.xbowScaleMultiplier2 * 10),
                                1,
                                100
                        )
                        .setDefaultValue(10)
                        .setMin(1)
                        .setMax(100)
                        .setSaveConsumer(value ->
                                Bowcasting.CONFIG.xbowScaleMultiplier2 = value / 10.0f
                        )
                        .build()
        );

        general.addEntry(
                entryBuilder
                        .startIntSlider(
                                Component.literal("Crossbow Stage 3 Scale Multiplier"),
                                (int) (Bowcasting.CONFIG.xbowScaleMultiplier3 * 10),
                                1,
                                100
                        )
                        .setDefaultValue(10)
                        .setMin(1)
                        .setMax(100)
                        .setSaveConsumer(value ->
                                Bowcasting.CONFIG.xbowScaleMultiplier3 = value / 10.0f
                        )
                        .build()
        );

        general.addEntry(
                entryBuilder
                        .startIntSlider(
                                Component.literal("Rotation Speed Multiplier"),
                                (int) (Bowcasting.CONFIG.rotationSpeedMultiplier * 10),
                                1,
                                100
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
    }
}