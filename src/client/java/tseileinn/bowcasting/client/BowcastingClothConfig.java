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

        general.addEntry(
                entryBuilder
                        .startBooleanToggle(
                                Component.literal("Enable Bow Effects"),
                                Bowcasting.CONFIG.renderBowRune
                        )
                        .setDefaultValue(true)
                        .setSaveConsumer(value ->
                                Bowcasting.CONFIG.renderBowRune = value
                        )
                        .build()
        );

        general.addEntry(
                entryBuilder
                        .startBooleanToggle(
                                Component.literal("Enable Crossbow Effects"),
                                Bowcasting.CONFIG.renderCrossbowRune
                        )
                        .setDefaultValue(true)
                        .setSaveConsumer(value ->
                                Bowcasting.CONFIG.renderCrossbowRune = value
                        )
                        .build()
        );

        general.addEntry(
                entryBuilder
                        .startBooleanToggle(
                                Component.literal("Enable Projectile Effects"),
                                Bowcasting.CONFIG.renderArrowRune
                        )
                        .setDefaultValue(true)
                        .setSaveConsumer(value ->
                                Bowcasting.CONFIG.renderArrowRune = value
                        )
                        .build()
        );

        general.addEntry(
                entryBuilder
                        .startIntSlider(
                                Component.literal("Projectile Texture Version"),
                                Bowcasting.CONFIG.runeParticleVersion,
                                1,
                                Bowcasting.BowcastingConfig.RUNE_PARTICLE_VERSIONS
                        )
                        .setDefaultValue(1)
                        .setMin(1)
                        .setMax(Bowcasting.BowcastingConfig.RUNE_PARTICLE_VERSIONS)
                        .setSaveConsumer(value ->
                                Bowcasting.CONFIG.runeParticleVersion = value
                        )
                        .build()
        );


        builder.setSavingRunnable(() -> Bowcasting.CONFIG.save());

        return builder.build();
    }
}