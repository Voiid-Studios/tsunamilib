package voiidstudios.tsunamilib;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import voiidstudios.tsunamilib.log.YALogger;
import voiidstudios.tsunamilib.managers.AdventureManager;
import voiidstudios.tsunamilib.managers.ConfigManager;
import voiidstudios.tsunamilib.managers.MessagesManager;
import voiidstudios.tsunamilib.platform.Platform;
import voiidstudios.tsunamilib.platform.PlatformAdapter;
import voiidstudios.tsunamilib.scheduler.SchedulerAdapter;

public final class TsunamiLib {
    private static volatile TsunamiLib instance;

    private final TLBootstrap plugin;
    private final TLContext context;
    private final Platform platform;

    TsunamiLib(TLBootstrap plugin, TLContext context) {
        this.plugin = plugin;
        this.context = context;
        this.platform = Platform.detect();
    }

    static void setAPI(TsunamiLib api) {
        if (instance != null) {
            throw new IllegalStateException(
                "TsunamiLib API is already set. This should never happen twice without an onDisable() in between - something is very wrong"
            );
        }
        instance = api;
    }

    static void clearAPI() {
        instance = null;
    }

    public static TsunamiLib getAPI() {
        TsunamiLib api = instance;
        if (api == null) {
            throw new IllegalStateException(
                "TsunamiLib API is not available. Either TsunamiLib isn't installed, hasn't enabled yet, or your plugin.yml is missing 'depend: [TsunamiLib]'"
            );
        }
        return api;
    }

    public static boolean isAvailable() {
        return instance != null;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public TLContext getContext() {
        return context;
    }

    public YALogger getLogger() {
        return context.getLogger();
    }

    public Platform getPlatform() {
        return platform;
    }

    public boolean isSpigot() {
        return platform == Platform.SPIGOT;
    }

    public boolean isPaper() {
        return platform == Platform.PAPER;
    }

    public boolean isFolia() {
        return platform == Platform.FOLIA;
    }

    public PlatformAdapter getPlatformAdapter() {
        return context.getPlatform();
    }

    public SchedulerAdapter getSchedulerAdapter() {
        return context.getScheduler();
    }

    public AdventureManager getAdventureManager() {
        return context.getAdventureManager();
    }

    public ConfigManager getConfigManager() {
        return context.getConfigManager();
    }

    public MessagesManager createMessagesManager(JavaPlugin plugin, String language) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null.");
        }
        return new MessagesManager(plugin, language, context.getLogger().withName(plugin));
    }
}