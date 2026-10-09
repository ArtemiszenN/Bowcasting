package tseileinn.bowcasting.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tseileinn.bowcasting.Bowcasting;
import tseileinn.bowcasting.client.animation.BowcastingAnimationState;
import tseileinn.bowcasting.client.animation.BowcastingAnimationStateHolder;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Unique
    private static final float CROSSBOW_MULTIPLIER = 0.15f;
    @Unique
    private static final String STAGE_1_PATH = "textures/spell/stage1.png";
    @Unique
    private static final String STAGE_2_PATH = "textures/spell/stage2.png";
    @Unique
    private static final String STAGE_3_PATH = "textures/spell/stage3.png";

    @Unique
    private static void beginTexture(String path) {
        RenderSystem.setShader(CoreShaders.POSITION_TEX);

        RenderSystem.setShaderTexture(
                0,
                Bowcasting.id(path)
        );

        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.defaultBlendFunc();
    }

    @Unique
    private static void endTexture(PoseStack poseStack) {
        PoseStack.Pose pose = poseStack.last();

        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX
        );

        buffer.addVertex(pose.pose(), -0.5f, -0.5f, 0.0f)
                .setUv(0.0f, 1.0f);

        buffer.addVertex(pose.pose(), 0.5f, -0.5f, 0.0f)
                .setUv(1.0f, 1.0f);

        buffer.addVertex(pose.pose(), 0.5f, 0.5f, 0.0f)
                .setUv(1.0f, 0.0f);

        buffer.addVertex(pose.pose(), -0.5f, 0.5f, 0.0f)
                .setUv(0.0f, 0.0f);

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
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
    private static void renderStage1(
            PoseStack poseStack,
            BowcastingAnimationState state,
            float scaleMultiplier
    ) {
        beginTexture(STAGE_1_PATH);
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
        endTexture(poseStack);
    }

    @Unique
    private static void renderStage2(PoseStack poseStack, BowcastingAnimationState state, float scaleMultiplier) {
        beginTexture(STAGE_2_PATH);
        state.stage2Simulate();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) state.stage_2_rotation));
        poseStack.translate(0f, 0f, -state.stage_2_transform);
        float scale = state.stage_2_scale * scaleMultiplier;
        poseStack.scale(scale, scale, scale);
        drawWorldLight(poseStack, state, 1);
        endTexture(poseStack);
    }

    @Unique
    private static void renderStage3(PoseStack poseStack, BowcastingAnimationState state, float scaleMultiplier) {
        beginTexture(STAGE_3_PATH);
        state.stage3Simulate();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) state.stage_3_rotation));
        poseStack.translate(0f, 0f, -state.stage_3_transform);
        float scale = state.stage_3_scale * scaleMultiplier;
        poseStack.scale(scale, scale, scale);
        drawWorldLight(poseStack, state, 2);
        endTexture(poseStack);
    }

    @Shadow
    @Final
    private ItemStackRenderState scratchItemStackRenderState;

    @Inject(
            method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;" +
                    "Lnet/minecraft/world/item/ItemStack;" +
                    "Lnet/minecraft/world/item/ItemDisplayContext;" +
                    "Z" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lnet/minecraft/world/level/Level;" +
                    "III)V",
            at = @At("TAIL")
    )
    private void bowcasting$renderSpell(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ItemDisplayContext itemDisplayContext,
            boolean bl,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            Level level,
            int light,
            int overlay,
            int seed,
            CallbackInfo ci
    ) {
        if (itemDisplayContext != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                && itemDisplayContext != ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                && itemDisplayContext != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                && itemDisplayContext != ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            return;
        }

        if (itemStack.getItem() instanceof BowItem && Bowcasting.CONFIG.renderBowRune) {
            BowcastingAnimationState state =
                    ((BowcastingAnimationStateHolder) (Object) itemStack)
                            .bowcasting$getAnimationState();

            if (state.isDead()) {
                return;
            }

            poseStack.pushPose();

            scratchItemStackRenderState.transform().apply(bl, poseStack);

            poseStack.translate(-0.5F, 0.5F, 0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45F));
            poseStack.mulPose(Axis.XP.rotationDegrees(100F));

            poseStack.pushPose();
            renderStage1(poseStack, state, Bowcasting.CONFIG.scaleMultiplier1);
            poseStack.popPose();

            poseStack.pushPose();
            renderStage2(poseStack, state, Bowcasting.CONFIG.scaleMultiplier2);
            poseStack.popPose();

            poseStack.pushPose();
            renderStage3(poseStack, state, Bowcasting.CONFIG.scaleMultiplier3);
            poseStack.popPose();

            poseStack.popPose();

            state.endFrame();
        }

        if (itemStack.getItem() instanceof CrossbowItem && Bowcasting.CONFIG.renderCrossbowRune) {
            BowcastingAnimationState state =
                    ((BowcastingAnimationStateHolder) (Object) itemStack)
                            .bowcasting$getAnimationState();

            if (CrossbowItem.isCharged(itemStack)) {
                if (state.isDead()) {
                    state.crossbowChargedStartAnim(itemStack, null);
                }
                state.heartbeat();
            }

            if (state.isDead()) {
                return;
            }

            poseStack.pushPose();

            scratchItemStackRenderState.transform().apply(bl, poseStack);

            poseStack.translate(-0.2F, 0.2F, 0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45F));
            poseStack.mulPose(Axis.XP.rotationDegrees(100F));

            poseStack.pushPose();
            renderStage1(poseStack, state, CROSSBOW_MULTIPLIER * Bowcasting.CONFIG.xbowScaleMultiplier1);
            poseStack.popPose();

            poseStack.pushPose();
            renderStage2(poseStack, state, CROSSBOW_MULTIPLIER * Bowcasting.CONFIG.xbowScaleMultiplier2);
            poseStack.popPose();

            poseStack.pushPose();
            renderStage3(poseStack, state, CROSSBOW_MULTIPLIER * Bowcasting.CONFIG.xbowScaleMultiplier3);
            poseStack.popPose();

            poseStack.popPose();

            state.endFrame();
        }
    }
}