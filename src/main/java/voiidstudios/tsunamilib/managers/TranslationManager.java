package voiidstudios.tsunamilib.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import voiidstudios.tsunamilib.log.YALogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class TranslationManager {
    private static final String FALLBACK_LANG = "en_US";

    private final JavaPlugin plugin;
    private final YALogger logger;
    private final String langDir;

    private FileConfiguration jarFallback;
    private FileConfiguration selected;

    private String currentLang = FALLBACK_LANG;

    public TranslationManager(JavaPlugin plugin, YALogger logger, String langDir) {
        this.plugin = plugin;
        this.logger = logger;
        this.langDir = normalizeDir(langDir);
    }

    private static String normalizeDir(String dir) {
        if (dir == null || dir.isBlank()) {
            return "lang";
        }
        String normalized = dir.replace('\\', '/');
        while (normalized.startsWith("/")) normalized = normalized.substring(1);
        while (normalized.endsWith("/")) normalized = normalized.substring(0, normalized.length() - 1);
        return normalized.isEmpty() ? "lang" : normalized;
    }

    public void loadLanguage(String langCode) {
        this.currentLang = (langCode == null || langCode.isBlank()) ? FALLBACK_LANG : langCode;

        extractBundledLanguages();

        jarFallback = loadYamlFromResource(langDir + "/" + FALLBACK_LANG + ".yml");
        if (jarFallback == null || jarFallback.getKeys(true).isEmpty()) {
            logger.severe("TranslationManager for " + plugin.getName() + " could not find a bundled " + langDir + "/" + FALLBACK_LANG + ".yml resource. That file is required as the fallback language.");
            jarFallback = new YamlConfiguration();
        }

        File selectedFile = new File(plugin.getDataFolder(), langDir + "/" + currentLang + ".yml");
        if (!selectedFile.exists()) {
            logger.passiveWarning("Language file '" + currentLang + ".yml' was not found in " + langDir + "/ for " + plugin.getName() + ", falling back to " + FALLBACK_LANG + ".");
            this.currentLang = FALLBACK_LANG;
            selectedFile = new File(plugin.getDataFolder(), langDir + "/" + FALLBACK_LANG + ".yml");
        }

        selected = YamlConfiguration.loadConfiguration(selectedFile);
        syncMissingKeys(selectedFile);
    }

    private void extractBundledLanguages() {
        File jarFile = pluginJarFile();
        if (jarFile == null || !jarFile.isFile()) {
            return;
        }

        String prefix = langDir + "/";

        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (entry.isDirectory() || !name.startsWith(prefix) || !name.endsWith(".yml")) {
                    continue;
                }

                if (name.substring(prefix.length()).contains("/")) {
                    continue;
                }

                copyResourceIfAbsent(name);
            }
        } catch (IOException e) {
            logger.passiveWarning("Could not scan jar for bundled language files (" + plugin.getName() + "): " + e.getMessage());
        }
    }

    private File pluginJarFile() {
        try {
            URL url = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
            if (url.getProtocol().equals("jar")) {
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                return new File(connection.getJarFile().getName());
            }
            return new File(url.toURI());
        } catch (Exception e) {
            return null;
        }
    }

    private void copyResourceIfAbsent(String resourcePath) {
        File dest = new File(plugin.getDataFolder(), resourcePath);
        if (dest.exists()) {
            return;
        }

        InputStream is = plugin.getResource(resourcePath);
        if (is == null) {
            return;
        }

        dest.getParentFile().mkdirs();
        try (InputStream in = is; FileOutputStream out = new FileOutputStream(dest)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            logger.passiveWarning("Could not copy bundled language file: " + resourcePath + " (" + e.getMessage() + ")");
        }
    }

    private FileConfiguration loadYamlFromResource(String resourcePath) {
        InputStream is = plugin.getResource(resourcePath);
        if (is == null) {
            return null;
        }
        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            logger.passiveWarning("Failed to load language resource: " + resourcePath + " (" + e.getMessage() + ")");
            return null;
        }
    }

    private void syncMissingKeys(File selectedFile) {
        if (jarFallback == null || jarFallback.getKeys(true).isEmpty()) {
            return;
        }
        if (selected == null) {
            selected = new YamlConfiguration();
        }

        boolean changed = false;
        for (String key : jarFallback.getKeys(true)) {
            if (!selected.contains(key)) {
                selected.set(key, jarFallback.get(key));
                changed = true;
            }
        }

        if (changed) {
            try {
                selectedFile.getParentFile().mkdirs();
                selected.save(selectedFile);
            } catch (IOException e) {
                logger.severe("Failed to save language file '" + selectedFile.getName() + "': " + e.getMessage());
            }
        }
    }

    public String get(String key) {
        return getFromSources(key);
    }

    public List<String> getStringList(String key) {
        return asStringList(key);
    }

    public String formatKey(String key, Map<String, String> placeholders) {
        String raw = get(key);
        if (raw == null) {
            return null;
        }
        return formatRaw(raw, placeholders);
    }

    public String formatRaw(String raw, Map<String, String> placeholders) {
        if (raw == null) {
            return null;
        }
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                raw = raw.replace(entry.getKey(), entry.getValue());
            }
        }
        return raw;
    }

    private String getFromSources(String key) {
        if (selected != null && selected.contains(key)) {
            return selected.getString(key);
        }
        if (jarFallback != null && jarFallback.contains(key)) {
            return jarFallback.getString(key);
        }
        return null;
    }

    private List<String> asStringList(String key) {
        if (selected != null && selected.isList(key)) {
            return selected.getStringList(key);
        }
        if (jarFallback != null && jarFallback.isList(key)) {
            return jarFallback.getStringList(key);
        }

        String raw = getFromSources(key);
        if (raw == null) {
            return Collections.emptyList();
        }
        if (!raw.contains("\n")) {
            return Collections.singletonList(raw);
        }
        return Arrays.asList(raw.split("\n"));
    }

    public String getCurrentLanguage() {
        return currentLang;
    }

    public String getLangDir() {
        return langDir;
    }
}
