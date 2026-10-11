
package tseileinn.bowcasting.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import tseileinn.bowcasting.client.BowcastingRenderHook;
import tseileinn.bowcasting.client.BowcastingSpellRendererImpl;
import tseileinn.bowcasting.client.ItemStackResolver;

@Pseudo
@Mixin(
        targets = "punchy.client.render.PunchyArmRenderer",
        remap = false
)
public abstract class PunchyArmRendererMixin {
    @WrapOperation(
            method = "submitVanillaItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;" +
                            "submit(Lcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private static void bowcasting$punchyFirstPerson(
            ItemStackRenderState renderState,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            int overlay,
            int outlineColor,
            Operation<Void> original,
            LocalPlayer player,
            ItemStack itemStack,
            ItemDisplayContext context,
            PoseStack originalPose,
            SubmitNodeCollector originalCollector,
            int originalLight
    ) {
        if (!(itemStack.getItem() instanceof BowItem)
                && !(itemStack.getItem() instanceof CrossbowItem)) {
            original.call(renderState, poseStack, collector,
                    light, overlay, outlineColor);
            return;
        }

        ItemStack owner = ItemStackResolver.getStateOwner(
                itemStack,
                context,
                player
        );

        var previous = BowcastingRenderHook.swap(
                ps -> BowcastingSpellRendererImpl.INSTANCE
                        .bowcasting$renderSpell(
                                owner,
                                context,
                                ps,
                                collector
                        )
        );

        try {
            original.call(renderState, poseStack, collector,
                    light, overlay, outlineColor);
        } finally {
            BowcastingRenderHook.swap(previous);
        }
    }
}
