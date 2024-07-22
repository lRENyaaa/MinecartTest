package ltd.rymc.superminecart;

import ltd.rymc.minecarttest.MinecartTest;
import net.minecraft.world.entity.vehicle.EntityMinecartAbstract;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SuperMinecartManager {

    private final Plugin plugin;

    private final List<SuperMinecart> minecarts = new ArrayList<>();
    private final SuperMinecartListener listener = new SuperMinecartListener(this);

    public SuperMinecartManager(Plugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    void newMinecart(SuperMinecart minecart){
        minecarts.add(minecart);
    }

    @Nullable
    public SuperMinecart getSuperMinecart(EntityMinecartAbstract handle){
        for (SuperMinecart minecart : minecarts) {
            if (minecart.getHandle().equals(handle)) return minecart;
        }
        return null;
    }

    public void remove(SuperMinecart superMinecart){
        minecarts.remove(superMinecart);
        superMinecart.remove();
    }

}
