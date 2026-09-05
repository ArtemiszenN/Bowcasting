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
import tseileinn.bowcasting.client.animation.BowcastingAnimationStateHolder;

@Mixin(Item.class)
public class ItemMixin {

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

        if (!(itemStack.getItem() instanceof BowItem)) {
            return;
        }

        BowcastingAnimationState state =
                ((BowcastingAnimationStateHolder) (Object) itemStack)
                        .bowcasting$getAnimationState();

        state.heartbeat();
        if(state.isDead()) {
            state.startAnim();
        }
    }
}