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
}
