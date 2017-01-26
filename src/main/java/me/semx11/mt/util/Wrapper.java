package me.semx11.mt.util;

import static org.bukkit.Bukkit.getServer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import me.semx11.mt.MinetopiaTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Wrapper {

    /**
     * Utility class, so shouldn't be instantiated.
     */
    private Wrapper() {
    }

    private static final String version = Bukkit.getServer().getClass().getPackage().getName()
            .split("\\.")[3];

    public static List<String> tabComplete(String arg, String... possibilities) {
        return tabComplete(arg, Arrays.asList(possibilities));
    }

    public static List<String> tabComplete(String arg, List<String> possibilities) {
        if (arg == null) {
            return possibilities;
        }
        return possibilities.stream()
                .filter(p -> p.toLowerCase().startsWith(arg.toLowerCase()))
                .sorted()
                .collect(Collectors.toList());
    }

    public static Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + version + "." + name);
    }

    public static Class<?> getCraftClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
    }

    @SafeVarargs
    public static <T> boolean equalsAny(T target, T... values) {
        for (T value : values) {
            if (Objects.equals(target, value)) {
                return true;
            }
        }
        return false;
    }

}
