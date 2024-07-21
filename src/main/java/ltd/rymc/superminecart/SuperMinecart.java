package ltd.rymc.superminecart;

import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import net.minecraft.world.level.block.BlockMinecartTrackAbstract;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SuperMinecart {

    private static Method METHOD_C = null;
    private static final int SPEED = 40;

    private final EntityMinecartAbstract handle;

    private SuperMinecart nextSuperMinecart = null;
    private Runnable runnableOnRemove = null;

    public SuperMinecart(EntityMinecartAbstract handle){
        this.handle = handle;
    }

    public EntityMinecartAbstract getHandle() {
        return handle;
    }

    public void updateDeltaMovement(){
        double speed = 100.0D;
        Vec3D deltaMovement = handle.ds();
        handle.o(deltaMovement.c * speed, deltaMovement.d * speed, deltaMovement.e * speed);
    }

    public void handleMove(){
        if (!handle.ci()) return;
        if (!passengersContainsPlayer()) return;

        for (int i = 1; i < SPEED; i++){
            move();
        }
    }

    void remove(){
        runnableOnRemove.run();
    }

    private void setRunnableOnRemove(Runnable runnable){
        runnableOnRemove = runnable;
    }

    public void setNextSuperMinecart(SuperMinecart superMinecart) {
        this.nextSuperMinecart = superMinecart;
        superMinecart.setRunnableOnRemove(() -> {
            SuperMinecart nextSuperMinecart = superMinecart.nextSuperMinecart;
            if (nextSuperMinecart == null) return;
            setNextSuperMinecart(nextSuperMinecart);
        });
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
    }

    private boolean passengersContainsPlayer(){
        for (Entity entity : handle.cS()) {
            if (entity instanceof EntityHuman) return true;
        }
        return false;
    }


}
