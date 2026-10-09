package tseileinn.bowcasting.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public final class BowcastingRenderHook {
    private static final ThreadLocal<Consumer<PoseStack>> ACTIVE = new ThreadLocal<>();

    public record RenderedItem(ItemStack stack, ItemDisplayContext context) {}

    private static final Map<ItemStackRenderState, RenderedItem> ITEMS =
            new WeakHashMap<>();

    public static void remember(
            ItemStackRenderState state,
            ItemStack stack,
            ItemDisplayContext context
    ) {
        ITEMS.put(state, new RenderedItem(stack, context));
    }

    public static RenderedItem lookup(ItemStackRenderState state) {
        return ITEMS.get(state);
    }

    public static Consumer<PoseStack> swap(Consumer<PoseStack> callback) {
        Consumer<PoseStack> previous = ACTIVE.get();

        if (callback == null) ACTIVE.remove();
        else ACTIVE.set(callback);

        return previous;
    }

    public static void draw(PoseStack poseStack) {
        Consumer<PoseStack> callback = ACTIVE.get();

        if (callback != null) {
            ACTIVE.remove(); // Only draw once, not once per model layer
            callback.accept(poseStack);
        }
    }
}