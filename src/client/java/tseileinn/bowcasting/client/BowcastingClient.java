package tseileinn.bowcasting.client;

import net.fabricmc.api.ClientModInitializer;

public class BowcastingClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
	}

	public static BowcastingLightFactory LIGHT_FACTORY =
			NoOpBowcastingLight::new;

}