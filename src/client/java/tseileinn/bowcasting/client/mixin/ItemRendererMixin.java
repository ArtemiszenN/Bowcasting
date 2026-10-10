package tseileinn.bowcasting.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tseileinn.bowcasting.Bowcasting;
import tseileinn.bowcasting.client.BowcastingSpellRenderer;
import tseileinn.bowcasting.client.animation.BowcastingAnimationState;
import tseileinn.bowcasting.client.animation.BowcastingAnimationStateHolder;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import tseileinn.bowcasting.client.BowcastingRenderHook;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin implements BowcastingSpellRenderer {
    @Unique
    private static final float CROSSBOW_MULTIPLIER = 0.15f;
    @Unique
    private static final String STAGE_1_PATH = "textures/spell/stage1.png";
    @Unique
    private static final String STAGE_2_PATH = "textures/spell/stage2.png";
    @Unique
    private static final String STAGE_3_PATH = "textures/spell/stage3.png";

    @Unique
    private static void drawTexture(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            String path
    ) {
        collector.submitCustomGeometry(
                poseStack,
                RenderType.energySwirl(Bowcasting.id(path), 0F, 0F),
                (pose, vertex) -> {
                    // Front
                    addRuneVertex(vertex, pose, -0.5f, -0.5f, 0, 1,  1);
                    addRuneVertex(vertex, pose,  0.5f, -0.5f, 1, 1,  1);
                    addRuneVertex(vertex, pose,  0.5f,  0.5f, 1, 0,  1);
                    addRuneVertex(vertex, pose, -0.5f,  0.5f, 0, 0,  1);

                    // Back
                    addRuneVertex(vertex, pose, -0.5f,  0.5f, 0, 0, -1);
                    addRuneVertex(vertex, pose,  0.5f,  0.5f, 1, 0, -1);
                    addRuneVertex(vertex, pose,  0.5f, -0.5f, 1, 1, -1);
                    addRuneVertex(vertex, pose, -0.5f, -0.5f, 0, 1, -1);
                }
        );
    }

    @Unique
    private static void addRuneVertex(
            VertexConsumer vertex,
            PoseStack.Pose pose,
            float x, float y,
            float u, float v,
            int normalZ
    ) {
        vertex.addVertex(pose, x, y, 0)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(pose, 0, 0, normalZ);
    }

    @Unique
    private static void drawWorldLight(PoseStack poseStack, BowcastingAnimationState state, int index) {
        Camera camera = Minecraft.getInstance()
                .gameRenderer
                .getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        Quaternionf cameraRotation = camera.rotation();
        Vector3f lightPos = new Vector3f(0, 0, 0);
        poseStack.last().pose().transformPosition(lightPos);
        lightPos.rotate(cameraRotation);
        Vec3 worldPos = cameraPos.add(
                lightPos.x(),
                lightPos.y(),
                lightPos.z()
        );
        state.lights[index].setPosition(
                worldPos.x,
                worldPos.y,
                worldPos.z
        );
    }

    @Unique
    private static void renderStage1(PoseStack poseStack, SubmitNodeCollector collector, BowcastingAnimationState state, float scaleMultiplier) {
        state.stage1Simulate();
        poseStack.mulPose(
                Axis.ZP.rotationDegrees((float) state.stage_1_rotation)
        );
        poseStack.translate(
                0f,
                0f,
                -state.stage_1_transform
        );
        float scale = state.stage_1_scale * scaleMultiplier;
        poseStack.scale(scale, scale, scale);
        drawWorldLight(poseStack, state, 0);
        drawTexture(poseStack, collector, STAGE_1_PATH);
    }

    @Unique
    private static void renderStage2(PoseStack poseStack, SubmitNodeCollector collector, BowcastingAnimationState state, float scaleMultiplier) {
        state.stage2Simulate();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) state.stage_2_rotation));
        poseStack.translate(0f, 0f, -state.stage_2_transform);
        float scale = state.stage_2_scale * scaleMultiplier;
        poseStack.scale(scale, scale, scale);
        drawWorldLight(poseStack, state, 1);
        drawTexture(poseStack, collector, STAGE_2_PATH);
    }

    @Unique
    private static void renderStage3(PoseStack poseStack, SubmitNodeCollector collector, BowcastingAnimationState state, float scaleMultiplier) {
        state.stage3Simulate();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) state.stage_3_rotation));
        poseStack.translate(0f, 0f, -state.stage_3_transform);
        float scale = state.stage_3_scale * scaleMultiplier;
        poseStack.scale(scale, scale, scale);
        drawWorldLight(poseStack, state, 2);
        drawTexture(poseStack, collector, STAGE_3_PATH);
    }


    @Override
    public void bowcasting$renderSpell(
            ItemStack itemStack,
            ItemDisplayContext context,
            PoseStack poseStack,
            SubmitNodeCollector collector
    ) {
        if (context != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                && context != ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                && context != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                && context != ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            return;
        }

        boolean bow = itemStack.getItem() instanceof BowItem;
        boolean crossbow = itemStack.getItem() instanceof CrossbowItem;

        if (!(bow && Bowcasting.CONFIG.renderBowRune)
                && !(crossbow && Bowcasting.CONFIG.renderCrossbowRune)) {
            return;
        }

        BowcastingAnimationState state =
                ((BowcastingAnimationStateHolder) (Object) itemStack)
                        .bowcasting$getAnimationState();

        if (crossbow && CrossbowItem.isCharged(itemStack)) {
            if (state.isDead()) {
                state.crossbowChargedStartAnim(itemStack, null);
            }
            state.heartbeat();
        }

        if (state.isDead()) {
            return;
        }

        float multiplier = bow ? 1.0F : CROSSBOW_MULTIPLIER;

        float scale1 = multiplier * (bow
                ? Bowcasting.CONFIG.scaleMultiplier1
                : Bowcasting.CONFIG.xbowScaleMultiplier1);

        float scale2 = multiplier * (bow
                ? Bowcasting.CONFIG.scaleMultiplier2
                : Bowcasting.CONFIG.xbowScaleMultiplier2);

        float scale3 = multiplier * (bow
                ? Bowcasting.CONFIG.scaleMultiplier3
                : Bowcasting.CONFIG.xbowScaleMultiplier3);

        poseStack.pushPose();
        float offset = bow ? 0.5F : 0.2F;
        float finalX = bow ? 0.75F : 0.7F;

        poseStack.translate(-offset, offset, 0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(45F));
        poseStack.mulPose(Axis.XP.rotationDegrees(100F));
        poseStack.translate(finalX, 0.4F, -0.5F);

        poseStack.pushPose();
        renderStage1(poseStack, collector, state, scale1);
        poseStack.popPose();

        poseStack.pushPose();
        renderStage2(poseStack, collector, state, scale2);
        poseStack.popPose();

        poseStack.pushPose();
        renderStage3(poseStack, collector, state, scale3);
        poseStack.popPose();

        poseStack.popPose();

        state.endFrame();
    }
}