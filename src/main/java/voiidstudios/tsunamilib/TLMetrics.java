package voiidstudios.tsunamilib;

import dev.faststats.ErrorTracker;
import dev.faststats.bukkit.BukkitContext;
import dev.faststats.data.Metric;

public final class TLMetrics {
    private static final String PROJECT_TOKEN = "6dde783318b6f9e79068e42da832387a";

    private static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware().ignoreError(ClassNotFoundException.class);

    private final TLContext context;
    private final long startTime = System.currentTimeMillis();
    private BukkitContext bukkitContext;

    public TLMetrics(TLContext context) {
        this.context = context;
    }

    public void start() {
        if (bukkitContext != null) {
            return;
        }

        ERROR_TRACKER.getAttributes();

        bukkitContext = new BukkitContext.Factory(context.getPlugin(), PROJECT_TOKEN)
                .errorTrackerService(ERROR_TRACKER)
                .metrics(factory -> factory
                        .addMetric(Metric.number("uptime_days", () -> (System.currentTimeMillis() - startTime) / (1000L * 60 * 60 * 24)))
                        .create())
                .create();

        bukkitContext.ready();
    }

    public void stop() {
        if (bukkitContext != null) {
            bukkitContext.shutdown();
            bukkitContext = null;
        }
    }

    public static ErrorTracker getErrorTracker() {
        return ERROR_TRACKER;
    }
}