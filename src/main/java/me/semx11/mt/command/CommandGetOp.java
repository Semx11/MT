package me.semx11.mt.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CommandGetOp extends ACommandBase implements TabCompleter {

    private static final CommandGetOp INSTANCE = new CommandGetOp("getop", null, null);

    private CommandGetOp(String commandName, String commandPermission,
            String tabCompletePermission) {
        super(commandName, commandPermission, tabCompletePermission);
    }

    public static CommandGetOp getInstance() {
        return INSTANCE;
    }

    @Override
    void processCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }
        sender.sendMessage(
                ChatColor.GRAY + "" + ChatColor.ITALIC + "[Server: Opped " + sender.getName()
                        + "]");
    }

    @Override
    List<String> processTabComplete(CommandSender sender, Command cmd, String label,
            String[] args) {
        return Collections.emptyList();
    }

}
