package tseileinn.bowcasting.client.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import tseileinn.bowcasting.client.animation.BowcastingHandAnimationStateHolder;

@Mixin(BowItem.class)
public class BowItemMixin {
    @Inject(method = "releaseUsing", at = @At("HEAD"))
    private void bowcasting$releaseUsing(
            ItemStack itemStack,
            Level level,
            LivingEntity entity,
            int remainingUseTicks,
            CallbackInfo ci
    ) {
        if (!level.isClientSide()) {
            return;
        }

        ((BowcastingHandAnimationStateHolder) entity)
                .bowcasting$getAnimationState(
                        entity.getUsedItemHand()
                )
                .stopAnim();
    }

}