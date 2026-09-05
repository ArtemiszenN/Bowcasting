package tseileinn.bowcasting.client;

import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import tseileinn.bowcasting.Bowcasting;

public class BowcastingDynamicLight implements DynamicLightBehavior {

    private double x;
    private double y;
    private double z;

    private boolean changed = true;

    public BowcastingDynamicLight(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BowcastingDynamicLight(){}

    public void setPosition(double x, double y, double z) {
        if (this.x == x && this.y == y && this.z == z) {
            return;
        }

        this.x = x;
        this.y = y;
        this.z = z;
        this.changed = true;
    }

    @Override
    public double lightAtPos(BlockPos pos, double falloffRatio) {
        double dx = pos.getX() + 0.5 - x;
        double dy = pos.getY() + 0.5 - y;
        double dz = pos.getZ() + 0.5 - z;

        double distance = Math.sqrt(
                dx * dx +
                        dy * dy +
                        dz * dz
        );

        double falloff = 0.35;

        return Math.max(
                15.0 - distance * falloffRatio * falloff,
                0.0
        );
    }

    @Override
    public @NotNull BoundingBox getBoundingBox() {
        int bx = (int) Math.floor(x);
        int by = (int) Math.floor(y);
        int bz = (int) Math.floor(z);
        return new BoundingBox(
                bx, by, bz,
                bx + 1, by + 1, bz + 1
        );
    }

    @Override
    public boolean hasChanged() {
        boolean result = changed;
        changed = false;
        return result;
    }
}