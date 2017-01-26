package me.semx11.mt.event;

import static org.bukkit.entity.EntityType.PLAYER;

import java.util.Optional;
import me.semx11.mt.customitem.CustomItem;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EventLeftClickEntity implements Listener {

    private static final EventLeftClickEntity INSTANCE = new EventLeftClickEntity();

    private EventLeftClickEntity() {
    }

    public static EventLeftClickEntity getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getDamager();

        if (p.getGameMode().equals(GameMode.CREATIVE) && !e.getEntityType().equals(PLAYER)) {
            return;
        }

        ItemStack item = p.getInventory().getItemInMainHand();

        Optional<CustomItem> customItem = CustomItem.getCustomItem(item);
        if (customItem.isPresent() && customItem.get().hasLeftClickEntityAction()) {
            customItem.get().getLeftClickEntityAction().execute(e);
        } else {
            e.setCancelled(true);
        }

    }

}
