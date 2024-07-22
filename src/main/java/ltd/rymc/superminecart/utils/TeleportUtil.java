package ltd.rymc.superminecart.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class TeleportUtil {

    private final static boolean isFolia = isFolia();

    private static boolean isFolia() {
        try {
            Bukkit.class.getMethod("getRegionScheduler");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static Method teleportAsync;

    static {
        if (isFolia) {
            try {
                teleportAsync = Entity.class.getMethod("teleportAsync", Location.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static CompletableFuture<Boolean> teleport(Entity entity, Location location) {

        if (!isFolia) {
            CompletableFuture<Boolean> result = new CompletableFuture<>();
            result.complete(entity.teleport(location));
            return result;
        }

        try {
            @SuppressWarnings("unchecked")
            CompletableFuture<Boolean> result = (CompletableFuture<Boolean>) teleportAsync.invoke(entity, location);
            return result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
