package me.semx11.mt.command;

import static me.semx11.mt.util.ChatConfig.ERROR;
import static org.bukkit.ChatColor.GOLD;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import me.semx11.mt.customitem.CustomItem;
import me.semx11.mt.util.Wrapper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandCustomItem extends ACommandBase {

    private static final CommandCustomItem INSTANCE = new CommandCustomItem(
            "customitem", "mt.customitem", "mt.customitem");

    private CommandCustomItem(String commandName, String commandPermission,
            String tabCompletePermission) {
        super(commandName, commandPermission, tabCompletePermission);
    }

    public static CommandCustomItem getInstance() {
        return INSTANCE;
    }

    @Override
    void processCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(
                    ERROR + "Too few parameters!\nUsage: /" + label
                            + " <player> <customitem> [amount]");
            return;
        }

        Player p;
        if ((p = Bukkit.getServer().getPlayer(args[0])) == null) {
            sender.sendMessage(ERROR + "Player '" + args[0] + "' not found.");
            return;
        }

        CustomItem cItem;
        try {
            cItem = CustomItem.valueOfIgnoreCase(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(
                    ERROR + "This custom item does not exist! Try one of the following:");

            if (sender instanceof Player) {
                ComponentBuilder componentBuilder = new ComponentBuilder("");
                Iterator<CustomItem> customItems = CustomItem.sortedValues().iterator();

                int totalBytes = 0;
                while (customItems.hasNext()) {
                    CustomItem customItem = customItems.next();

                    int tagBytes = 0;
                    try {
                        tagBytes = customItem.getItemTag().getBytes("UTF-32").length;
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }

                    // Minecraft only supports components up to 2^15-1 bytes. If it hits this limit,
                    // it will send the current component and continue with a new one.

                    System.out.println("totalBytes: " + totalBytes);
                    System.out.println("tagBytes: " + tagBytes);
                    System.out.println("totalBytes + tagBytes: " + (totalBytes + tagBytes));
                    if (totalBytes + tagBytes > 32767) {
                        ((Player) sender).spigot().sendMessage(componentBuilder.create());
                        componentBuilder = new ComponentBuilder("");
                        totalBytes = 0;
                    }
                    totalBytes += tagBytes;
                    BaseComponent[] itemMessage = new BaseComponent[]{
                            new TextComponent(customItem.getItemTag())};
                    componentBuilder.append(customItem.toString())
                            .event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, itemMessage));

                    if (customItems.hasNext()) {
                        componentBuilder.append(", ");
                    }
                }

                ((Player) sender).spigot().sendMessage(componentBuilder.create());
            } else {
                sender.sendMessage(StringUtils.join(CustomItem.sortedValues(), ", "));
            }

            return;
        }

        int amount = 1;
        short damage = 0;
        if (args.length > 2) {
            try {
                if (args[2].contains(":")) {
                    String[] data = args[2].split(":");
                    amount = Integer.parseInt(data[0]);
                    damage = Short.parseShort(data[1]);
                } else {
                    amount = Integer.parseInt(args[2]);
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ERROR + "Invalid amount.");
                return;
            }
        }

        ItemStack cItemStack = cItem.getItemStack();
        if (amount > 0 && amount <= 64) {
            cItemStack.setAmount(amount);
        } else {
            sender.sendMessage(ERROR + "Invalid amount.");
            return;
        }

        if (damage > 0) {
            cItemStack.setDurability(damage);
        }

        p.getInventory().addItem(cItemStack);
        sender.sendMessage(
                String.format(GOLD + "You gave %d '%s' to %s", amount, cItem, p.getName())
        );
        p.sendMessage(
                String.format(GOLD + "You received %d '%s' from %s", amount, cItem,
                        sender.getName())
        );
    }

    @Override
    List<String> processTabComplete(CommandSender sender, Command cmd, String label,
            String[] args) {
        int i = args.length - 1;
        switch (args.length) {
            case 1:
                return Wrapper.tabComplete(args[i], sender.getServer().getOnlinePlayers()
                        .stream()
                        .map((Function<Player, String>) HumanEntity::getName)
                        .collect(Collectors.toList()));
            case 2:
                return Wrapper.tabComplete(args[i], CustomItem.sortedValues().stream()
                        .map(CustomItem::toString)
                        .collect(Collectors.toList()));
            case 4:
                return Wrapper.tabComplete(args[i], "true", "false");
            default:
                return Collections.emptyList();
        }
    }

}
