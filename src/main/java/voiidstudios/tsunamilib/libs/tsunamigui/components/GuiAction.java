package voiidstudios.tsunamilib.libs.tsunamigui.components;

import org.bukkit.event.Event;

public interface GuiAction<T extends Event> {
    void execute(final T event);
}