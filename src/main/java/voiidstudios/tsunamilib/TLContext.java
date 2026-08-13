package voiidstudios.tsunamilib;

import voiidstudios.tsunamilib.TLBootstrap;
import voiidstudios.tsunamilib.managers.AdventureManager;
import voiidstudios.tsunamilib.managers.ConfigManager;
import voiidstudios.tsunamilib.log.YALogger;
import voiidstudios.tsunamilib.platform.PaperPlatformAdapter;
import voiidstudios.tsunamilib.platform.PlatformAdapter;
import voiidstudios.tsunamilib.platform.SpigotPlatformAdapter;
import voiidstudios.tsunamilib.scheduler.BukkitSchedulerAdapter;
import voiidstudios.tsunamilib.scheduler.FoliaSchedulerAdapter;
import voiidstudios.tsunamilib.scheduler.SchedulerAdapter;
import voiidstudios.tsunamilib.platform.Platform;

public final class TLContext {
    private final TLBootstrap plugin;
    private final YALogger logger;
    private final ConfigManager configManager;
    private final PlatformAdapter platformAdapter;
    private final SchedulerAdapter schedulerAdapter;
    private final AdventureManager adventureManager;

    private TLMetrics tlMetrics;

    public TLContext(TLBootstrap plugin, YALogger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.configManager = new ConfigManager(plugin);
        this.configManager.bootstrap();
        this.platformAdapter = createPlatformAdapter();
        this.schedulerAdapter = createSchedulerAdapter();
        this.adventureManager = new AdventureManager(plugin, logger);
        this.adventureManager.start();
    }

    private PlatformAdapter createPlatformAdapter() {
        if (PaperPlatformAdapter.isAvailable()) {
            return new PaperPlatformAdapter(logger);
        }
        return new SpigotPlatformAdapter(logger);
    }

    private SchedulerAdapter createSchedulerAdapter() {
        if (Platform.detect() == Platform.FOLIA) {
            return new FoliaSchedulerAdapter(plugin);
        }
        return new BukkitSchedulerAdapter(plugin);
    }

    public TLBootstrap getPlugin() { return plugin; }
    public TLBootstrap getCore() { return plugin; }
    public YALogger getLogger() { return logger; }
    public ConfigManager getConfigManager() { return configManager; }
    public PlatformAdapter getPlatform() { return platformAdapter; }
    public SchedulerAdapter getScheduler() { return schedulerAdapter; }
    public AdventureManager getAdventureManager() { return adventureManager; }

    public TLMetrics getTLMetrics() { return tlMetrics; }
    public void setTLMetrics(TLMetrics tlMetrics) { this.tlMetrics = tlMetrics; }
}