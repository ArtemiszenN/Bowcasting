package tseileinn.bowcasting.client;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.WeakHashMap;

public final class BowcastingItemLookup {
    private static final Map<ItemStackRenderState, ItemStack> ITEMS =
            new WeakHashMap<>();

    public static void remember(ItemStackRenderState state, ItemStack stack) {
        if (stack.getItem() instanceof BowItem
                || stack.getItem() instanceof CrossbowItem) {
            ITEMS.put(state, stack);
        } else {
            ITEMS.remove(state);
        }
    }

    public static ItemStack get(ItemStackRenderState state) {
        return ITEMS.get(state);
    }
}