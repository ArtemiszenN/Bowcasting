package tseileinn.bowcasting.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
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
            MultiBufferSource buffers,
            String path
    ) {
        VertexConsumer vertex = buffers.getBuffer(
                //RenderType.entityTranslucentEmissive(Bowcasting.id(path))
                RenderType.eyes(Bowcasting.id(path))
        );

        PoseStack.Pose pose = poseStack.last();

    // Front face
        addRuneVertex(vertex, pose, -0.5f, -0.5f, 0, 1,  1);
        addRuneVertex(vertex, pose,  0.5f, -0.5f, 1, 1,  1);
        addRuneVertex(vertex, pose,  0.5f,  0.5f, 1, 0,  1);
        addRuneVertex(vertex, pose, -0.5f,  0.5f, 0, 0,  1);

    // Back face (reversed winding)
        addRuneVertex(vertex, pose, -0.5f,  0.5f, 0, 0, -1);
        addRuneVertex(vertex, pose,  0.5f,  0.5f, 1, 0, -1);
        addRuneVertex(vertex, pose,  0.5f, -0.5f, 1, 1, -1);
        addRuneVertex(vertex, pose, -0.5f, -0.5f, 0, 1, -1);
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
    private static void renderStage1(PoseStack poseStack, MultiBufferSource buffers, BowcastingAnimationState state, float scaleMultiplier) {
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
        drawTexture(poseStack, buffers, STAGE_1_PATH);
    }

    @Unique
    private static void renderStage2(PoseStack poseStack, MultiBufferSource buffers, BowcastingAnimationState state, float scaleMultiplier) {
        state.stage2Simulate();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) state.stage_2_rotation));
        poseStack.translate(0f, 0f, -state.stage_2_transform);
        float scale = state.stage_2_scale * scaleMultiplier;
        poseStack.scale(scale, scale, scale);
        drawWorldLight(poseStack, state, 1);
        drawTexture(poseStack, buffers, STAGE_2_PATH);
    }

    @Unique
    private static void renderStage3(PoseStack poseStack, MultiBufferSource buffers, BowcastingAnimationState state, float scaleMultiplier) {
        state.stage3Simulate();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) state.stage_3_rotation));
        poseStack.translate(0f, 0f, -state.stage_3_transform);
        float scale = state.stage_3_scale * scaleMultiplier;
        poseStack.scale(scale, scale, scale);
        drawWorldLight(poseStack, state, 2);
        drawTexture(poseStack, buffers, STAGE_3_PATH);
    }

    @Unique
    private static void renderGizmo(
            PoseStack poseStack,
            MultiBufferSource buffers
    ) {
        VertexConsumer vertex = buffers.getBuffer(RenderType.lines());
        PoseStack.Pose pose = poseStack.last();

        float length = 0.5f;

        // X = red
        gizmoLine(vertex, pose, length, 0, 0, 255, 0, 0);

        // Y = green
        gizmoLine(vertex, pose, 0, length, 0, 0, 255, 0);

        // Z = blue
        gizmoLine(vertex, pose, 0, 0, length, 0, 0, 255);
    }

    @Unique
    private static void gizmoLine(
            VertexConsumer vertex,
            PoseStack.Pose pose,
            float x, float y, float z,
            int r, int g, int b
    ) {
        vertex.addVertex(pose, 0, 0, 0)
                .setColor(r, g, b, 255)
                .setNormal(pose, x, y, z);

        vertex.addVertex(pose, x, y, z)
                .setColor(r, g, b, 255)
                .setNormal(pose, x, y, z);
    }

    @Override
    public void bowcasting$renderSpell(
            ItemStack itemStack,
            ItemDisplayContext context,
            PoseStack poseStack,
            MultiBufferSource buffers
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
        poseStack.translate(finalX, 0.5F, 0F);

        poseStack.pushPose();
        renderStage1(poseStack, buffers, state, scale1);
        poseStack.popPose();

        poseStack.pushPose();
        renderStage2(poseStack, buffers, state, scale2);
        poseStack.popPose();

        poseStack.pushPose();
        renderStage3(poseStack, buffers, state, scale3);
        poseStack.popPose();

        poseStack.popPose();

        state.endFrame();
    }

    @WrapOperation(
            method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;" +
                    "Lnet/minecraft/world/item/ItemStack;" +
                    "Lnet/minecraft/world/item/ItemDisplayContext;" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lnet/minecraft/world/level/Level;III)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;" +
                            "render(Lcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lnet/minecraft/client/renderer/MultiBufferSource;II)V"
            )
    )
    private void bowcasting$withRenderContext(
            ItemStackRenderState renderState,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int light,
            int overlay,
            Operation<Void> original,
            LivingEntity entity,
            ItemStack itemStack,
            ItemDisplayContext context,
            PoseStack originalPose,
            MultiBufferSource originalBuffers,
            Level level,
            int originalLight,
            int originalOverlay,
            int seed
    ) {
        if (!(itemStack.getItem() instanceof BowItem)
                && !(itemStack.getItem() instanceof CrossbowItem)) {
            original.call(renderState, poseStack, buffers, light, overlay);
            return;
        }

        var previous = BowcastingRenderHook.swap(
                ps -> bowcasting$renderSpell(itemStack, context, ps, buffers)
        );

        try {
            original.call(renderState, poseStack, buffers, light, overlay);
        } finally {
            BowcastingRenderHook.swap(previous);
        }
    }
}