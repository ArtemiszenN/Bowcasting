package tseileinn.bowcasting.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tseileinn.bowcasting.client.BowcastingRenderHook;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public class ItemStackLayerMixin {
    @Inject(
            method = "submit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/model/ItemTransform;" +
                            "apply(ZLcom/mojang/blaze3d/vertex/PoseStack$Pose;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void bowcasting$afterTransform(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            int overlay,
            int outlineColor,
            CallbackInfo ci
    ) {
        BowcastingRenderHook.draw(poseStack);
    }
}