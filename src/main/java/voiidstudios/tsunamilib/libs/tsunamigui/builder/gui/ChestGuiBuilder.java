package voiidstudios.tsunamilib.libs.tsunamigui.builder.gui;

import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiType;
import voiidstudios.tsunamilib.libs.tsunamigui.guis.Gui;

import java.util.function.Consumer;

public final class ChestGuiBuilder extends BaseChestGuiBuilder<Gui, ChestGuiBuilder> {
    public TypedGuiBuilder type(final GuiType guiType) {
        return new TypedGuiBuilder(guiType, this);
    }

    public Gui create() {
        final Gui gui = new Gui(createContainer(), getModifiers());
        final Consumer<Gui> consumer = getConsumer();
        if (consumer != null) consumer.accept(gui);
        return gui;
    }
}