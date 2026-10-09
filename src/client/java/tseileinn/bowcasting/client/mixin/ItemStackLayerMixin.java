package tseileinn.bowcasting.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tseileinn.bowcasting.client.BowcastingRenderHook;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public class ItemStackLayerMixin {
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/model/ItemTransform;" +
                            "apply(ZLcom/mojang/blaze3d/vertex/PoseStack$Pose;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void bowcasting$afterTransform(
            PoseStack poseStack,
            MultiBufferSource buffers,
            int light,
            int overlay,
            CallbackInfo ci
    ) {
        BowcastingRenderHook.draw(poseStack);
    }
}