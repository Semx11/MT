package me.semx11.mt.event;

import java.util.Optional;
import me.semx11.mt.customitem.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EventRightClickEntity implements Listener {

    private static final EventRightClickEntity INSTANCE = new EventRightClickEntity();

    private EventRightClickEntity() {
    }

    public static EventRightClickEntity getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {

        if (e.getHand().equals(EquipmentSlot.HAND)) {

            Player p = e.getPlayer();
            ItemStack item = p.getInventory().getItemInMainHand();

            Optional<CustomItem> customItem = CustomItem.getCustomItem(item);
            if (customItem.isPresent() && customItem.get().hasRightClickEntityAction()) {
                customItem.get().getRightClickEntityAction().execute(e);
            }
        }
    }
}
