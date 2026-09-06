package tseileinn.bowcasting.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
import tseileinn.bowcasting.client.animation.BowcastingAnimationStateHolder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Inject(
            method = "releaseUsing",
            at = @At("TAIL")
    )
    private void bowcasting$releaseUsing(
            ItemStack itemStack,
            Level level,
            LivingEntity livingEntity,
            int remainingUseTicks,
            CallbackInfo ci
    ) {
        if (!level.isClientSide()) {
            return;
        }

        BowcastingAnimationState state =
                ((BowcastingAnimationStateHolder) (Object) itemStack)
                        .bowcasting$getAnimationState();

        if (CrossbowItem.isCharged(itemStack)) {
            state.crossbowChargedCheck();
            return;
        }

        state.stopAnim();
    }

    @Inject(
            method = "use",
            at = @At("HEAD")
    )
    private void bowcasting$use(
            Level level,
            Player player,
            InteractionHand interactionHand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir
    ) {
        if (!level.isClientSide()) {
            return;
        }

        ItemStack itemStack = player.getItemInHand(interactionHand);

        BowcastingAnimationState state =
                ((BowcastingAnimationStateHolder) (Object) itemStack)
                        .bowcasting$getAnimationState();

        if (CrossbowItem.isCharged(itemStack)) {
            state.crossbowChargedCheck();
            return;
        }

        state.stopAnim();
    }

    @Inject(
            method = "onUseTick",
            at = @At("HEAD")
    )
    private void bowcasting$onUseTick(
            Level level,
            LivingEntity livingEntity,
            ItemStack itemStack,
            int remainingUseTicks,
            CallbackInfo ci
    ) {
        if (!level.isClientSide()) {
            return;
        }

        BowcastingAnimationState state =
                ((BowcastingAnimationStateHolder) (Object) itemStack)
                        .bowcasting$getAnimationState();

        state.heartbeat();

        if (state.isDead()) {
            state.startAnim(itemStack);
        }
    }

    @Inject(
            method = "onUseTick",
            at = @At("TAIL")
    )
    private void bowcasting$afterUseTick(
            Level level,
            LivingEntity livingEntity,
            ItemStack itemStack,
            int remainingUseTicks,
            CallbackInfo ci
    ) {
        if (!level.isClientSide()) {
            return;
        }

        if (!CrossbowItem.isCharged(itemStack)) {
            return;
        }

        BowcastingAnimationState state =
                ((BowcastingAnimationStateHolder) (Object) itemStack)
                        .bowcasting$getAnimationState();

        CompletableFuture.delayedExecutor(
                200,
                TimeUnit.MILLISECONDS
        ).execute(() ->
                Minecraft.getInstance().execute(state::crossbowChargedCheck)
        );
    }
}

