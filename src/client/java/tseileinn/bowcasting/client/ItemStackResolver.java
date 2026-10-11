package tseileinn.bowcasting.client;

import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Unique;

public class ItemStackResolver {
    @Unique
    public static ItemStack getStateOwner(
            ItemStack rendered,
            ItemDisplayContext context,
            LivingEntity entity
    ) {
        if (entity == null) {
            return rendered;
        }

        HumanoidArm arm = switch (context) {
            case FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_RIGHT_HAND ->
                    HumanoidArm.RIGHT;
            case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND ->
                    HumanoidArm.LEFT;
            default -> null;
        };

        if (arm == null) {
            return rendered;
        }

        ItemStack actual = entity.getItemHeldByArm(arm);

        return ItemStack.isSameItemSameComponents(actual, rendered)
                ? actual
                : rendered;
    }
}
