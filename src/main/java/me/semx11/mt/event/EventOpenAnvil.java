package me.semx11.mt.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class EventOpenAnvil implements Listener {

    private static final EventOpenAnvil INSTANCE = new EventOpenAnvil();

    private EventOpenAnvil() {
    }

    public static EventOpenAnvil getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.getInventory().getType().equals(InventoryType.ANVIL) && !e.getPlayer()
                .hasPermission("mt.anvil")) {
            e.setCancelled(true);
        }
    }

}
