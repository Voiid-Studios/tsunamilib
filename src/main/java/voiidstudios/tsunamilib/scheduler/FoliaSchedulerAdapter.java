package voiidstudios.tsunamilib.scheduler;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class FoliaSchedulerAdapter implements SchedulerAdapter {
    private static final long MIN_TICKS = 1L;
    private static final long TICK_MILLIS = 50L;

    private final Plugin plugin;
    private final AsyncScheduler asyncScheduler;

    public FoliaSchedulerAdapter(Plugin plugin) {
        this.plugin = plugin;
        this.asyncScheduler = Bukkit.getAsyncScheduler();
    }

    public void runTask(Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().run(plugin, task -> runnable.run());
    }

    public void runTaskLater(Runnable runnable, long delayTicks) {
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> runnable.run(), atLeast(delayTicks));
    }

    public void runTaskTimer(Runnable runnable, long delayTicks, long periodTicks) {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, task -> runnable.run(),
                atLeast(delayTicks), atLeast(periodTicks));
    }

    public void runAsync(Runnable runnable) {
        asyncScheduler.runNow(plugin, task -> runnable.run());
    }

    public void runAsyncTimer(Runnable runnable, long delayTicks, long periodTicks) {
        asyncScheduler.runAtFixedRate(plugin, task -> runnable.run(),
                atLeast(delayTicks) * TICK_MILLIS, atLeast(periodTicks) * TICK_MILLIS, TimeUnit.MILLISECONDS);
    }

    private static long atLeast(long ticks) {
        return Math.max(ticks, MIN_TICKS);
    }
}
