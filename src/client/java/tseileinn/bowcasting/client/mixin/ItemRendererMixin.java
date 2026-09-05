package tseileinn.bowcasting.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
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
    private static final String STAGE_1_PATH = "textures/spell/stage1.png";
    @Unique
    private static final String STAGE_2_PATH = "textures/spell/stage2.png";
    @Unique
    private static final String STAGE_3_PATH = "textures/spell/stage3.png";
    @Unique
    private static LivingEntity bowcasting$currentEntity;

    @Unique
    private static void renderGizmo(PoseStack poseStack) {
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);

        PoseStack.Pose pose = poseStack.last();

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        float length = 0.5f;

        // X = red
        buffer.vertex(pose.pose(), 0.0f, 0.0f, 0.0f)
                .color(255, 0, 0, 255)
                .endVertex();

        buffer.vertex(pose.pose(), length, 0.0f, 0.0f)
                .color(255, 0, 0, 255)
                .endVertex();

        // Y = green
        buffer.vertex(pose.pose(), 0.0f, 0.0f, 0.0f)
                .color(0, 255, 0, 255)
                .endVertex();

        buffer.vertex(pose.pose(), 0.0f, length, 0.0f)
                .color(0, 255, 0, 255)
                .endVertex();

        // Z = blue
        buffer.vertex(pose.pose(), 0.0f, 0.0f, 0.0f)
                .color(0, 0, 255, 255)
                .endVertex();

        buffer.vertex(pose.pose(), 0.0f, 0.0f, length)
                .color(0, 0, 255, 255)
                .endVertex();

        Tesselator.getInstance().end();
    }

    @Unique
    private static void beginTexture(String path){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
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
    private static void endTexture(PoseStack poseStack){
        PoseStack.Pose pose = poseStack.last();

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX
        );
        buffer.vertex(pose.pose(), -0.5f, -0.5f, 0.0f)
                .uv(0.0f, 1.0f)
                .endVertex();

        buffer.vertex(pose.pose(), 0.5f, -0.5f, 0.0f)
                .uv(1.0f, 1.0f)
                .endVertex();

        buffer.vertex(pose.pose(), 0.5f, 0.5f, 0.0f)
                .uv(1.0f, 0.0f)
                .endVertex();

        buffer.vertex(pose.pose(), -0.5f, 0.5f, 0.0f)
                .uv(0.0f, 0.0f)
                .endVertex();

        Tesselator.getInstance().end();

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }


    @Unique
    private static void renderStage1(
            PoseStack poseStack,
            BowcastingAnimationState state,
            Vec3 entityPos
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

        float scale = state.stage_1_scale * Bowcasting.CONFIG.scaleMultiplier;

        poseStack.scale(scale, scale, scale);

        Camera camera = Minecraft.getInstance()
                .gameRenderer
                .getMainCamera();

        Vec3 cameraPos = camera.getPosition();
        Quaternionf cameraRotation = camera.rotation();

        Vector3f lightpos = new Vector3f(0, 0, 0);
        poseStack.last().pose().transformPosition(lightpos);

        lightpos.rotate(cameraRotation);

        Vec3 worldpos = cameraPos.add(
                lightpos.x(),
                lightpos.y(),
                lightpos.z()
        );

        state.lights[0].setPosition(
                worldpos.x,
                worldpos.y,
                worldpos.z
        );

        endTexture(poseStack);
    }

    @Unique
    private static void renderStage2(PoseStack poseStack, BowcastingAnimationState state) {
        beginTexture(STAGE_2_PATH);
        state.stage2Simulate();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) state.stage_2_rotation));
        poseStack.translate(0f, 0f, -state.stage_2_transform);
        poseStack.scale(state.stage_2_scale * Bowcasting.CONFIG.scaleMultiplier, state.stage_2_scale* Bowcasting.CONFIG.scaleMultiplier, state.stage_2_scale* Bowcasting.CONFIG.scaleMultiplier);

        Camera camera = Minecraft.getInstance()
                .gameRenderer
                .getMainCamera();

        Vec3 cameraPos = camera.getPosition();
        Quaternionf cameraRotation = camera.rotation();

        Vector3f lightpos = new Vector3f(0, 0, 0);
        poseStack.last().pose().transformPosition(lightpos);

        lightpos.rotate(cameraRotation);

        Vec3 worldpos = cameraPos.add(
                lightpos.x(),
                lightpos.y(),
                lightpos.z()
        );

        state.lights[1].setPosition(
                worldpos.x,
                worldpos.y,
                worldpos.z
        );

        endTexture(poseStack);
    }

    @Unique
    private static void renderStage3(PoseStack poseStack, BowcastingAnimationState state) {
        beginTexture(STAGE_3_PATH);
        state.stage3Simulate();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) state.stage_3_rotation));
        poseStack.translate(0f, 0f, -state.stage_3_transform);
        poseStack.scale(state.stage_3_scale* Bowcasting.CONFIG.scaleMultiplier, state.stage_3_scale *  Bowcasting.CONFIG.scaleMultiplier, state.stage_3_scale* Bowcasting.CONFIG.scaleMultiplier);

        Camera camera = Minecraft.getInstance()
                .gameRenderer
                .getMainCamera();

        Vec3 cameraPos = camera.getPosition();
        Quaternionf cameraRotation = camera.rotation();

        Vector3f lightpos = new Vector3f(0, 0, 0);
        poseStack.last().pose().transformPosition(lightpos);

        lightpos.rotate(cameraRotation);

        Vec3 worldpos = cameraPos.add(
                lightpos.x(),
                lightpos.y(),
                lightpos.z()
        );

        state.lights[0].setPosition(
                worldpos.x,
                worldpos.y,
                worldpos.z
        );

        endTexture(poseStack);
    }

    @Inject(
            method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;" +
                    "Lnet/minecraft/world/item/ItemStack;" +
                    "Lnet/minecraft/world/item/ItemDisplayContext;" +
                    "Z" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;" +
                    "Lnet/minecraft/world/level/Level;" +
                    "II" +
                    "I)V",
            at = @At("HEAD")
    )
    private void bowcasting$captureEntity(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ItemDisplayContext itemDisplayContext,
            boolean bl,
            PoseStack poseStack,
            MultiBufferSource buffers,
            Level level,
            int light,
            int overlay,
            int i,
            CallbackInfo ci
    ) {
        bowcasting$currentEntity = livingEntity;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/model/ItemTransform;apply(ZLcom/mojang/blaze3d/vertex/PoseStack;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void bowcasting$renderSpell(
            ItemStack itemStack,
            ItemDisplayContext itemDisplayContext,
            boolean bl,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int light,
            int overlay,
            BakedModel bakedModel,
            CallbackInfo ci
    ) {
        if (!(itemStack.getItem() instanceof BowItem)) {
            return;
        }

        if (itemDisplayContext != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                && itemDisplayContext != ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                && itemDisplayContext != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                && itemDisplayContext != ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            return;
        }

        BowcastingAnimationState state =
                ((BowcastingAnimationStateHolder) (Object) itemStack)
                        .bowcasting$getAnimationState();

        if (state.isDead()) {
            return;
        }

        if (System.nanoTime() - state.last_heartbeat > 100_000_000L) {
            state.stopAnim();
            return;
        }

        poseStack.pushPose();

        poseStack.translate(-0.5F, 0.5F, 0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(45F));
        poseStack.mulPose(Axis.XP.rotationDegrees(100F));
        //renderGizmo(poseStack);
        poseStack.pushPose();
        renderStage1(poseStack, state, bowcasting$currentEntity.position());
        poseStack.popPose();
        poseStack.pushPose();
        renderStage2(poseStack, state);
        poseStack.popPose();
        poseStack.pushPose();
        renderStage3(poseStack, state);
        poseStack.popPose();

        poseStack.popPose();

        state.endFrame();
    }
}