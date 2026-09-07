package tseileinn.bowcasting.client.mixin;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tseileinn.bowcasting.Bowcasting;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void bowcasting$spawnRuneTrail(CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow)(Object)this;

        if (!arrow.level().isClientSide) {
            return;
        }

        if (!Bowcasting.CONFIG.renderArrowRune){
            return;
        }

        var v = arrow.getDeltaMovement();

        arrow.level().addParticle(
                Bowcasting.RUNE_PARTICLE,
                arrow.getX(),
                arrow.getY() + 0.1,
                arrow.getZ(),
                0,
                0,
                0
        );
    }
}