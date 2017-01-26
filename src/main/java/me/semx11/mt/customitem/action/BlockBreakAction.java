package me.semx11.mt.customitem.action;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

public interface BlockBreakAction {

    void execute(BlockBreakEvent e, Block b);

}
