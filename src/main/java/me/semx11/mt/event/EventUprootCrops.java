package me.semx11.mt.event;

import static org.bukkit.Material.CARROT;
import static org.bukkit.Material.CROPS;
import static org.bukkit.Material.MELON_STEM;
import static org.bukkit.Material.POTATO;
import static org.bukkit.Material.PUMPKIN_STEM;

import me.semx11.mt.util.Wrapper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventUprootCrops implements Listener {

    private static final EventUprootCrops INSTANCE = new EventUprootCrops();

    private EventUprootCrops() {
    }

    public static EventUprootCrops getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.PHYSICAL) && e.getClickedBlock().getType()
                .equals(Material.SOIL)) {
            Block b = e.getClickedBlock().getRelative(BlockFace.UP);
            Boolean isCrop = false;
            if (Wrapper.equalsAny(b.getType(), CROPS, MELON_STEM, PUMPKIN_STEM, CARROT, POTATO)) {
                isCrop = true;
            }
            if (e.getPlayer().hasPermission("mt.uproot") || !isCrop) {
                return;
            }
            e.setCancelled(true);
        }
    }

}
