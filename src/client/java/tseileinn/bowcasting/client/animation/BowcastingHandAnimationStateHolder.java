package tseileinn.bowcasting.client.animation;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

public interface BowcastingHandAnimationStateHolder {
    BowcastingAnimationState bowcasting$getAnimationState(InteractionHand hand);

    BowcastingAnimationState bowcasting$getAnimationState(HumanoidArm arm);
}
