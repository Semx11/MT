package me.semx11.mt.event;

import java.util.Optional;
import me.semx11.mt.customitem.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EventRightClickItem implements Listener {

    private static final EventRightClickItem INSTANCE = new EventRightClickItem();

    private EventRightClickItem() {
    }

    public static EventRightClickItem getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction()
                .equals(Action.RIGHT_CLICK_BLOCK)) && e.getHand().equals(EquipmentSlot.HAND)) {
            Player p = e.getPlayer();
            ItemStack item = p.getInventory().getItemInMainHand();

            Optional<CustomItem> customItem = CustomItem.getCustomItem(item);
            if (customItem.isPresent() && customItem.get().hasRightClickItemAction()) {
                customItem.get().getRightClickItemAction().execute(e);
            }
        }
    }
}
