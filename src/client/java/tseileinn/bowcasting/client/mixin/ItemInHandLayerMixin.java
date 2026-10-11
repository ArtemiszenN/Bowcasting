package tseileinn.bowcasting.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import tseileinn.bowcasting.client.animation.BowcastingAnimationState;
import tseileinn.bowcasting.client.animation.BowcastingHandAnimationStateHolder;
import tseileinn.bowcasting.client.render.BowcastingEntityRenderState;
import tseileinn.bowcasting.client.render.BowcastingSpellRenderer;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin {

    @Inject(
            method = "renderArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render("
                            + "Lnet/minecraft/world/item/ItemStack;"
                            + "Lnet/minecraft/world/item/ItemDisplayContext;"
                            + "Z"
                            + "Lcom/mojang/blaze3d/vertex/PoseStack;"
                            + "Lnet/minecraft/client/renderer/MultiBufferSource;"
                            + "II"
                            + "Lnet/minecraft/client/resources/model/BakedModel;"
                            + ")V"
            )
    )
    private void bowcasting$renderSpell(
            LivingEntityRenderState renderState,
            @Nullable BakedModel bakedModel,
            ItemStack renderedStack,
            ItemDisplayContext context,
            HumanoidArm arm,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int light,
            CallbackInfo ci
    ) {
        if (bakedModel == null || renderedStack.isEmpty()) {
            return;
        }

        if (!(renderedStack.getItem() instanceof BowItem)
                && !(renderedStack.getItem() instanceof CrossbowItem)) {
            return;
        }

        LivingEntity entity =
                ((BowcastingEntityRenderState) renderState)
                        .bowcasting$getEntity();

        if (entity == null) {
            return;
        }

        ItemStack heldStack = entity.getItemHeldByArm(arm);

        if (heldStack.isEmpty()
                || heldStack.getItem() != renderedStack.getItem()) {
            return;
        }

        BowcastingAnimationState state =
                ((BowcastingHandAnimationStateHolder) entity)
                        .bowcasting$getAnimationState(arm);

        BowcastingSpellRenderer.render(
                renderedStack,
                context,
                arm == HumanoidArm.LEFT,
                poseStack,
                bakedModel,
                state
        );
    }
}