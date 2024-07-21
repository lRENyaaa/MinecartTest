package ltd.rymc.superminecart;

import org.bukkit.craftbukkit.v1_20_R4.entity.CraftMinecart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import javax.annotation.Nullable;

public class SuperMinecartListener implements Listener {

    private final SuperMinecartManager manager;

    public SuperMinecartListener(SuperMinecartManager manager){
        this.manager = manager;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event){
        Minecart minecart = toMinecart(event.getEntity());
        if (minecart == null) return;

        minecart.setMaxSpeed(Math.sqrt(Double.MAX_VALUE));

        manager.newMinecart(new SuperMinecart(((CraftMinecart) minecart).getHandle()));
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event){
        Minecart minecart = toMinecart(event.getVehicle());
        if (minecart == null) return;

        SuperMinecart superMinecart = manager.getSuperMinecart(((CraftMinecart) minecart).getHandle());
        if (superMinecart == null) return;

        superMinecart.handleMove();
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    public void onEntityRemove(EntityRemoveEvent event){
        Minecart minecart = toMinecart(event.getEntity());
        if (minecart == null) return;

        SuperMinecart superMinecart = manager.getSuperMinecart(((CraftMinecart) minecart).getHandle());
        if (superMinecart == null) return;

        manager.remove(superMinecart);
    }

    @Nullable
    private Minecart toMinecart(Entity entity){
        return entity instanceof Minecart ? (Minecart) entity : null;
    }

}
