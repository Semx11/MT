package me.semx11.mt.event;

import java.util.Optional;
import me.semx11.mt.customitem.CustomItem;
import me.semx11.mt.customitem.Handcuffs;
import me.semx11.mt.util.ChatConfig;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

public class EventHandcuffLogic implements Listener {

    private static final EventHandcuffLogic INSTANCE = new EventHandcuffLogic();

    private EventHandcuffLogic() {
    }

    public static EventHandcuffLogic getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerUnleashEntity(PlayerUnleashEntityEvent e) {
        if (e.getEntityType().equals(EntityType.BAT)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLeashEntity(PlayerLeashEntityEvent e) {
        Optional<CustomItem> customItem = CustomItem.getCustomItem(
                e.getPlayer().getInventory().getItemInMainHand());
        if (customItem.isPresent() && customItem.get().equals(CustomItem.HANDCUFFS)) {
            e.getPlayer().sendMessage(ChatConfig.ERROR + "Animals aren't criminals!");
            e.setCancelled(true);
            e.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Handcuffs.terminateAll(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Handcuffs.terminateAll(e.getPlayer().getUniqueId());
    }

}
