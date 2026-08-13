package voiidstudios.tsunamilib.libs.tsunamigui.guis;

import voiidstudios.tsunamilib.libs.tsunamigui.builder.gui.PaginatedBuilder;
import voiidstudios.tsunamilib.libs.tsunamigui.builder.gui.ScrollingBuilder;
import voiidstudios.tsunamilib.libs.tsunamigui.builder.gui.ChestGuiBuilder;
import voiidstudios.tsunamilib.libs.tsunamigui.builder.gui.StorageBuilder;
import voiidstudios.tsunamilib.libs.tsunamigui.builder.gui.TypedGuiBuilder;
import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiContainer;
import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiType;
import voiidstudios.tsunamilib.libs.tsunamigui.components.InteractionModifier;
import voiidstudios.tsunamilib.libs.tsunamigui.components.ScrollType;

import java.util.Set;

public class Gui extends BaseGui {
    public Gui(final GuiContainer guiContainer, final Set<InteractionModifier> interactionModifiers) {
        super(guiContainer, interactionModifiers);
    }

    public static TypedGuiBuilder gui(final GuiType type) {
        return new TypedGuiBuilder(type);
    }

    public static ChestGuiBuilder gui() {
        return new ChestGuiBuilder();
    }

    public static StorageBuilder storage() {
        return new StorageBuilder();
    }

    public static PaginatedBuilder paginated() {
        return new PaginatedBuilder();
    }

    public static ScrollingBuilder scrolling(final ScrollType scrollType) {
        return new ScrollingBuilder(scrollType);
    }

    public static ScrollingBuilder scrolling() {
        return scrolling(ScrollType.VERTICAL);
    }
}