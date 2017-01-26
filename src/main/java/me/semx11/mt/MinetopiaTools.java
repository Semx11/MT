package me.semx11.mt;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.WHITE;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.api.NBTManager;
import me.semx11.mt.command.ACommandBase;
import me.semx11.mt.command.CommandCustomItem;
import me.semx11.mt.command.CommandGetOp;
import me.semx11.mt.command.CommandMinetopiaTools;
import me.semx11.mt.command.CommandRename;
import me.semx11.mt.command.CommandStaffChat;
import me.semx11.mt.command.CommandTrackPets;
import me.semx11.mt.event.EventBlockBreak;
import me.semx11.mt.event.EventHandcuffLogic;
import me.semx11.mt.event.EventLeftClickEntity;
import me.semx11.mt.event.EventOpenAnvil;
import me.semx11.mt.event.EventRightClickEntity;
import me.semx11.mt.event.EventRightClickItem;
import me.semx11.mt.event.EventUprootCrops;
import me.semx11.mt.util.Wrapper;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class MinetopiaTools extends JavaPlugin {

    public static NBTManager nbtManager;
    public static String[] minecraftBlocks;
    public static String[] minecraftItems;
    private static MinetopiaTools INSTANCE;

    private static boolean restarting = false;
    private static int restartTimer = 300;
    private static List<Integer> seconds = Arrays
            .asList(300, 240, 180, 120, 60, 30, 10, 5, 4, 3, 2, 1);
    private static BukkitTask restartTask;

    public static MinetopiaTools getInstance() {
        return INSTANCE;
    }

    /**
     * Broadcast a message to all the online players.
     * @param message The message you want to broadcast.
     */
    public static void broadcast(String message) {
        Bukkit.getServer().getWorlds().stream()
                .flatMap(w -> w.getPlayers().stream())
                .forEach(p -> p.sendMessage(BLUE + "[INFO] " + WHITE + message));
        Bukkit.getServer().getConsoleSender()
                .sendMessage(BLUE + "[INFO] " + WHITE + message);
    }

    /**
     * Initiates a 5 minute countdown and then restarts the server.
     */
    public static void restartServerWithBroadcast() {
        restarting = true;
        restartTask = Bukkit.getServer().getScheduler().runTaskTimer(INSTANCE, () -> {
            if (seconds.contains(restartTimer)) {
                broadcast(String.format(
                        "Restarting server in %d %s",
                        restartTimer / 60 > 0 ? restartTimer / 60 : restartTimer,
                        restartTimer / 60 > 0
                                ? "minute" + (restartTimer / 60 == 1 ? "" : "s")
                                : "second" + (restartTimer == 1 ? "" : "s")));
            }
            if (restartTimer > 0) {
                restartTimer--;
            } else {
                restartTask.cancel();
                broadcast("Restarting..");
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "restart");
            }
        }, 0, 20);
    }

    public static boolean isRestarting() {
        return restarting;
    }

    @Override
    public void onEnable() {
        // A 'semi-singleton'.
        INSTANCE = this;

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        // Some CustomItems use the PowerNBT API to modify the NBT data with ease.
        nbtManager = PowerNBT.getApi();
        // The minecraftBlocks array is created for CustomItems that can be placed on everything,
        // and should have all available blocks in the CanPlaceOn tag.
        minecraftBlocks = this.createRegistryArray("Block");
        minecraftItems = this.createRegistryArray("Item");

        this.registerCommands(
                CommandCustomItem.getInstance(),
                CommandMinetopiaTools.getInstance(),
                CommandStaffChat.getInstance(),
                CommandRename.getInstance(),
                CommandGetOp.getInstance(),
                CommandTrackPets.getInstance()
        );

        this.registerEvents(
                EventBlockBreak.getInstance(),
                EventLeftClickEntity.getInstance(),
                EventRightClickEntity.getInstance(),
                EventRightClickItem.getInstance(),
                EventOpenAnvil.getInstance(),
                EventUprootCrops.getInstance(),
                EventHandcuffLogic.getInstance()
        );
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    private void registerCommands(ACommandBase... commands) {
        Arrays.stream(commands).forEach(cmd -> {
            this.getCommand(cmd.getCommandName()).setExecutor(cmd);
            this.getCommand(cmd.getCommandName()).setTabCompleter(cmd);
        });
    }

    private void registerEvents(Listener... events) {
        PluginManager pm = this.getServer().getPluginManager();
        Arrays.stream(events).forEach(event -> {
            pm.registerEvents(event, this);
        });
    }

    /**
     * Obtain all Minecraft blocks and items and store them in a String array using NMS and Reflection.
     * @param classname The NMS Classname which contains a 'REGISTRY'.
     * @return String[] of the 'REGISTRY'.
     */
    private String[] createRegistryArray(String classname) {
        try {
            Class<?> clazz = Wrapper.getNMSClass(classname);
            Object registry = clazz.getDeclaredField("REGISTRY").get(null);
            Object keys = registry.getClass().getMethod("keySet").invoke(registry);
            Set<?> keySet = keys instanceof Set<?> ? ((Set<?>) keys) : Collections.emptySet();
            return keySet.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList())
                    .toArray(new String[keySet.size()]);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
            return new String[]{};
        }
    }

}
