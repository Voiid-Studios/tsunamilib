package voiidstudios.tsunamilib.platform;

import org.bukkit.Bukkit;

import java.util.OptionalInt;

public final class ServerInfo {
    private static volatile boolean resolved;
    private static String brand;
    private static String mcVersion;
    private static OptionalInt build;

    private ServerInfo() {}

    public static String brandName() {
        ensureResolved();
        return brand;
    }

    public static String minecraftVersion() {
        ensureResolved();
        return mcVersion;
    }

    public static OptionalInt buildNumber() {
        ensureResolved();
        return build;
    }

    public static Platform platform() {
        return Platform.detect();
    }

    public static String formatted() {
        OptionalInt buildNumber = buildNumber();
        if (buildNumber.isEmpty()) {
            return String.format("%s §7(MC: %s)", brandName(), minecraftVersion());
        }
        return String.format("%s §7(MC: %s, Build: %s)", brandName(), minecraftVersion(), buildNumber.getAsInt());
    }

    private static void ensureResolved() {
        if (resolved) {
            return;
        }
        synchronized (ServerInfo.class) {
            if (resolved) {
                return;
            }
            if (hasModernBuildInfo()) {
                resolveModern();
            } else {
                resolveLegacy();
            }
            resolved = true;
        }
    }

    private static boolean hasModernBuildInfo() {
        try {
            Class.forName("io.papermc.paper.ServerBuildInfo");
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    private static void resolveModern() {
        io.papermc.paper.ServerBuildInfo info = io.papermc.paper.ServerBuildInfo.buildInfo();
        brand = info.brandName();
        mcVersion = info.minecraftVersionName();
        build = info.buildNumber();
    }

    private static void resolveLegacy() {
        String bukkitVersion = Bukkit.getBukkitVersion(); // "1.8.8-R0.1-SNAPSHOT"
        mcVersion = bukkitVersion.split("-")[0];

        String raw = Bukkit.getVersion(); // "git-Paper-1618 (MC: 1.16.5)"
        brand = extractBrand(raw);
        build = OptionalInt.empty();
    }

    private static String extractBrand(String rawVersion) {
        if (rawVersion != null && rawVersion.startsWith("git-")) {
            String stripped = rawVersion.substring(4);
            int dash = stripped.indexOf('-');
            if (dash > 0) {
                return stripped.substring(0, dash);
            }
        }

        return Bukkit.getName();
    }
}