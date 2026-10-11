package tseileinn.bowcasting.client.render;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface BowcastingEntityRenderState {
    @Nullable
    LivingEntity bowcasting$getEntity();

    void bowcasting$setEntity(@Nullable LivingEntity entity);
}
