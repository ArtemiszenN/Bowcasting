package tseileinn.bowcasting.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import tseileinn.bowcasting.client.animation.BowcastingAnimationState;

public class BowcastingClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client ->
				BowcastingAnimationState.tickAnimations()
		);
	}

	public static BowcastingLightFactory LIGHT_FACTORY =
			NoOpBowcastingLight::new;

}