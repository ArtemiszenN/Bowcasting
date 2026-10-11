package tseileinn.bowcasting.client.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import tseileinn.bowcasting.client.animation.BowcastingAnimationState;
import tseileinn.bowcasting.client.animation.BowcastingHandAnimationStateHolder;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Inject(method = "onUseTick", at = @At("HEAD"))
    private void bowcasting$onUseTick(
            Level level,
            LivingEntity entity,
            ItemStack stack,
            int remainingUseTicks,
            CallbackInfo ci
    ) {
        if (!level.isClientSide()) {
            return;
        }

        BowcastingAnimationState state =
                ((BowcastingHandAnimationStateHolder) entity)
                        .bowcasting$getAnimationState(
                                entity.getUsedItemHand()
                        );

        state.heartbeat();

        if (state.isDead()) {
            state.startAnim(stack);
        }
    }


    @Inject(method = "releaseUsing", at = @At("TAIL"))
    private void bowcasting$releaseUsing(
            ItemStack itemStack,
            Level level,
            LivingEntity entity,
            int i,
            CallbackInfo ci
    ) {
        if (!level.isClientSide()) {
            return;
        }

        BowcastingAnimationState state =
                ((BowcastingHandAnimationStateHolder) entity)
                        .bowcasting$getAnimationState(
                                entity.getUsedItemHand()
                        );

        if (CrossbowItem.isCharged(itemStack)) {
            // Successful charge: preserve rotations.
            state.heartbeat();
        } else {
            // Aborted charge.
            state.stopAnim();
        }
    }

    @Inject(method = "use", at = @At("HEAD"))
    private void bowcasting$use(
            Level level,
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (!level.isClientSide()) {
            return;
        }

        // New charging attempt, or firing a charged crossbow.
        ((BowcastingHandAnimationStateHolder) player)
                .bowcasting$getAnimationState(hand)
                .stopAnim();
    }
}
