package tseileinn.bowcasting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Bowcasting implements ModInitializer {
	public static final String MOD_ID = "bowcasting";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CONFIG = BowcastingConfig.load();
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir()
			.resolve("bowcasting");

	public static BowcastingConfig CONFIG;

	public static class BowcastingConfig {
		private static final Gson GSON = new GsonBuilder()
				.setPrettyPrinting()
				.create();

		private static final Path PATH = CONFIG_DIR.resolve("config.json");

		public float scaleMultiplier1 = 1.0f;
		public float scaleMultiplier2 = 1.0f;
		public float scaleMultiplier3 = 1.0f;
		public float rotationSpeedMultiplier = 1.0f;

		public static BowcastingConfig load() {
			try {
				if (Files.exists(PATH)) {
					return GSON.fromJson(
							Files.readString(PATH),
							BowcastingConfig.class
					);
				}
			} catch (IOException e) {
				LOGGER.error("Failed to load config", e);
			}

			return new BowcastingConfig();
		}

		public void save() {
			try {
				Files.createDirectories(PATH.getParent());
				Files.writeString(PATH, GSON.toJson(this));
			} catch (IOException e) {
				LOGGER.error("Failed to save config", e);
			}
		}
	}
}
