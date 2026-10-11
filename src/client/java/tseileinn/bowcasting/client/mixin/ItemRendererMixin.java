package tseileinn.bowcasting.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import tseileinn.bowcasting.client.animation.BowcastingAnimationState;
import tseileinn.bowcasting.client.animation.BowcastingHandAnimationStateHolder;
import tseileinn.bowcasting.client.render.BowcastingSpellRenderer;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Inject(
            method = "renderStatic("
                    + "Lnet/minecraft/world/entity/LivingEntity;"
                    + "Lnet/minecraft/world/item/ItemStack;"
                    + "Lnet/minecraft/world/item/ItemDisplayContext;"
                    + "Z"
                    + "Lcom/mojang/blaze3d/vertex/PoseStack;"
                    + "Lnet/minecraft/client/renderer/MultiBufferSource;"
                    + "Lnet/minecraft/world/level/Level;"
                    + "III)V",
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
    private void bowcasting$renderFirstPerson(
            @Nullable LivingEntity entity,
            ItemStack stack,
            ItemDisplayContext context,
            boolean leftHand,
            PoseStack poseStack,
            MultiBufferSource buffers,
            @Nullable Level level,
            int light,
            int overlay,
            int seed,
            CallbackInfo ci,
            @Local BakedModel bakedModel
    ) {
        if (entity == null) {
            return;
        }

        HumanoidArm arm;

        switch (context) {
            case FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_RIGHT_HAND ->
                    arm = HumanoidArm.RIGHT;

            case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND ->
                    arm = HumanoidArm.LEFT;

            default -> {
                return;
            }
        }

        BowcastingAnimationState state =
                ((BowcastingHandAnimationStateHolder) entity)
                        .bowcasting$getAnimationState(arm);

        BowcastingSpellRenderer.render(
                stack,
                context,
                leftHand,
                poseStack,
                bakedModel,
                state
        );
    }
}
