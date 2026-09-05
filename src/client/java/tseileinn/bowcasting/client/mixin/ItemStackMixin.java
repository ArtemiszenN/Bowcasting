package tseileinn.bowcasting.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.item.ItemStack;

import tseileinn.bowcasting.client.animation.BowcastingAnimationState;
import tseileinn.bowcasting.client.animation.BowcastingAnimationStateHolder;

@Mixin(ItemStack.class)
public class ItemStackMixin implements BowcastingAnimationStateHolder {

    @Unique
    private final BowcastingAnimationState bowcasting$animationState =
            new BowcastingAnimationState();

    @Override
    public BowcastingAnimationState bowcasting$getAnimationState() {
        return bowcasting$animationState;
    }
}