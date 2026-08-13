package voiidstudios.tsunamilib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import voiidstudios.tsunamilib.log.ConsoleBox;
import voiidstudios.tsunamilib.log.JavaLoggerImpl;
import voiidstudios.tsunamilib.log.LogPrefixStyle;
import voiidstudios.tsunamilib.log.YALogger;
import voiidstudios.tsunamilib.platform.ServerInfo;
import voiidstudios.tsunamilib.update.TLUpdateChecker;
import voiidstudios.tsunamilib.update.TLUpdateCheckerResult;
import voiidstudios.tsunamilib.update.TLUpdateDownloader;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class TLBootstrap extends JavaPlugin {
    public String version = getDescription().getVersion();

    private static final String WE_LOADED_PROPERTY = "tsunamilib.jvm.loaded";
    private static final String RELEASES_PAGE_URL = "https://modrinth.com/plugin/tsunami";

    private YALogger yaLogger;
    private static final LogPrefixStyle TL_PREFIX = LogPrefixStyle.of("§8[§9TsunamiLib§8] §r", "§e[TsunamiLib] ", "§c[TsunamiLib] ");

    private TLContext context;
    private TLMetrics tlMetrics;

    private TLUpdateChecker updateChecker;
    private TLUpdateDownloader updateDownloader;
    private volatile String foundNewVersion;
    private boolean firstUpdateCheck = true;

    public void onEnable() {
        yaLogger = new YALogger(new JavaLoggerImpl(Bukkit.getServer().getLogger()), true, TL_PREFIX);

        sendConsoleInformationMessage();

        if (Boolean.getBoolean(WE_LOADED_PROPERTY)) {
            sendConsoleReloadWarning();
        } else {
            System.setProperty(WE_LOADED_PROPERTY, "true");
        }

        context = new TLContext(this, yaLogger);
        tlMetrics = new TLMetrics(context);

        context.setTLMetrics(tlMetrics);

        TsunamiLib.setAPI(new TsunamiLib(this, context));

        if (context.getConfigManager().isMetricsEnabled()) {
            tlMetrics.start();
        }

        updateChecker = new TLUpdateChecker(version, yaLogger);
        updateDownloader = new TLUpdateDownloader(yaLogger, updateChecker);

        scheduleUpdateChecks();
    }

    private void scheduleUpdateChecks() {
        long delayTicks = context.getConfigManager().getUpdateCheckDelaySeconds() * 20L;

        Runnable checkTask = () -> {
            if (firstUpdateCheck) {
                yaLogger.process("Checking for updates...");
                firstUpdateCheck = false;
            } else {
                yaLogger.process("Checking for updates again...");
            }

            checkUpdates(updateChecker.check());
        };

        context.getScheduler().runAsyncTimer(checkTask, 0L, delayTicks);
    }

    public void onDisable() {
        yaLogger.process("Turning everything off...");

        if (context != null) context.getAdventureManager().stop();

        TsunamiLib.clearAPI();

        yaLogger.success("See you next time! <3");
    }

    private void checkUpdates(TLUpdateCheckerResult result) {
        if (result.isError()) {
            yaLogger.passiveWarning("Failed to check for updates: " + result.getErrorMessage());
            return;
        }

        String latest = result.getLatestVersion();
        if (latest == null) {
            return;
        }

        if (version.contains("+")) {
            yaLogger.passiveSevere("Internal / testing version detected, skipping update check...");
            return;
        }

        int comparison = compareVersions(version, latest);

        if (comparison == 0) {
            return;
        }

        if (comparison > 0) {
            yaLogger.passiveQuestion("...wait, you're running a version newer than the latest stable release?");
            yaLogger.passiveSevere("Either you're a time traveler, or something went very wrong. Skipping...");
            return;
        }

        foundNewVersion = latest;

        yaLogger.passiveInfo("Latest version found: §9v" + latest);
        yaLogger.passiveInfo("Current version: §6v" + version);

        if (context.getConfigManager().isUpdateNotification()) {
            List<String> box = ConsoleBox.builder()
                    .borderColor("§a")
                    .title("§a!!! NEW UPDATE AVAILABLE !!!")
                    .line("§aA newer version of TsunamiLib is ready for you.")
                    .blank()
                    .line("§aLatest version: §f" + latest)
                    .line("§aCurrent version: §f" + version)
                    .blank()
                    .line("§aDownload it at:")
                    .line("§f" + RELEASES_PAGE_URL)
                    .build();

            box.forEach(yaLogger::info);
        }

        if (context.getConfigManager().isAutoUpdate()) {
            yaLogger.process("Auto-update enabled. §bDownloading " + latest + "...");
            updateDownloader.downloadUpdate();
        }
    }

    private int compareVersions(String v1, String v2) {
        try {
            String[] parts1 = v1.split("[.+\\-]");
            String[] parts2 = v2.split("[.+\\-]");
            int len = Math.max(parts1.length, parts2.length);
            for (int i = 0; i < len; i++) {
                int a = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
                int b = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
                if (a != b) return a - b;
            }
            return 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getFoundNewVersion() {
        return foundNewVersion;
    }

    public void sendConsoleInformationMessage() {
        List<String> box = ConsoleBox.builder()
                .borderColor("§8")
                .title("§9TsunamiLib")
                .line("§fVersion: §9" + version)
                .line("§fRunning on: §9" + ServerInfo.formatted())
                .footer(dateText())
                .build();

        box.forEach(yaLogger::info);
    }

    private void sendConsoleReloadWarning() {
        final String[] RELOAD_PREFIXES = {
            "WHAT ARE YOU DOING?!",
            "OH HELL NO.",
            "...seriously?",
            "bro.",
            "nope. nope. nope.",
            "have you tried NOT doing that?",
            "the council does not approve.",
            "skill issue.",
            "i am so tired of you.",
            "do you feel powerful? does this make you feel powerful?",
            "i will not stand for this.",
            "i will not tolerate such ingratitude.",
            "i love you very much, but this time you've really gone too far."
        };

        String prefix = RELOAD_PREFIXES[ThreadLocalRandom.current().nextInt(RELOAD_PREFIXES.length)];

        List<String> box = ConsoleBox.builder()
                .title("⚠ " + prefix + " ⚠")
                .line("Server reload detected by TsunamiLib.")
                .line("This usually happens when you use /bukkit:reload, PlugMan, or similar.")
                .blank()
                .line("This action IS NOT SUPPORTED and may cause SERIOUS PROBLEMS with plugins")
                .line("that depend on TsunamiLib!!!")
                .blank()
                .line("YOU WILL GET NO SUPPORT FOR THE PLUGIN FOR ANY ISSUES YOU ENCOUNTER")
                .line("AFTER THE SERVER RELOAD!")
                .blank()
                .line("More info: https://madelinemiller.dev/blog/problem-with-reload/")
                .footer("#RestartYourServerAndNeverReloadIt")
                .build();

        box.forEach(yaLogger::warning);
    }

    private String dateText() {
        LocalDate date = LocalDate.now();

        switch (date.getMonth()) {
            case JANUARY:
                if (date.getDayOfMonth() == 1) {
                    return "§fNew year, new bugs... uh, I mean, adventures!! <3";
                }
                break;
            case FEBRUARY:
                if (date.getDayOfMonth() == 29) {
                    return "§fFebruary 29. I guess that's better than seeing a friendly creeper.";
                }
                break;
            case MARCH:
                if (date.getDayOfMonth() == 13) {
                    return "§fToday is my dev's birthday. Happy birthday, MaxxVoiid! :D";
                }
                break;
            case APRIL:
                if (date.getDayOfMonth() == 1) {
                    return "§fToday is April 1. If something explodes... maybe it was on purpose.";
                }
                break;
            case JUNE:
                String[] PRIDE_MESSAGES = {
                        "§cHap§6py P§erid§ae Mo§9nth§d! <3",
                        "§cJune i§6s here§e. Time §ato bri§bng out §9the co§dlors ;)",
                        "§cHave §6a won§ederf§aul Pr§bide M§9onth§d! <3"
                };

                return PRIDE_MESSAGES[ThreadLocalRandom.current().nextInt(PRIDE_MESSAGES.length)];
            case OCTOBER:
                if (date.getDayOfMonth() == 31) {
                    return "§fTonight, even the bugs are scarier... Happy Halloween!";
                }
                break;
            case DECEMBER:
                if (date.getDayOfMonth() > 23 && date.getDayOfMonth() < 27) {
                    return "§fMerry Christmas! Hopefully no plugins will give you any errors today <3";
                }
                break;
            default:
                break;
        }

        String[] VS_MESSAGES = {
                "§fMade with <3 from Voiid Studios",
                "§fThe Voiid Studios Team says hello ;)",
                "§fVoiid Studios on top! <3",
                "§fVoiid Studios was here :D"
        };

        return VS_MESSAGES[ThreadLocalRandom.current().nextInt(VS_MESSAGES.length)];
    }

    public YALogger getYALogger() {
        return yaLogger;
    }

    public TLContext getTLContext() {
        return context;
    }

    public TLBootstrap getCore() {
        return this;
    }

    public java.io.File getPluginJarFile() {
        return getFile();
    }
}