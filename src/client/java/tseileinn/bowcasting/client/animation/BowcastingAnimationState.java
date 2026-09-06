package tseileinn.bowcasting.client.animation;

import net.minecraft.util.Mth;
import tseileinn.bowcasting.Bowcasting;
import tseileinn.bowcasting.client.BowcastingClient;
import tseileinn.bowcasting.client.BowcastingDynamicLight;
import tseileinn.bowcasting.client.BowcastingDynamicLightsInitializer;
import tseileinn.bowcasting.client.BowcastingLightInterface;


public class BowcastingAnimationState {
    public static final float SCALE_DEFAULT = 5.5f;
    public static final float STAGE_1_SCALE_START = 0f;
    public static final float STAGE_1_ROTATION_RATE = 45f;
    public static final float STAGE_2_SCALE_START = 0f;
    public static final float STAGE_2_ROTATION_RATE = -90f;
    public static final float STAGE_2_TRANSFORM_START = 1f;
    public static final float STAGE_2_TRANSFORM_END = 0.5f;
    public static final float STAGE_3_ROTATION_RATE = 180f;
    public static final float STAGE_3_TRANSFORM_START = 5f;
    public static final float STAGE_3_TRANSFORM_END = 2f;
    public long charge_start_time = -1;
    public long last_frame_time = -1;
    // I use time instead of charge progress for smoother animations, even if it doesn't match completely
    public static final double STAGE_2_START_TIME = 0.33f;
    public static final double STAGE_3_START_TIME = 0.67f;
    public static final double CHARGE_TIME = 1f;
    public static final double STAGE_1_DURATION = STAGE_2_START_TIME;
    public static final double STAGE_2_DURATION = STAGE_3_START_TIME - STAGE_2_START_TIME;
    public static final double STAGE_3_DURATION = CHARGE_TIME - STAGE_3_START_TIME;
    public float stage_1_scale = STAGE_1_SCALE_START;
    public double stage_1_rotation = 0f;
    public double stage_1_transform = 0.1f;
    public float stage_2_scale = STAGE_2_SCALE_START;
    public double stage_2_rotation = 0f;
    public double stage_2_transform = 0.1f;
    public float stage_3_scale = SCALE_DEFAULT;
    public double stage_3_rotation = 0f;
    public double stage_3_transform = 0.1f;
    public long last_heartbeat = -1;

    public final BowcastingLightInterface[] lights = {
            BowcastingClient.LIGHT_FACTORY.create(),
            BowcastingClient.LIGHT_FACTORY.create(),
            BowcastingClient.LIGHT_FACTORY.create()
    };


    private static double elapsedSeconds(long start){
        return (System.nanoTime() - start) / 1_000_000_000.0;
    }

    public boolean isDead(){
        return last_frame_time == -1;
    }

    public void stopAnim() {
        charge_start_time = -1;
        last_frame_time = -1;
        stage_1_rotation = 0f;
        stage_1_scale = STAGE_1_SCALE_START;
        for (BowcastingLightInterface light : lights){
            light.remove();
        }
    }

    public void startAnim(){
        charge_start_time = System.nanoTime();
        last_frame_time = System.nanoTime();
        heartbeat();
        lights[0].add();
    }

    public void endFrame(){
        last_frame_time = System.nanoTime();
    }

    public void heartbeat(){
        last_heartbeat = System.nanoTime();
    }

    public void stage1Simulate(){
        if (elapsedSeconds(charge_start_time) <= STAGE_2_START_TIME){
            stage_1_rotation += elapsedSeconds(last_frame_time) * STAGE_1_ROTATION_RATE * Bowcasting.CONFIG.rotationSpeedMultiplier;
            stage_1_scale = (float) Mth.lerp(elapsedSeconds(charge_start_time)/STAGE_1_DURATION, STAGE_1_SCALE_START, SCALE_DEFAULT);
        }else{
            stage_1_rotation += elapsedSeconds(last_frame_time) * STAGE_1_ROTATION_RATE* Bowcasting.CONFIG.rotationSpeedMultiplier;
            stage_1_scale = SCALE_DEFAULT;
        }
    }

    public void stage2Simulate(){
        if (elapsedSeconds(charge_start_time) < STAGE_2_START_TIME){
            stage_2_scale = 0f;
        }else if(elapsedSeconds(charge_start_time) < STAGE_3_START_TIME){
            lights[1].add();
            stage_2_rotation = 0f;
            stage_2_scale = (float) Mth.lerp((elapsedSeconds(charge_start_time) - STAGE_2_START_TIME)/STAGE_2_DURATION, STAGE_2_SCALE_START, SCALE_DEFAULT);
            stage_2_transform = (float) Mth.lerp((elapsedSeconds(charge_start_time) - STAGE_2_START_TIME)/STAGE_2_DURATION, STAGE_2_TRANSFORM_START, STAGE_2_TRANSFORM_END);
        }else{
            stage_2_transform = STAGE_2_TRANSFORM_END;
            stage_2_scale = SCALE_DEFAULT;
            stage_2_rotation += elapsedSeconds(last_frame_time) * STAGE_2_ROTATION_RATE * Bowcasting.CONFIG.rotationSpeedMultiplier;
        }
    }

    public void stage3Simulate(){
        if (elapsedSeconds(charge_start_time) < STAGE_3_START_TIME){
            stage_3_scale = 0f;
        }else if(elapsedSeconds(charge_start_time) < CHARGE_TIME){
            lights[2].add();
            stage_3_rotation = 0f;
            stage_3_scale = SCALE_DEFAULT;
            stage_3_transform = (float) Mth.lerp((elapsedSeconds(charge_start_time) - STAGE_3_START_TIME)/STAGE_3_DURATION, STAGE_3_TRANSFORM_START, STAGE_3_TRANSFORM_END);
        }else{
            stage_3_transform = STAGE_3_TRANSFORM_END;
            stage_3_scale = SCALE_DEFAULT;
            stage_3_rotation += elapsedSeconds(last_frame_time) * STAGE_3_ROTATION_RATE * Bowcasting.CONFIG.rotationSpeedMultiplier;
        }
    }
}