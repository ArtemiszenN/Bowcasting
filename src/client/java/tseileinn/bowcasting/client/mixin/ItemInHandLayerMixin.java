package tseileinn.bowcasting.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tseileinn.bowcasting.client.BowcastingRenderHook;
import tseileinn.bowcasting.client.BowcastingSpellRenderer;
import tseileinn.bowcasting.client.BowcastingSpellRendererImpl;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {
    @WrapOperation(
            method = "submitArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;" +
                            "submit(Lcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
            )
    )
    private void bowcasting$thirdPerson(
            ItemStackRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            int overlay,
            int outlineColor,
            Operation<Void> original
    ) {
        var item = BowcastingRenderHook.lookup(state);

        if (item == null
                || !(item.stack().getItem() instanceof BowItem
                || item.stack().getItem() instanceof CrossbowItem)) {
            original.call(state, poseStack, collector,
                    light, overlay, outlineColor);
            return;
        }

        var renderer = BowcastingSpellRendererImpl.INSTANCE;

        var previous = BowcastingRenderHook.swap(
                ps -> renderer.bowcasting$renderSpell(
                        item.stack(), item.context(), ps, collector)
        );

        try {
            original.call(state, poseStack, collector,
                    light, overlay, outlineColor);
        } finally {
            BowcastingRenderHook.swap(previous);
        }
    }
}