package tseileinn.bowcasting.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import tseileinn.bowcasting.Bowcasting;
import tseileinn.bowcasting.client.animation.BowcastingAnimationState;
import tseileinn.bowcasting.client.particle.RuneParticle;

public class BowcastingClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client ->
				BowcastingAnimationState.tickAnimations()
		);
		ParticleFactoryRegistry.getInstance().register(
				Bowcasting.RUNE_PARTICLE,
				RuneParticle.Provider::new
		);
	}

	public static BowcastingLightFactory LIGHT_FACTORY =
			NoOpBowcastingLight::new;

}