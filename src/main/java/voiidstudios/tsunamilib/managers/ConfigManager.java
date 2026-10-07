package voiidstudios.tsunamilib.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import voiidstudios.tsunamilib.TLBootstrap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ConfigManager {
    private final TLBootstrap plugin;
    private final File configFile;
    private FileConfiguration config;
    private final List<String> addedKeys = new ArrayList<>();

    public ConfigManager(TLBootstrap plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    public void bootstrap() {
        ensureFolders();
        ensureCoreConfig();
        reload();
        migrateMissingKeys();
    }

    public List<String> getAddedKeys() {
        return Collections.unmodifiableList(addedKeys);
    }

    private void migrateMissingKeys() {
        addedKeys.clear();

        try (InputStream in = plugin.getResource("config.yml")) {
            if (in == null) {
                return;
            }

            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
            boolean changed = false;

            for (String key : defaults.getKeys(true)) {
                if (config.contains(key)) {
                    continue;
                }

                int dot = key.lastIndexOf('.');
                if (dot != -1) {
                    String parent = key.substring(0, dot);
                    if (config.contains(parent) && !config.isConfigurationSection(parent)) {
                        continue;
                    }
                }

                if (defaults.isConfigurationSection(key)) {
                    config.createSection(key);
                } else {
                    config.set(key, defaults.get(key));
                    addedKeys.add(key);
                }

                config.setComments(key, defaults.getComments(key));
                config.setInlineComments(key, defaults.getInlineComments(key));
                changed = true;
            }

            if (changed) {
                config.save(configFile);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Could not update config.yml with the new keys: " + e.getMessage());
        }
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

    public boolean isModernLogFormat() {
        return config.getBoolean("Config.logger.modern_format", true);
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