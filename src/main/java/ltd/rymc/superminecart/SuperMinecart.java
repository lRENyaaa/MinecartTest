package ltd.rymc.superminecart;

import ltd.rymc.superminecart.move.MoveLocation;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import net.minecraft.world.level.block.BlockMinecartTrackAbstract;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R4.util.CraftLocation;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SuperMinecart {

    private static Method METHOD_C = null;
    private static final int SPEED = 4;
    private static final int SIZE = 5;

    private final EntityMinecartAbstract handle;
    private final double originMaxSpeed;
    private final Vector originVelocity;

    private SuperMinecart nextSuperMinecart = null;
    private SuperMinecart previousSuperMinecart = null;

    private final MoveLocation[] moveLocationList = new MoveLocation[SIZE];
    private Location previousLocation = null;

    private MoveLocation leastUnDerailMoveLocation = null;

    public SuperMinecart(EntityMinecartAbstract handle){
        this.handle = handle;
        originMaxSpeed = handle.maxSpeed;
        originVelocity = handle.getBukkitEntity().getVelocity();
        handle.setFlyingVelocityMod(new Vector(10,0.1,10));
    }

    public EntityMinecartAbstract getHandle() {
        return handle;
    }

    public void updateDeltaMovement(){
        double speed = 100.0D;
        Vec3D deltaMovement = handle.ds();
        handle.o(deltaMovement.c * speed, deltaMovement.d * speed, deltaMovement.e * speed);
    }

    private void updateMaxSpeed(){
        handle.getBukkitEntity().setVelocity(nextSuperMinecart == null ? originVelocity : new Vector());
        handle.maxSpeed = nextSuperMinecart == null ? originMaxSpeed : 0;
        // handle.ag = nextSuperMinecart != null;
    }

    public void handleMove(VehicleMoveEvent event){
        if (nextSuperMinecart != null) {
            return;
        }

        updateMoveLocation(event.getFrom(), event.getTo());
        handleAllPreviousMinecartMove();

        if (!handle.ci()) {
            if (leastUnDerailMoveLocation == null) return;
            handle.a(EnumMoveType.a, leastUnDerailMoveLocation.getVec3D());
            return;
        }
        if (!passengersContainsPlayer()) return;

        for (int i = 1; i < SPEED; i++){
            move();
        }
    }

    void remove(){
        if (previousSuperMinecart != null){
            previousSuperMinecart.nextSuperMinecart = this.nextSuperMinecart;
            previousSuperMinecart.updateMaxSpeed();
        }

        if (nextSuperMinecart != null) {
            nextSuperMinecart.previousSuperMinecart = this.previousSuperMinecart;
            nextSuperMinecart.updateMaxSpeed();
        }
    }

    public void setPreviousSuperMinecart(SuperMinecart superMinecart) {
        if (isInMinecartChain(superMinecart)) return;
        this.previousSuperMinecart = superMinecart;
        superMinecart.nextSuperMinecart = this;
        superMinecart.updateMaxSpeed();
    }

    private void handleAllPreviousMinecartMove(){
        SuperMinecart previousSuperMinecart = this.previousSuperMinecart;
        while (previousSuperMinecart != null){
            previousSuperMinecart.previousMove();
            previousSuperMinecart = previousSuperMinecart.previousSuperMinecart;
        }
    }

    private void previousMove(){
        MoveLocation moveLocation = nextSuperMinecart.moveLocationList[moveLocationList.length - 1];

        if (moveLocation == null) return;
        Location to = moveLocation.getTo();
        double distance = new MoveLocation(getLocation(), to).getVec3D().f();
        if (distance > 1.1) {
            handle.a_(to.getX(), to.getY(), to.getZ());
            updateMoveLocation(moveLocation.getFrom(), to);
        }

    }

    private void move(){
        Vec3D vec3D = handle.dn();
        int x = MathHelper.a(vec3D.c);
        int y = MathHelper.a(vec3D.d);
        int z = MathHelper.a(vec3D.e);

        BlockPosition blockposition = new BlockPosition(x, y, z);
        IBlockData iblockdata = handle.dP().a_(blockposition);

        if (!(iblockdata.b() instanceof BlockMinecartTrackAbstract)) return;

        try {
            if (METHOD_C == null){
                METHOD_C = EntityMinecartAbstract.class.getDeclaredMethod("c", BlockPosition.class, IBlockData.class);
                METHOD_C.setAccessible(true);
            }
            METHOD_C.invoke(handle, blockposition, iblockdata);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        updateDeltaMovement();

        Location to = getLocation();
        updateMoveLocation(previousLocation, to);

        handleAllPreviousMinecartMove();
    }

    private boolean passengersContainsPlayer(){
        for (Entity entity : handle.cS()) {
            if (entity instanceof EntityHuman) return true;
        }
        return false;
    }

    private void updateMoveLocation(Location from, Location to){
        for (int i = moveLocationList.length - 2 ; i >= 0; i--){
            moveLocationList[i + 1] = moveLocationList[i];
        }
        MoveLocation moveLocation = new MoveLocation(from, to);
        moveLocationList[0] = moveLocation;
        if (handle.ci()) leastUnDerailMoveLocation = moveLocation;
        previousLocation = to;
    }

    public boolean hasPreviousMinecart(){
        return previousSuperMinecart != null;
    }

    public boolean hasNextMinecart(){
        return nextSuperMinecart != null;
    }

    private Location getLocation(){
        return CraftLocation.toBukkit(handle.dn(), handle.dP().getWorld(), handle.getBukkitYaw(), handle.dH());
    }

    public boolean isInMinecartChain(SuperMinecart superMinecart){
        SuperMinecart nextSuperMinecart = this.nextSuperMinecart;
        while (nextSuperMinecart != null){
            if (nextSuperMinecart.equals(superMinecart)) return true;
            nextSuperMinecart = nextSuperMinecart.nextSuperMinecart;
        }

        SuperMinecart previousSuperMinecart = this.previousSuperMinecart;
        while (previousSuperMinecart != null){
            if (previousSuperMinecart.equals(superMinecart)) return true;
            previousSuperMinecart = previousSuperMinecart.previousSuperMinecart;
        }

        return false;
    }



}
