package tseileinn.bowcasting.client.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import tseileinn.bowcasting.client.animation.BowcastingAnimationState;
import tseileinn.bowcasting.client.animation.BowcastingHandAnimationStateHolder;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "onUseTick", at = @At("HEAD"))
    private void bowcasting$onUseTick(
            Level level,
            LivingEntity entity,
            ItemStack stack,
            int remainingUseTicks,
            CallbackInfo ci
    ) {
        if (!level.isClientSide()
                || !(stack.getItem() instanceof BowItem)) {
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
}
