package tseileinn.bowcasting.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tseileinn.bowcasting.client.BowcastingRenderHook;
import tseileinn.bowcasting.client.BowcastingSpellRenderer;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {
    @WrapOperation(
            method = "renderArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;" +
                            "render(Lcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lnet/minecraft/client/renderer/MultiBufferSource;II)V"
            )
    )
    private void bowcasting$thirdPerson(
            ItemStackRenderState instance,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int i,
            int j,
            Operation<Void> original
    ) {
        var item = BowcastingRenderHook.lookup(instance);

        if (item == null
                || !(item.stack().getItem() instanceof BowItem
                || item.stack().getItem() instanceof CrossbowItem)) {
            original.call(instance, poseStack, multiBufferSource, i, j);
            return;
        }

        var renderer = (BowcastingSpellRenderer)
                Minecraft.getInstance().getItemRenderer();

        var previous = BowcastingRenderHook.swap(
                ps -> renderer.bowcasting$renderSpell(
                        item.stack(), item.context(), ps, multiBufferSource)
        );

        try {
            original.call(instance, poseStack, multiBufferSource, i, j);
        } finally {
            BowcastingRenderHook.swap(previous);
        }
    }
}