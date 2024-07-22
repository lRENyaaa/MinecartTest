package ltd.rymc.minecarttest;

import ltd.rymc.superminecart.SuperMinecartManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecartTest extends JavaPlugin {

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
    }

}
