package me.semx11.mt.event;

import java.util.List;
import java.util.stream.Collectors;
import me.semx11.mt.customitem.CustomItem;
import me.semx11.mt.customitem.action.BlockBreakAction;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class EventBlockBreak implements Listener {

    private static final EventBlockBreak INSTANCE = new EventBlockBreak();

    private EventBlockBreak() {
    }

    public static EventBlockBreak getInstance() {
        return INSTANCE;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent e) {

        if (e.isCancelled()) {
            return;
        }

        Player p = e.getPlayer();
        Block b = e.getBlock();
        if (!p.getGameMode().equals(GameMode.ADVENTURE)) {
            return;
        }

        List<BlockBreakAction> breakActions = CustomItem.sortedValues().stream()
                .filter(CustomItem::hasBlockBreakAction)
                .filter(cItem -> cItem.getBreakMaterials().contains(b.getType()))
                .map(CustomItem::getBlockBreakAction)
                .limit(1)
                .collect(Collectors.toList());

        breakActions.forEach(breakAction -> breakAction.execute(e, b));

    }

}
