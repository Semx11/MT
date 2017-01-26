package me.semx11.mt.util;

public class ChatConfig implements Configurable {

    private static final ChatConfig INSTANCE = new ChatConfig();

    public static String ERROR;
    public static String SUCCESS;

    public static String NO_PERMISSION;
    public static String NOT_PLAYER;

    public static String ACCENT;
    public static String LIGHT_ACCENT;
    public static String MAIN;
    public static String DARK_MAIN;
    public static String PREFIX;

    public static String STAFFCHAT_PREFIX;

    private ChatConfig() {
        reloadFromConfig();
    }

    /**
     * Get instance of ChatConfig. Only used for reloading the config.
     * @return The instance.
     */
    public static ChatConfig getInstance() {
        return INSTANCE;
    }

    public void reloadFromConfig() {
        ERROR = Config.getString("chat.colors.error", true);
        SUCCESS = Config.getString("chat.colors.success", true);

        NO_PERMISSION = ERROR + Config.getString("chat.errors.no_permission", false);
        NOT_PLAYER = ERROR + Config.getString("chat.errors.not_player", false);

        ACCENT = Config.getString("commands.minetopiatools.colors.accent", true);
        LIGHT_ACCENT = Config.getString("commands.minetopiatools.colors.light_accent", true);
        MAIN = Config.getString("commands.minetopiatools.colors.main", true);
        DARK_MAIN = Config.getString("commands.minetopiatools.colors.dark_main", true);
        PREFIX = ACCENT + "M" + LIGHT_ACCENT + "T " + DARK_MAIN + "> " + MAIN;

        STAFFCHAT_PREFIX = Config.getString("commands.staffchat.prefix", true);
    }
}
