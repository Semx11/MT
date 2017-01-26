package me.semx11.mt.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import me.semx11.mt.MinetopiaTools;
import me.semx11.mt.customitem.PetTracker;
import me.semx11.mt.util.ChatConfig;
import me.semx11.mt.util.Wrapper;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTrackPets extends ACommandBase {

    private static final CommandTrackPets INSTANCE = new CommandTrackPets(
            "trackpets", "mt.trackpets.self", "mt.trackpets");

    private CommandTrackPets(String commandName, String commandPermission,
            String tabCompletePermission) {
        super(commandName, commandPermission, tabCompletePermission);
    }

    public static CommandTrackPets getInstance() {
        return INSTANCE;
    }

    @Override
    void processCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatConfig.NOT_PLAYER);
                return;
            }
            PetTracker.trackPets(MinetopiaTools.getInstance().getServer()
                            .getOfflinePlayer(((Player) sender).getUniqueId()),
                    sender);
        } else {
            if (!sender.hasPermission("mt.trackpets")) {
                sender.sendMessage(ChatConfig.NO_PERMISSION);
                return;
            }
            List<OfflinePlayer> offlinePlayers = Arrays
                    .stream(MinetopiaTools.getInstance().getServer().getOfflinePlayers())
                    .filter(offlinePlayer -> offlinePlayer.getName() != null)
                    .filter(offlinePlayer -> offlinePlayer.getName().equalsIgnoreCase(args[0]))
                    .collect(Collectors.toList());

            if (offlinePlayers.size() > 0) {
                PetTracker.trackPets(offlinePlayers.get(0), sender);
            } else {
                sender.sendMessage(ChatConfig.ERROR + "Player '" + args[0] + "' not found.");
            }
        }
    }

    @Override
    List<String> processTabComplete(CommandSender sender, Command cmd, String label,
            String[] args) {
        return args.length == 1
                ? Wrapper.tabComplete(args[0],
                Arrays.stream(MinetopiaTools.getInstance().getServer().getOfflinePlayers())
                        .filter(offlinePlayer -> offlinePlayer.getName() != null)
                        .map(OfflinePlayer::getName)
                        .collect(Collectors.toList()))
                : Collections.emptyList();
    }

}