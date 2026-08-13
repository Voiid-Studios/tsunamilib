package voiidstudios.tsunamilib.platform;

public enum Platform {
    SPIGOT,
    PAPER,
    FOLIA;

    private static volatile Platform cached;

    public static Platform detect() {
        Platform result = cached;
        if (result != null) {
            return result;
        }

        result = resolve();
        cached = result;
        return result;
    }

    private static Platform resolve() {
        if (hasFolia()) {
            return FOLIA;
        }

        if (hasPaper()) {
            return PAPER;
        }

        return SPIGOT;
    }

    private static boolean hasFolia() {
        return hasClass("io.papermc.paper.threadedregions.RegionizedServer")
                || hasClass("io.papermc.paper.threadedregions.RegionizedServerInitEvent");
    }

    private static boolean hasPaper() {
        return hasClass("io.papermc.paper.configuration.Configuration")
                || hasClass("com.destroystokyo.paper.PaperConfig")
                || hasClass("io.papermc.paper.ServerBuildInfo");
    }

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }
}
