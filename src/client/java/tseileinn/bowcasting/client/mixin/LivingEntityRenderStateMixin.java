package tseileinn.bowcasting.client.mixin;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import tseileinn.bowcasting.client.render.BowcastingEntityRenderState;

@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateMixin
        implements BowcastingEntityRenderState {

    @Unique
    @Nullable
    private LivingEntity bowcasting$entity;

    @Override
    public LivingEntity bowcasting$getEntity() {
        return bowcasting$entity;
    }

    @Override
    public void bowcasting$setEntity(@Nullable LivingEntity entity) {
        bowcasting$entity = entity;
    }
}
