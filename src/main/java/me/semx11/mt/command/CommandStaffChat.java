package me.semx11.mt.command;

import java.util.Collections;
import java.util.List;
import me.semx11.mt.MinetopiaTools;
import me.semx11.mt.util.ChatConfig;
import me.semx11.mt.util.Config;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandStaffChat extends ACommandBase {

    private static final CommandStaffChat INSTANCE = new CommandStaffChat(
            "staffchat", "mt.staffchat", "mt.staffchat");

    private CommandStaffChat(String commandName, String commandPermission,
            String tabCompletePermission) {
        super(commandName, commandPermission, tabCompletePermission);
    }

    public static CommandStaffChat getInstance() {
        return INSTANCE;
    }

    @Override
    void processCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String displayName;
        if (sender instanceof Player) {
            displayName = ((Player) sender).getDisplayName();
        } else if (sender instanceof ConsoleCommandSender) {
            displayName = ChatColor.RED + "Console";
        } else {
            return;
        }

        String message = Config.format(ChatConfig.STAFFCHAT_PREFIX, "name", displayName,
                "message", StringUtils.join(args, ' '));

        MinetopiaTools.getInstance().getServer().getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("mt.staffchat"))
                .forEach(p -> p.sendMessage(message));

        MinetopiaTools.getInstance().getServer().getConsoleSender().sendMessage(message);
    }

    @Override
    List<String> processTabComplete(CommandSender sender, Command cmd, String label,
            String[] args) {
        return Collections.emptyList();
    }

}