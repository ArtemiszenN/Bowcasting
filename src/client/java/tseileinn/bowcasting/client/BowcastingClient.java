package tseileinn.bowcasting.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;
import tseileinn.bowcasting.Bowcasting;

public class BowcastingClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
	}

	public static class BowcastingModMenu implements ModMenuApi {

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
								.startFloatField(
										Component.literal("Scale Multiplier"),
										Bowcasting.CONFIG.scaleMultiplier
								)
								.setDefaultValue(1.0f)
								.setMin(0.1f)
								.setMax(10.0f)
								.setSaveConsumer(value ->
										Bowcasting.CONFIG.scaleMultiplier = value
								)
								.build()
				);

				general.addEntry(
						entryBuilder
								.startFloatField(
										Component.literal("Rotation Speed Multiplier"),
										Bowcasting.CONFIG.rotationSpeedMultiplier
								)
								.setDefaultValue(1.0f)
								.setMin(0.1f)
								.setMax(10.0f)
								.setSaveConsumer(value ->
										Bowcasting.CONFIG.rotationSpeedMultiplier = value
								)
								.build()
				);

				builder.setSavingRunnable(() -> Bowcasting.CONFIG.save());

				return builder.build();
			};
		}
	}
}