package voiidstudios.tsunamilib.libs.tsunamigui;

import voiidstudios.tsunamilib.libs.tsunamigui.guis.BaseGui;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class TsunamiGui {
    private static Plugin PLUGIN = null;

    private TsunamiGui() {}

    public static void init(final Plugin plugin) {
        PLUGIN = plugin;
    }

    public static Plugin getPlugin() {
        if (PLUGIN == null) init(JavaPlugin.getProvidingPlugin(BaseGui.class));
        return PLUGIN;
    }
}