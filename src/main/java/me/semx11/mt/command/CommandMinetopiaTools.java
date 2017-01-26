package me.semx11.mt.command;

import static me.semx11.mt.util.ChatConfig.ACCENT;
import static me.semx11.mt.util.ChatConfig.ERROR;
import static me.semx11.mt.util.ChatConfig.MAIN;
import static me.semx11.mt.util.ChatConfig.NO_PERMISSION;
import static me.semx11.mt.util.ChatConfig.PREFIX;
import static me.semx11.mt.util.ChatConfig.SUCCESS;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.semx11.mt.MinetopiaTools;
import me.semx11.mt.util.Config;
import me.semx11.mt.util.Wrapper;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandMinetopiaTools extends ACommandBase {

    private static final CommandMinetopiaTools INSTANCE = new CommandMinetopiaTools(
            "minetopiatools", null, "mt.tools");

    private CommandMinetopiaTools(String commandName, String commandPermission,
            String tabCompletePermission) {
        super(commandName, commandPermission, tabCompletePermission);
    }

    public static CommandMinetopiaTools getInstance() {
        return INSTANCE;
    }

    @Override
    void processCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(
                    String.format("%sMinetopiaTools v%s %s- Made by %sSemx11%s.", PREFIX,
                            ACCENT + MinetopiaTools.getInstance().getDescription().getVersion(),
                            MAIN, ACCENT, MAIN));
        } else {
            switch (args[0]) {
                case "broadcast":
                    if (!sender.hasPermission("mt.tools.restart")) {
                        sender.sendMessage(NO_PERMISSION);
                        return;
                    }
                    MinetopiaTools.broadcast(
                            StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " "));
                    break;
                case "reload":
                    if (!sender.hasPermission("mt.tools.reload")) {
                        sender.sendMessage(NO_PERMISSION);
                        return;
                    }
                    Config.reload();
                    sender.sendMessage(PREFIX + SUCCESS + "Config reloaded!");
                    break;
                case "restart":
                    if (!sender.hasPermission("mt.tools.restart")) {
                        sender.sendMessage(NO_PERMISSION);
                        return;
                    }
                    if (!MinetopiaTools.isRestarting()) {
                        MinetopiaTools.restartServerWithBroadcast();
                    } else {
                        sender.sendMessage(ERROR + "Server is already restarting!");
                    }
                    break;
                default:
                    sender.sendMessage(PREFIX + ERROR + "Usage: /" + label + " <broadcast, reload, restart>");
            }
        }
    }

    @Override
    List<String> processTabComplete(CommandSender sender, Command cmd, String label,
            String[] args) {
        switch (args.length) {
            case 1:
                return Wrapper.tabComplete(args[args.length - 1], "broadcast", "reload", "restart");
            default:
                return Collections.emptyList();
        }
    }

}