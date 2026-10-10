
package tseileinn.bowcasting.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tseileinn.bowcasting.client.BowcastingRenderHook;
import tseileinn.bowcasting.client.BowcastingSpellRendererImpl;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class ItemInHandRendererMixin {

    @WrapOperation(
            method = "submitArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;" +
                            "submit(Lcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
            )
    )
    private void bowcasting$firstPerson(
            ItemStackRenderState renderState,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            int overlay,
            int outlineColor,
            Operation<Void> original,
            PlayerRenderState playerState,
            FirstPersonHandsAndItemsRenderState handsState,
            float partialTicks,
            float xRot,
            InteractionHand hand,
            float attack,
            ItemStack itemStack,
            float inverseArmHeight,
            PoseStack originalPose,
            SubmitNodeCollector originalCollector,
            int originalLight
    ) {
        if (!(itemStack.getItem() instanceof BowItem)
                && !(itemStack.getItem() instanceof CrossbowItem)) {
            original.call(renderState, poseStack, collector,
                    light, overlay, outlineColor);
            return;
        }

        boolean rightHand =
                (hand == InteractionHand.MAIN_HAND)
                        == (playerState.avatarRenderState.mainArm == HumanoidArm.RIGHT);

        ItemDisplayContext context = rightHand
                ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        var previous = BowcastingRenderHook.swap(
                ps -> BowcastingSpellRendererImpl.INSTANCE
                        .bowcasting$renderSpell(itemStack, context, ps, collector)
        );

        try {
            original.call(renderState, poseStack, collector,
                    light, overlay, outlineColor);
        } finally {
            BowcastingRenderHook.swap(previous);
        }
    }
}
