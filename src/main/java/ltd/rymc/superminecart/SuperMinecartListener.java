package ltd.rymc.superminecart;

import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftMinecart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;

import javax.annotation.Nullable;

public class SuperMinecartListener implements Listener {

    private final SuperMinecartManager manager;

    public SuperMinecartListener(SuperMinecartManager manager){
        this.manager = manager;
    }

    @EventHandler
    public void onCreatureSpawn(VehicleCreateEvent event){
        Minecart minecart = toMinecart(event.getVehicle());
        if (minecart == null) return;

        EntityMinecartAbstract handle = ((CraftMinecart) minecart).getHandle();
        if (manager.getSuperMinecart(handle) != null) return;

        SuperMinecart superMinecart = new SuperMinecart(((CraftMinecart) minecart).getHandle());
        manager.newMinecart(superMinecart);

    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event){
        for (Entity entity : event.getEntities()) {
            Minecart minecart = toMinecart(entity);
            if (minecart == null) return;

            EntityMinecartAbstract handle = ((CraftMinecart) minecart).getHandle();
            if (manager.getSuperMinecart(handle) != null) return;

            SuperMinecart superMinecart = new SuperMinecart(handle);
            manager.newMinecart(superMinecart);
        }
    }

    @EventHandler
    public void onEntitiesUnload(EntitiesUnloadEvent event){
        for (Entity entity : event.getEntities()) {
            Minecart minecart = toMinecart(entity);
            if (minecart == null) return;

            SuperMinecart superMinecart = toSuperMinecart(minecart);
            if (superMinecart == null) return;

            manager.remove(superMinecart);
        }
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event){
        Minecart minecart = toMinecart(event.getVehicle());
        if (minecart == null) return;

        SuperMinecart superMinecart = toSuperMinecart(minecart);
        if (superMinecart == null) return;

        superMinecart.handleMove(event);
    }

    @EventHandler
    public void onEntityRemove(VehicleDestroyEvent event){
        Minecart minecart = toMinecart(event.getVehicle());
        if (minecart == null) return;

        SuperMinecart superMinecart = toSuperMinecart(minecart);
        if (superMinecart == null) return;

        manager.remove(superMinecart);
    }

    @EventHandler
    public void onVehicleCollision(VehicleEntityCollisionEvent event){
        Minecart oFrom = toMinecart(event.getVehicle());
        Minecart oTo = toMinecart(event.getEntity());
        if (oFrom == null || oTo == null) return;

        SuperMinecart from = toSuperMinecart(oFrom);
        SuperMinecart to = toSuperMinecart(oTo);
        if (from == null || to == null) return;

        if (to.hasPreviousMinecart() || from.hasNextMinecart()) return;
        to.setPreviousSuperMinecart(from);
    }

    @Nullable
    private Minecart toMinecart(Entity entity){
        return entity instanceof Minecart ? (Minecart) entity : null;
    }

    @Nullable
    private SuperMinecart toSuperMinecart(Minecart minecart){
        return manager.getSuperMinecart(((CraftMinecart) minecart).getHandle());
    }

}
