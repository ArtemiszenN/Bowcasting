package tseileinn.bowcasting.client.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import tseileinn.bowcasting.client.animation.BowcastingAnimationState;
import tseileinn.bowcasting.client.animation.BowcastingHandAnimationStateHolder;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements BowcastingHandAnimationStateHolder {

    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    private void bowcasting$stopUsingItem(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!self.level().isClientSide()) {
            return;
        }

        ItemStack stack = self.getUseItem();

        if (!(stack.getItem() instanceof BowItem
                || stack.getItem() instanceof CrossbowItem)) {
            return;
        }

        BowcastingAnimationState state =
                ((BowcastingHandAnimationStateHolder) self)
                        .bowcasting$getAnimationState(self.getUsedItemHand());

        // Crossbow finished charging: preserve the animation!
        if (stack.getItem() instanceof CrossbowItem
                && CrossbowItem.isCharged(stack)) {
            state.heartbeat();
            return;
        }

        // Bow released, or crossbow charge canceled.
        state.stopAnim();
    }


    @Unique
    private BowcastingAnimationState bowcasting$mainHandState;

    @Unique
    private BowcastingAnimationState bowcasting$offHandState;

    @Override
    public BowcastingAnimationState bowcasting$getAnimationState(
            InteractionHand hand
    ) {
        if (hand == InteractionHand.MAIN_HAND) {
            if (bowcasting$mainHandState == null) {
                bowcasting$mainHandState = new BowcastingAnimationState();
            }

            return bowcasting$mainHandState;
        }

        if (bowcasting$offHandState == null) {
            bowcasting$offHandState = new BowcastingAnimationState();
        }

        return bowcasting$offHandState;
    }

    @Override
    public BowcastingAnimationState bowcasting$getAnimationState(
            HumanoidArm arm
    ) {
        LivingEntity entity = (LivingEntity) (Object) this;

        InteractionHand hand = arm == entity.getMainArm()
                ? InteractionHand.MAIN_HAND
                : InteractionHand.OFF_HAND;

        return bowcasting$getAnimationState(hand);
    }


    @Unique
    private Item bowcasting$previousMainItem;

    @Unique
    private Item bowcasting$previousOffItem;

    @Unique
    private boolean bowcasting$previousMainCharged;

    @Unique
    private boolean bowcasting$previousOffCharged;

    @Inject(method = "tick", at = @At("TAIL"))
    private void bowcasting$trackHeldItems(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!entity.level().isClientSide()) {
            return;
        }

        bowcasting$trackHand(InteractionHand.MAIN_HAND);
        bowcasting$trackHand(InteractionHand.OFF_HAND);
    }

    @Unique
    private void bowcasting$trackHand(InteractionHand hand) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack stack = entity.getItemInHand(hand);

        Item currentItem = stack.getItem();
        boolean charged = currentItem instanceof CrossbowItem
                && CrossbowItem.isCharged(stack);

        boolean main = hand == InteractionHand.MAIN_HAND;

        Item previousItem = main
                ? bowcasting$previousMainItem
                : bowcasting$previousOffItem;

        boolean previouslyCharged = main
                ? bowcasting$previousMainCharged
                : bowcasting$previousOffCharged;

        if (previousItem != null &&
                (previousItem != currentItem ||
                        (previouslyCharged && !charged))) {

            BowcastingAnimationState state = main
                    ? bowcasting$mainHandState
                    : bowcasting$offHandState;

            if (state != null) {
                state.stopAnim();
            }
        }

        if (main) {
            bowcasting$previousMainItem = currentItem;
            bowcasting$previousMainCharged = charged;
        } else {
            bowcasting$previousOffItem = currentItem;
            bowcasting$previousOffCharged = charged;
        }
    }

}