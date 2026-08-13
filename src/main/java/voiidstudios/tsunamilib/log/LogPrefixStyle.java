package voiidstudios.tsunamilib.log;

public final class LogPrefixStyle {
    private final String normal;
    private final String warning;
    private final String error;

    private LogPrefixStyle(String normal, String warning, String error) {
        this.normal = normal;
        this.warning = warning;
        this.error = error;
    }

    public static LogPrefixStyle of(String normal, String warning, String error) {
        if (normal == null || normal.isBlank()) {
            throw new IllegalArgumentException("The normal prefix cannot be null or blank.");
        }
        String resolvedWarning = (warning == null || warning.isBlank()) ? normal : warning;
        String resolvedError = (error == null || error.isBlank()) ? resolvedWarning : error;
        return new LogPrefixStyle(normal, resolvedWarning, resolvedError);
    }

    public static LogPrefixStyle of(String normal, String errorAndWarning) {
        return of(normal, errorAndWarning, errorAndWarning);
    }

    public static LogPrefixStyle same(String prefix) {
        return of(prefix, prefix, prefix);
    }

    static LogPrefixStyle defaultFor(String pluginName) {
        String plain = "[" + pluginName + "] ";
        return new LogPrefixStyle(plain, plain, plain);
    }

    public String normal() {
        return normal;
    }

    public String warning() {
        return warning;
    }

    public String error() {
        return error;
    }
}