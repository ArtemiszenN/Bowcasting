package tseileinn.bowcasting.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface BowcastingSpellRenderer {
    void bowcasting$drawSpell(
            ItemStack stack,
            ItemDisplayContext context,
            PoseStack poseStack,
            ItemTransform transform,
            boolean leftHand
    );
}