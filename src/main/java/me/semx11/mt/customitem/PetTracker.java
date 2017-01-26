package me.semx11.mt.customitem;

import static me.semx11.mt.util.ChatConfig.ERROR;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.DARK_AQUA;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import java.util.List;
import java.util.stream.Collectors;
import me.semx11.mt.MinetopiaTools;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

/**
 * Utility class for CustomItem.PETTRACKER.
 */
public class PetTracker {

    /**
     * Utility class, so shouldn't be instantiated.
     */
    private PetTracker() {
    }

    /**
     * Prints a list of all tamed animals (pets) a player owns. Runs when a player clicks a
     * CustomItem.PETTRACKER or when somebody runs /trackpets [player].
     *
     * @param player The OfflinePlayer that should be checked.
     * @param sender The CommandSender the output should be sent to.
     */
    public static void trackPets(OfflinePlayer player, CommandSender sender) {

        List<Animals> animals = MinetopiaTools.getInstance().getServer().getWorlds().stream()
                .flatMap(world -> world.getEntities().stream())
                .filter(entity -> entity instanceof Tameable && entity instanceof Animals)
                .filter(entity -> ((Tameable) entity).isTamed())
                .filter(entity -> ((Tameable) entity).getOwner() != null)
                .filter(entity -> ((Tameable) entity).getOwner().getUniqueId()
                        .equals(player.getUniqueId()))
                .map(entity -> (Animals) entity)
                .collect(Collectors.toList());

        if (animals.size() > 0) {
            sender.sendMessage(String.format(
                    DARK_AQUA + "[" + AQUA + "PetTracker%s" + DARK_AQUA + "]",
                    sender instanceof Player ?
                            ((Player) sender).getUniqueId().equals(player.getUniqueId()) ?
                                    ""
                                    : ": " + player.getName()
                            : ": " + player.getName()
            ));

            animals.forEach(animal -> {
                Location l = animal.getLocation();
                sender.sendMessage(String.format(
                        DARK_GREEN + "[" + GREEN + "%s" + DARK_GREEN
                                + "] " + GREEN + "x: %d, y: %d, z: %d",
                        animal.getCustomName() != null
                                ? animal.getCustomName()
                                : animal.getName(),
                        l.getBlockX(), l.getBlockY(), l.getBlockZ()
                ));
            });
        } else {
            sender.sendMessage(ERROR + "Could not locate any pets!");
        }
    }

    /**
     * Prints the name (or UUID if name is not available) of the OfflinePlayer who owns the given
     * Entity. Does nothing if Entity isn't Tameable.
     *
     * @param entity The entity that should be checked.
     * @param sender The CommandSender who should see the message.
     */
    public static void printOwner(Entity entity, CommandSender sender) {
        if (!(sender instanceof Player) || !(entity instanceof Tameable
                && entity instanceof Animals)) {
            return;
        }
        Animals animal = (Animals) entity;
        Tameable tameable = (Tameable) entity;

        ChatColor darkColor = DARK_RED;
        ChatColor lightColor = RED;

        String message;

        if (!tameable.isTamed()) {
            message = "This animal isn't tame.";
        } else if (tameable.getOwner() == null) {
            message = "This animal has no owner.";
        } else {
            darkColor = DARK_GREEN;
            lightColor = GREEN;
            message = "Owner: " + (tameable.getOwner().getName() != null
                    ? tameable.getOwner().getName()
                    : tameable.getOwner().getUniqueId()
            );
        }

        sender.sendMessage(DARK_AQUA + "[" + AQUA + "PetTracker" + DARK_AQUA + "]");
        sender.sendMessage(String.format(
                darkColor + "[" + lightColor + "%s" + darkColor + "] " + lightColor + message,
                animal.getCustomName() != null
                        ? animal.getCustomName()
                        : animal.getName()
        ));

    }

}
