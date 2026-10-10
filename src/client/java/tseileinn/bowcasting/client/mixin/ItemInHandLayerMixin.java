
package tseileinn.bowcasting.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tseileinn.bowcasting.client.BowcastingItemLookup;
import tseileinn.bowcasting.client.BowcastingSpellRenderer;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin {

    @Inject(
            method = "renderArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;" +
                            "render(Lcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lnet/minecraft/client/renderer/MultiBufferSource;II)V"
            )
    )
    private void bowcasting$thirdPerson(
            ArmedEntityRenderState entityState,
            ItemStackRenderState renderState,
            HumanoidArm arm,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int light,
            CallbackInfo ci
    ) {
        ItemStack stack = BowcastingItemLookup.get(renderState);

        if (stack == null) {
            return;
        }

        boolean leftHand = arm == HumanoidArm.LEFT;

        ItemDisplayContext context = leftHand
                ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;

        ((BowcastingSpellRenderer) Minecraft.getInstance().getItemRenderer())
                .bowcasting$drawSpell(
                        stack,
                        context,
                        poseStack,
                        renderState.transform(),
                        leftHand
                );
    }
}
