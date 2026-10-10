package tseileinn.bowcasting.client.mixin;

import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tseileinn.bowcasting.client.BowcastingItemLookup;

@Mixin(ItemModelResolver.class)
public class ItemModelResolverMixin {
    @Inject(method = "updateForLiving", at = @At("TAIL"))
    private void bowcasting$remember(
            ItemStackRenderState state,
            ItemStack stack,
            ItemDisplayContext context,
            boolean leftHand,
            LivingEntity entity,
            CallbackInfo ci
    ) {
        BowcastingItemLookup.remember(state, stack);
    }
}