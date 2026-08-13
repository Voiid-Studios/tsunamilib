package voiidstudios.tsunamilib.platform;

import org.bukkit.command.CommandSender;

import voiidstudios.tsunamilib.log.YALogger;
import voiidstudios.tsunamilib.utils.TextUtils;
import voiidstudios.tsunamilib.utils.UniversalFormatter;

import java.lang.reflect.Method;

public class PaperPlatformAdapter implements PlatformAdapter {
    private final YALogger logger;
    private final UniversalFormatter formatter;
    private Class<?> audienceClass;
    private Class<?> componentClass;
    private Method audienceSendMessageMethod;
    private boolean warnedSendFallback;

    public PaperPlatformAdapter(YALogger logger) {
        this.logger = logger;
        this.formatter = new UniversalFormatter(logger);
        initializeAudience();
    }

    public static boolean isAvailable() {
        Platform platform = Platform.detect();
        return platform == Platform.PAPER || platform == Platform.FOLIA;
    }

    private void initializeAudience() {
        try {
            this.audienceClass = Class.forName("net.kyori.adventure.audience.Audience");
            this.componentClass = Class.forName("net.kyori.adventure.text.Component");
            this.audienceSendMessageMethod = audienceClass.getMethod("sendMessage", componentClass);
        } catch (ReflectiveOperationException | LinkageError exception) {
            warnSendFallback("Adventure is not available", exception);
            this.audienceClass = null;
            this.componentClass = null;
            this.audienceSendMessageMethod = null;
        }
    }

    public String getName() {
        return supportsAdventure() ? "Paper/Fork + Adventure" : "Paper/Fork";
    }

    public boolean isPaper() {
        return true;
    }

    public boolean supportsAdventure() {
        return audienceClass != null && componentClass != null && audienceSendMessageMethod != null;
    }

    public void sendMessage(CommandSender sender, String message) {
        Object formatted = formatter.format(message);
        if (formatted instanceof String text) {
            sender.sendMessage(text);
            return;
        }

        if (!supportsAdventure() || !componentClass.isInstance(formatted) || !audienceClass.isInstance(sender)) {
            sender.sendMessage(TextUtils.toLegacy(message));
            return;
        }

        try {
            audienceSendMessageMethod.invoke(sender, formatted);
        } catch (ReflectiveOperationException | LinkageError exception) {
            warnSendFallback("Could not send the Adventure component", exception);
            sender.sendMessage(TextUtils.toLegacy(message));
        }
    }

    private void warnSendFallback(String message, Throwable throwable) {
        if (warnedSendFallback) return;
        warnedSendFallback = true;

        logger.warning(message + ", so falling back to legacy mode: " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
    }
}
