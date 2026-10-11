package tseileinn.bowcasting.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import tseileinn.bowcasting.Bowcasting;
import tseileinn.bowcasting.client.animation.BowcastingAnimationState;

public class BowcastingSpellRenderer {
    private static final float CROSSBOW_MULTIPLIER = 0.15f;
    private static final String STAGE_1_PATH = "textures/spell/stage1.png";
    private static final String STAGE_2_PATH = "textures/spell/stage2.png";
    private static final String STAGE_3_PATH = "textures/spell/stage3.png";

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


    public static void render(
            ItemStack itemStack,
            ItemDisplayContext context,
            boolean leftHand,
            PoseStack poseStack,
            BakedModel bakedModel,
            BowcastingAnimationState state
    ) {
        boolean bow = itemStack.getItem() instanceof BowItem
                && Bowcasting.CONFIG.renderBowRune;

        boolean crossbow = itemStack.getItem() instanceof CrossbowItem
                && Bowcasting.CONFIG.renderCrossbowRune;

        if (!bow && !crossbow) {
            return;
        }

        if (crossbow && CrossbowItem.isCharged(itemStack)) {
            if (state.isDead()) {
                state.crossbowChargedStartAnim(itemStack, null);
            }
            state.heartbeat();
        }

        if (state.isDead()) {
            return;
        }

        poseStack.pushPose();
        try {
            bakedModel.getTransforms()
                    .getTransform(context)
                    .apply(leftHand, poseStack);

            if (bow) {
                poseStack.translate(-0.5F, 0.5F, 0F);
            } else {
                poseStack.translate(-0.2F, 0.2F, 0F);
            }

            poseStack.mulPose(Axis.ZP.rotationDegrees(45F));
            poseStack.mulPose(Axis.XP.rotationDegrees(90F));

            float multiplier = crossbow ? CROSSBOW_MULTIPLIER : 1.0F;

            float s1 = multiplier * (bow
                    ? Bowcasting.CONFIG.scaleMultiplier1
                    : Bowcasting.CONFIG.xbowScaleMultiplier1);

            float s2 = multiplier * (bow
                    ? Bowcasting.CONFIG.scaleMultiplier2
                    : Bowcasting.CONFIG.xbowScaleMultiplier2);

            float s3 = multiplier * (bow
                    ? Bowcasting.CONFIG.scaleMultiplier3
                    : Bowcasting.CONFIG.xbowScaleMultiplier3);

            poseStack.pushPose();
            renderStage1(poseStack, state, s1);
            poseStack.popPose();

            poseStack.pushPose();
            renderStage2(poseStack, state, s2);
            poseStack.popPose();

            poseStack.pushPose();
            renderStage3(poseStack, state, s3);
            poseStack.popPose();
        } finally {
            poseStack.popPose();
        }

        state.endFrame();
    }

}
