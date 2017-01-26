package me.semx11.mt.customitem;

import static me.semx11.mt.util.ChatConfig.ERROR;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.ChatColor.GOLD;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.semx11.mt.MinetopiaTools;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

/**
 * Utility class for CustomItem.HANDCUFFS.
 */
public class Handcuffs {

    /**
     * Utility class, so shouldn't be instantiated.
     */
    private Handcuffs() {
    }

    private static final List<HandcuffSession> sessions = new ArrayList<>();

    /**
     * Toggles the handcuffs between players and initializes the logic behind it.
     * Called by the RightClickEntityAction from the CustomItem.HANDCUFFS.
     * @param holder UUID of the player who holds the Handcuffs.
     * @param target UUID of the player who was clicked by 'the holder'.
     */
    public static void toggleHandcuffs(Player holder, Player target) {

        switch (getHandcuffStatus(holder.getUniqueId(), target.getUniqueId())) {
            case BIND:
                // You can't leash ArmorStands, so spawn a Bat at the location of the target
                // and apply potion effects, NoAI, Silence, Invulnerability and make the holder
                // hold the bat.
                LivingEntity leashMarker = (LivingEntity) holder.getWorld()
                        .spawnEntity(target.getLocation(), EntityType.BAT);
                leashMarker.addPotionEffect(
                        new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 0, true, false));
                leashMarker.setAI(false);
                leashMarker.setSilent(true);
                leashMarker.setInvulnerable(true);
                leashMarker.setLeashHolder(holder);

                BukkitTask t = getServer().getScheduler()
                        .runTaskTimer(MinetopiaTools.getInstance(), () -> {
                            // Get vectors from player location and calculate distance (flat and real)
                            Vector targetVec = target.getLocation().toVector();
                            Vector holderVec = holder.getLocation().toVector();
                            double distance = holderVec.distance(targetVec);
                            double flatDistance = holderVec.setY(0).distance(targetVec.setY(0));

                            /*
                            If the distance between the holder and the target is higher than 3,
                            the target will be 'pulled' towards the holder.
                            If the distance between the holder and the target is higher than 6
                            (this is possible if the target gets stuck somewhere), the holder will
                            get 'pulled back' to prevent the lead from snapping.
                             */

                            if (flatDistance > 3) {
                                Vector v1 = holderVec.subtract(targetVec);
                                Vector v2 = v1.normalize().multiply(flatDistance / 5 - 0.4);
                                target.setVelocity(new Vector(
                                        v2.getX(), target.getVelocity().getY(), v2.getZ()));
                            }

                            if (flatDistance > 6 || distance > 6) {
                                Vector v1 = target.getLocation().toVector()
                                        .subtract(holder.getLocation().toVector());
                                Vector v2 = v1.normalize().multiply(flatDistance / 20);
                                holder.setVelocity(new Vector(v2.getX(), v2.getY(), v2.getZ()));
                            }
                            // Finally, teleport the marker (in this case a bat) to the player.
                            leashMarker.teleport(target);
                        }, 0L, 1L);

                sessions.add(new HandcuffSession(holder.getUniqueId(), target.getUniqueId(),
                        leashMarker, t));
                break;
            case BOTH_OCCUPIED:
            case HOLDER_OCCUPIED:
                holder.sendMessage(ERROR + "You can only hold one player!");
                break;
            case TARGET_OCCUPIED:
                holder.sendMessage(ERROR + "Someone else is already holding this player!");
                break;
            case UNBIND:
                Optional<HandcuffSession> session = getSession(holder.getUniqueId(),
                        target.getUniqueId());
                if (session.isPresent()) {
                    session.get().terminate();
                    holder.sendMessage(GOLD + "You removed the handcuffs.");
                    target.sendMessage(GOLD + "You are no longer cuffed.");
                }
                break;
        }
    }

    private static HandcuffStatus getHandcuffStatus(UUID holderUuid, UUID targetUuid) {
        boolean holding = sessions.stream().anyMatch(s -> s.getHolderUuid().equals(holderUuid));
        boolean targeted = sessions.stream().anyMatch(s -> s.getTargetUuid().equals(targetUuid));
        boolean unbind = sessions.stream().anyMatch(
                s -> s.getHolderUuid().equals(holderUuid) && s.getTargetUuid().equals(targetUuid));

        return HandcuffStatus.getStatus(holding, targeted, unbind);
    }

    /**
     * Get the HandcuffSession where two players are involved in.
     * @param holderUuid UUID of the holder
     * @param targetUuid UUID of the target
     * @return Optional HandcuffSession, in case you didn't check the status before calling this.
     */
    private static Optional<HandcuffSession> getSession(UUID holderUuid, UUID targetUuid) {
        return sessions.stream()
                .filter(s -> s.getHolderUuid().equals(holderUuid)
                        && s.getTargetUuid().equals(targetUuid))
                .findAny();
    }

    /**
     * Remove all HandcuffSessions a player is involved with. Called when a player quits the game
     * or when he dies.
     * @param playerUuid The UUID of the player.
     */
    public static void terminateAll(UUID playerUuid) {
        sessions.removeIf(s -> {
            if (s.getHolderUuid().equals(playerUuid) || s.getTargetUuid().equals(playerUuid)) {
                s.terminate();
                return true;
            }
            return false;
        });
    }

    /**
     * HandcuffStatus is used to indicate if the holder and target should bind, unbind, or do
     * nothing. It is decided by three criteria, all possible combinations (2^3) are listed here:
     *
     * Holder is holding   Target is being held   Target is being held by holder  HandcuffStatus
     * false               false                  false                           BIND
     * false               false                  true                            --Impossible--
     * false               true                   false                           TARGET_OCCUPIED
     * false               true                   true                            --Impossible--
     * true                false                  false                           HOLDER_OCCUPIED
     * true                false                  true                            --Impossible--
     * true                true                   false                           BOTH_OCCUPIED
     * true                true                   true                            UNBIND
     */
    private enum HandcuffStatus {
        BIND(false, false, false),
        HOLDER_OCCUPIED(true, false, false),
        TARGET_OCCUPIED(false, true, false),
        BOTH_OCCUPIED(true, true, false),
        UNBIND(true, true, true);

        private static final HandcuffStatus[] values = values();

        private final boolean holding;
        private final boolean targeted;
        private final boolean unbind;

        HandcuffStatus(boolean holding, boolean targeted, boolean unbind) {
            this.holding = holding;
            this.targeted = targeted;
            this.unbind = unbind;
        }

        private static HandcuffStatus getStatus(boolean holding, boolean targeted, boolean unbind) {
            for (HandcuffStatus status : values) {
                if (status.holding == holding && status.targeted == targeted
                        && status.unbind == unbind) {
                    return status;
                }
            }
            // Can't be reached.
            return null;
        }
    }

    private static class HandcuffSession {

        private final UUID holderUuid;
        private final UUID targetUuid;
        private final LivingEntity leashMarker;
        private final BukkitTask task;

        HandcuffSession(UUID holderUuid, UUID targetUuid, LivingEntity leashMarker,
                BukkitTask task) {
            this.holderUuid = holderUuid;
            this.targetUuid = targetUuid;
            this.leashMarker = leashMarker;
            this.task = task;
        }

        UUID getHolderUuid() {
            return holderUuid;
        }

        UUID getTargetUuid() {
            return targetUuid;
        }

        void terminate() {
            // Remove entity from the world (Bat) and cancel the BukkitTask
            leashMarker.remove();
            task.cancel();
        }

    }

}
