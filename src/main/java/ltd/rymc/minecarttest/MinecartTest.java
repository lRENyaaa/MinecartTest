package ltd.rymc.minecarttest;

import ltd.rymc.superminecart.SuperMinecartManager;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockMinecartTrackAbstract;
import net.minecraft.world.level.block.BlockStainedGlass;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftMinecart;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MinecartTest extends JavaPlugin implements Listener {

    private static Method c = null;
    private static final int SPEED = 40;

    private static MinecartTest instance;
    private static SuperMinecartManager superMinecartManager;

    public static MinecartTest getInstance(){
        return instance;
    }

    public static SuperMinecartManager getSuperMinecartManager(){
        return superMinecartManager;
    }

    @Override
    public void onEnable() {
        instance = this;

        superMinecartManager = new SuperMinecartManager(this);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event){
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Minecart)) return;
        Minecart minecart = (Minecart) entity;
        minecart.setMaxSpeed(Math.sqrt(Double.MAX_VALUE));

        updateDeltaMovement(((CraftMinecart) minecart).getHandle());
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event){
        Vehicle vehicle = event.getVehicle();
        if (!(vehicle instanceof Minecart)) return;
        Minecart minecart = (Minecart) vehicle;

        EntityMinecartAbstract minecraftMinecart = ((CraftMinecart) minecart).getHandle();

        if (!minecraftMinecart.ci()) return;
        if (!passengersContainsPlayer(minecraftMinecart)) return;

        for (int i = 1; i < SPEED; i++){
            move(minecraftMinecart);
        }

    }

    private boolean passengersContainsPlayer(EntityMinecartAbstract minecraftMinecart){
        for (Entity entity : minecraftMinecart.cS()) {
            if (entity instanceof EntityHuman) return true;
        }
        return false;
    }

    private void move(EntityMinecartAbstract minecart){
        Vec3D vec3D = minecart.dn();
        int x = MathHelper.a(vec3D.c);
        int y = MathHelper.a(vec3D.d);
        int z = MathHelper.a(vec3D.e);

        BlockPosition blockposition = new BlockPosition(x, y, z);
        IBlockData iblockdata = minecart.dP().a_(blockposition);

        if (!(iblockdata.b() instanceof BlockMinecartTrackAbstract)) return;

        try {
            if (c == null){
                c = EntityMinecartAbstract.class.getDeclaredMethod("c", BlockPosition.class, IBlockData.class);
                c.setAccessible(true);
            }
            c.invoke(minecart, blockposition, iblockdata);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        updateDeltaMovement(minecart);
    }

    private void updateDeltaMovement(EntityMinecartAbstract minecraftMinecart){
        double speed = 100.0D;
        Vec3D deltaMovement = minecraftMinecart.ds();
        minecraftMinecart.o(deltaMovement.c * speed, deltaMovement.d * speed, deltaMovement.e * speed);
    }

    private boolean checkBlackStainedGlass(EntityMinecartAbstract minecraftMinecart){
        Vec3D vec3D = minecraftMinecart.dn();
        int x = MathHelper.a(vec3D.c);
        int y = MathHelper.a(vec3D.d) - 1;
        int z = MathHelper.a(vec3D.e);

        BlockPosition blockposition = new BlockPosition(x, y, z);
        Block block = minecraftMinecart.dP().a_(blockposition).b();

        if (!(block instanceof BlockStainedGlass)) return false;

        BlockStainedGlass glass = (BlockStainedGlass) block;

        return glass.b().equals(EnumColor.p);
    }
}
