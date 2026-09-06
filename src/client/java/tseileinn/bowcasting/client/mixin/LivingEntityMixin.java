package tseileinn.bowcasting.client.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import tseileinn.bowcasting.client.animation.BowcastingAnimationState;
import tseileinn.bowcasting.client.animation.BowcastingAnimationStateHolder;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(
            method = "stopUsingItem",
            at = @At("HEAD")
    )
    private void bowcasting$stopUsingItem(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!self.level().isClientSide()) {
            return;
        }

        ItemStack itemStack = self.getUseItem();

        if (!(itemStack.getItem() instanceof BowItem
                || itemStack.getItem() instanceof CrossbowItem)) {
            return;
        }

        BowcastingAnimationState state =
                ((BowcastingAnimationStateHolder) (Object) itemStack)
                        .bowcasting$getAnimationState();

        state.stopAnim();
    }
}