package me.semx11.mt.command;

import java.util.Collections;
import java.util.List;
import me.semx11.mt.util.ChatConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public abstract class ACommandBase implements CommandExecutor, TabCompleter {

    private String commandName;
    private String commandPermission;
    private String tabCompletePermission;

    ACommandBase(String commandName, String commandPermission,
            String tabCompletePermission) {
        this.commandName = commandName;
        this.commandPermission = commandPermission;
        this.tabCompletePermission = tabCompletePermission;
    }

    public String getCommandName() {
        return commandName;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (commandPermission != null) {
            if (!sender.hasPermission(commandPermission)) {
                sender.sendMessage(ChatConfig.NO_PERMISSION);
                return true;
            }
        }
        processCommand(sender, cmd, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label,
            String[] args) {
        if (tabCompletePermission != null) {
            if (!sender.hasPermission(tabCompletePermission)) {
                return Collections.emptyList();
            }
        }
        return processTabComplete(sender, cmd, label, args);
    }

    abstract void processCommand(CommandSender sender, Command cmd, String label, String[] args);

    abstract List<String> processTabComplete(CommandSender sender, Command cmd, String label,
            String[] args);

}
