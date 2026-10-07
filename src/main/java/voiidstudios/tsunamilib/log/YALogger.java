package voiidstudios.tsunamilib.log;

public class YALogger {
    private final EpicPlatformLogger logger;
    private final boolean color;
    private final LogPrefixStyle prefixStyle;
    private final String namePrefix;
    private boolean debug;

    private static volatile boolean modernFormat = true;

    public static void setModernFormat(boolean modern) {
        modernFormat = modern;
    }

    public static boolean isModernFormat() {
        return modernFormat;
    }

    public YALogger(EpicPlatformLogger logger, boolean color, LogPrefixStyle prefixStyle) {
        this(logger, color, prefixStyle, "");
    }

    private YALogger(EpicPlatformLogger logger, boolean color, LogPrefixStyle prefixStyle, String namePrefix) {
        if (prefixStyle == null) {
            throw new IllegalArgumentException("prefixStyle cannot be null. A YALogger must always be scoped to an explicit prefix. Use LogPrefixStyle.defaultFor(name) if you don't have a custom LogPrefixStyle yet!");
        }
        this.logger = logger;
        this.color = color;
        this.prefixStyle = prefixStyle;
        this.namePrefix = namePrefix == null ? "" : namePrefix;
    }

    public YALogger withName(String name) {
        String tag = (name == null || name.isBlank()) ? "" : "(" + name + ") ";
        YALogger named = new YALogger(logger, color, prefixStyle, tag);
        named.debug = this.debug;
        return named;
    }

    public YALogger withStyle(LogPrefixStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        YALogger styled = new YALogger(logger, color, style, namePrefix);
        styled.debug = this.debug;
        return styled;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }


    public void debug(String message) {
        if (!debug) return;
        log(EpicLogLevel.INFO, message);
    }

    public void debug(String message, Throwable thrown) {
        if (!debug) return;
        log(EpicLogLevel.WARNING, message, thrown);
    }

    public void debug(EpicLogLevel level, String message) {
        if (!debug) return;
        log(level, message);
    }


    public void console(String message) {
        log(EpicLogLevel.CONSOLE, message);
    }

    public void console(String message, Throwable thrown) {
        log(EpicLogLevel.CONSOLE, message, thrown);
    }

    public void info(String message) {
        log(EpicLogLevel.INFO, message);
    }

    public void info(String message, Throwable thrown) {
        log(EpicLogLevel.INFO, message, thrown);
    }

    public void passiveInfo(String message) {
        log(EpicLogLevel.PASSIVE_INFO, message);
    }

    public void passiveInfo(String message, Throwable thrown) {
        log(EpicLogLevel.PASSIVE_INFO, message, thrown);
    }

    public void process(String message) {
        log(EpicLogLevel.PROCESS, message);
    }

    public void process(String message, Throwable thrown) {
        log(EpicLogLevel.PROCESS, message, thrown);
    }

    public void passiveQuestion(String message) {
        log(EpicLogLevel.PASSIVE_QUESTION, message);
    }

    public void passiveQuestion(String message, Throwable thrown) {
        log(EpicLogLevel.PASSIVE_QUESTION, message, thrown);
    }
    
    public void success(String message) {
        log(EpicLogLevel.SUCCESS, message);
    }

    public void success(String message, Throwable thrown) {
        log(EpicLogLevel.SUCCESS, message, thrown);
    }

    public void failure(String message) {
        log(EpicLogLevel.FAILURE, message);
    }

    public void failure(String message, Throwable thrown) {
        log(EpicLogLevel.FAILURE, message, thrown);
    }

    public void warning(String message) {
        log(EpicLogLevel.WARNING, message);
    }

    public void warning(String message, Throwable thrown) {
        log(EpicLogLevel.WARNING, message, thrown);
    }

    public void passiveWarning(String message) {
        log(EpicLogLevel.PASSIVE_WARNING, message);
    }

    public void passiveWarning(String message, Throwable thrown) {
        log(EpicLogLevel.PASSIVE_WARNING, message, thrown);
    }

    public void severe(String message) {
        log(EpicLogLevel.SEVERE, message);
    }

    public void severe(String message, Throwable thrown) {
        log(EpicLogLevel.SEVERE, withStackTrace(message, thrown));
    }

    public void passiveSevere(String message) {
        log(EpicLogLevel.PASSIVE_SEVERE, message);
    }

    public void passiveSevere(String message, Throwable thrown) {
        log(EpicLogLevel.PASSIVE_SEVERE, withStackTrace(message, thrown));
    }


    private void log(EpicLogLevel level, String message) {
        logger.log(level, formatMessage(level, message));
    }

    private void log(EpicLogLevel level, String message, Throwable thrown) {
        logger.log(level, formatMessage(level, message), thrown);
    }

    private static String withStackTrace(String message, Throwable thrown) {
        if (thrown == null) {
            return message;
        }

        java.io.StringWriter buffer = new java.io.StringWriter();
        thrown.printStackTrace(new java.io.PrintWriter(buffer));
        String trace = buffer.toString().replace("\r\n", "\n").trim();
        return message + "\n" + trace;
    }

    private String formatMessage(EpicLogLevel level, String message) {
        if (!namePrefix.isEmpty()) {
            message = namePrefix + message;
        }

        if (color) {
            String prefix = getPrefix(level);
            String levelColor;

            if (level == EpicLogLevel.WARNING) {
                levelColor = "§e";
            } else if (level == EpicLogLevel.SEVERE) {
                levelColor = "§c";
            } else {
                levelColor = getLevelTag(level);
            }

            if (level == EpicLogLevel.CONSOLE) {
                message = levelColor + message + "§r";
            } else {
                message = prefix + levelColor + message + "§r";
            }
        }
        return ANSIConverter.convertToAnsi(message);
    }

    private static String getLevelTag(EpicLogLevel level) {
        if (modernFormat) {
            switch (level) {
                case SUCCESS: return "[§a✓§r] ";
                case FAILURE: return "[§c×§r] ";
                case PROCESS: return "[-] ";
                case PASSIVE_INFO: return "[§9!§r] ";
                case PASSIVE_WARNING: return "[§e!§r] ";
                case PASSIVE_SEVERE: return "[§c!§r] ";
                case PASSIVE_QUESTION: return "[§6?§r] ";
                default: return "";
            }
        }

        switch (level) {
            case SUCCESS: return "§a[OK]§r ";
            case FAILURE: return "§c[FAIL]§r ";
            case PROCESS: return "[PROCESS] ";
            case PASSIVE_INFO: return "§9[INFO]§r ";
            case PASSIVE_WARNING: return "§e[WARNING]§r ";
            case PASSIVE_SEVERE: return "§c[ERROR]§r ";
            case PASSIVE_QUESTION: return "§6[QUESTION]§r ";
            default: return "";
        }
    }

    private String getPrefix(EpicLogLevel level) {
        if (level == EpicLogLevel.SEVERE) {
            return prefixStyle.error();
        }
        if (level == EpicLogLevel.WARNING) {
            return prefixStyle.warning();
        }
        return prefixStyle.normal();
    }
}