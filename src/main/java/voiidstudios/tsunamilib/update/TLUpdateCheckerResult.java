package voiidstudios.tsunamilib.update;

public final class TLUpdateCheckerResult {
    private final String latestVersion;
    private final boolean error;
    private final String errorMessage;

    private TLUpdateCheckerResult(String latestVersion, boolean error, String errorMessage) {
        this.latestVersion = latestVersion;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public String getLatestVersion() { return latestVersion; }
    public boolean isError() { return error; }
    public String getErrorMessage() { return errorMessage; }

    public static TLUpdateCheckerResult noErrors(String latestVersion) {
        return new TLUpdateCheckerResult(latestVersion, false, null);
    }

    public static TLUpdateCheckerResult error(String message) {
        return new TLUpdateCheckerResult(null, true, message);
    }
}