package me.semx11.mt.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.semx11.mt.MinetopiaTools;
import org.bukkit.ChatColor;

public class Config {

    /**
     * Utility class, so shouldn't be instantiated.
     */
    private Config() {
    }

    /**
     * Reload the main config and all classes that use the config.
     */
    public static void reload() {
        MinetopiaTools.getInstance().reloadConfig();
        ChatConfig.getInstance().reloadFromConfig();
    }

    public static String getString(String path, boolean colorize) {
        String s = MinetopiaTools.getInstance().getConfig().getString(path);
        return colorize ? ChatColor.translateAlternateColorCodes('&', s) : s;
    }

    public static String format(String formatString, String... variables) {
        String s = ChatColor.translateAlternateColorCodes('&', formatString);
        List<String> vars = Arrays.asList(variables);
        if (vars.size() % 2 != 0) {
            throw new IllegalArgumentException("The amount of variables must be even!");
        }

        Iterator<String> iter = vars.iterator();
        Map<String, String> pairedVars = new HashMap<>();
        while (iter.hasNext()) {
            pairedVars.put(iter.next(), iter.next());
        }

        for (Entry<String, String> entry : pairedVars.entrySet()) {
            if (s.contains("${" + entry.getKey() + "}")) {
                s = s.replaceAll("\\$\\{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return s;
    }

}
