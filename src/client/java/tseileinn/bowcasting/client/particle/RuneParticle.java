
package tseileinn.bowcasting.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import tseileinn.bowcasting.Bowcasting;

public class RuneParticle extends SingleQuadParticle {

    protected RuneParticle(
            ClientLevel level,
            double x, double y, double z,
            double xd, double yd, double zd,
            TextureAtlasSprite sprite
    ) {
        super(level, x, y, z, xd, yd, zd, sprite);

        this.friction = 1.0f;
        this.gravity = 0.0f;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        this.lifetime = 20;
        this.quadSize = 0.2f;
        this.alpha = 1.0f;

        this.roll = this.random.nextFloat() * ((float) Math.PI * 2f);
        this.oRoll = this.roll;
    }

    @Override
    public void tick() {
        super.tick();

        float progress = (float) this.age / (float) this.lifetime;

        // Rotate
        this.oRoll = this.roll;
        float rotationSpeed = 0.05f;
        this.roll += rotationSpeed;

        // Expand
        float expansionSpeed = 1.0f;
        float scaleProgress = 1.0f - (1.0f - progress) * (1.0f - progress);
        this.quadSize = 0.2f + expansionSpeed * scaleProgress;

        // Fade
        this.alpha = 1.0f - progress;
    }

    @Override
    public Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    protected int getLightCoords(float partialTick) {
        return 240;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x, double y, double z,
                double xd, double yd, double zd,
                RandomSource random
        ) {
            int version = Mth.clamp(
                    Bowcasting.CONFIG.runeParticleVersion,
                    1,
                    Bowcasting.BowcastingConfig.RUNE_PARTICLE_VERSIONS
            );

            TextureAtlasSprite sprite = sprites.get(
                    version - 1,
                    Bowcasting.BowcastingConfig.RUNE_PARTICLE_VERSIONS - 1
            );

            return new RuneParticle(level, x, y, z, xd, yd, zd, sprite);
        }
    }
}
