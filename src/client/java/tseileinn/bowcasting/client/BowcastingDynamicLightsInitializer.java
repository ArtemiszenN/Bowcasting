package tseileinn.bowcasting.client;

import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehaviorManager;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;

public class BowcastingDynamicLightsInitializer
        implements DynamicLightsInitializer {

    public static DynamicLightBehaviorManager MANAGER;
    public static BowcastingDynamicLight SPELL_LIGHT;

    @Override
    public void onInitializeDynamicLights(DynamicLightsContext context) {
        MANAGER = context.dynamicLightBehaviorManager();
        BowcastingClient.LIGHT_FACTORY = BowcastingDynamicLight::new;
    }
}