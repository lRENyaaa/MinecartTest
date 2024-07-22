package ltd.rymc.superminecart.move;

import ltd.rymc.minecarttest.MinecartTest;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;

public class MoveLocation {

    private final Location from;
    private final Location to;

    public MoveLocation(Location from, Location to){
        this.from = from;
        this.to = to;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public Vec3D getVec3D(){
        return new Vec3D(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ());
    }

    public static Vec3D fixVecY(Vec3D vec3D, double num){
        double fixedY = vec3D.d + (vec3D.d > 0.01 ? num : 0);
        MinecartTest.getInstance().getLogger().info(vec3D.c + ", " + fixedY + ", " + vec3D.e);
        return new Vec3D(vec3D.c, fixedY, vec3D.e);
    }
}
