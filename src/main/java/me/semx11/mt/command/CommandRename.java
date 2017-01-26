package me.semx11.mt.command;

import java.util.Collections;
import java.util.List;
import me.semx11.mt.customitem.CustomItem;
import me.semx11.mt.util.ChatConfig;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandRename extends ACommandBase {

    private static final CommandRename INSTANCE = new CommandRename(
            "rename", "mt.rename", "mt.rename");

    private CommandRename(String commandName, String commandPermission,
            String tabCompletePermission) {
        super(commandName, commandPermission, tabCompletePermission);
    }

    public static CommandRename getInstance() {
        return INSTANCE;
    }

    @Override
    void processCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatConfig.NOT_PLAYER);
            return;
        }

        Player p = (Player) sender;

        if (args.length < 1) {
            p.sendMessage(ChatConfig.ERROR + "Usage: /rename <name>");
            return;
        }

        ItemStack item = p.getInventory().getItemInMainHand();

        if (item.getType().equals(Material.AIR)) {
            p.sendMessage(ChatConfig.ERROR + "You don't have an item in your hand!");
            return;
        }

        if (CustomItem.getCustomItem(item).isPresent()) {
            p.sendMessage(ChatConfig.ERROR + "You are not allowed to rename this item!");
            return;
        }

        String newName = ChatColor.translateAlternateColorCodes('&', StringUtils.join(args, " "));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(newName);
        item.setItemMeta(itemMeta);
        p.getInventory().setItemInMainHand(item);

    }

    @Override
    List<String> processTabComplete(CommandSender sender, Command cmd, String label,
            String[] args) {
        return Collections.emptyList();
    }

}