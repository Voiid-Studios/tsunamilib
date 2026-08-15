package voiidstudios.tsunamilib.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import voiidstudios.tsunamilib.TLBootstrap;

import java.io.File;

public final class ConfigManager {
    private final TLBootstrap plugin;
    private final File configFile;
    private FileConfiguration config;

    public ConfigManager(TLBootstrap plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    public void bootstrap() {
        ensureFolders();
        ensureCoreConfig();
        reload();
    }

    public void reload() {
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void ensureFolders() {
        File data = plugin.getDataFolder();
        if (!data.exists()) {
            data.mkdirs();
        }
    }

    private void ensureCoreConfig() {
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean isMetricsEnabled() {
        return config.getBoolean("Config.metrics", true);
    }

    public boolean isAutoUpdate() {
        return config.getBoolean("Config.auto_updater.download", true);
    }

    public boolean isUpdateNotification() {
        return config.getBoolean("Config.auto_updater.notify", true);
    }

    public long getUpdateCheckDelaySeconds() {
        long configured = config.getLong("Config.auto_updater.delay", 28800L);
        long minimum = 300L; // 5 minutes
        return Math.max(configured, minimum);
    }

    public File getConfigFile() {
        return configFile;
    }
}