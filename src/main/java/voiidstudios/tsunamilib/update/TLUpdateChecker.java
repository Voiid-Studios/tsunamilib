package voiidstudios.tsunamilib.update;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import voiidstudios.tsunamilib.log.YALogger;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class TLUpdateChecker {
    private static final String API_URL = "https://api.github.com/repos/Voiid-Studios/tsunamilib/releases/latest";
    public static final String RELEASES_PAGE_URL = "https://modrinth.com/plugin/tsunami";

    private final String currentVersion;
    private final YALogger logger;
    private String latestVersion;

    public TLUpdateChecker(String currentVersion, YALogger logger) {
        this.currentVersion = currentVersion;
        this.logger = logger;
    }

    public TLUpdateCheckerResult check() {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(API_URL).openConnection();
            connection.setRequestProperty("User-Agent", "TsunamiLib-UpdateChecker");
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
            connection.setRequestMethod("GET");

            JsonObject release = JsonParser.parseReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
            ).getAsJsonObject();

            if (release.has("draft") && release.get("draft").getAsBoolean()) {
                return TLUpdateCheckerResult.noErrors(null);
            }

            if (release.has("prerelease") && release.get("prerelease").getAsBoolean()) {
                return TLUpdateCheckerResult.noErrors(null);
            }

            String tag = release.get("tag_name").getAsString().trim();
            latestVersion = tag.startsWith("v") ? tag.substring(1) : tag;

            if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                return TLUpdateCheckerResult.noErrors(latestVersion);
            }

            logger.success("You are using the latest version! <3");
            return TLUpdateCheckerResult.noErrors(null);
        } catch (Exception ex) {
            return TLUpdateCheckerResult.error(ex.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String getLatestVersion() { return latestVersion; }
    public String getCurrentVersion() { return currentVersion; }

    static String apiUrl() { return API_URL; }
}