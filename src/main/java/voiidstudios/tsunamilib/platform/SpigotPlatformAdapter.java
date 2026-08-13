package voiidstudios.tsunamilib.platform;

import org.bukkit.command.CommandSender;

import voiidstudios.tsunamilib.log.YALogger;
import voiidstudios.tsunamilib.utils.TextUtils;
import voiidstudios.tsunamilib.utils.UniversalFormatter;

public class SpigotPlatformAdapter implements PlatformAdapter {
    private final UniversalFormatter formatter;

    public SpigotPlatformAdapter(YALogger logger) {
        this.formatter = new UniversalFormatter(logger);
    }

    public String getName() {
        return "Spigot/Bukkit";
    }

    public boolean isPaper() {
        return false;
    }

    public boolean supportsAdventure() {
        return false;
    }

    public void sendMessage(CommandSender sender, String message) {
        Object formatted = formatter.format(message);
        sender.sendMessage(formatted instanceof String text ? text : TextUtils.toLegacy(message));
    }
}
