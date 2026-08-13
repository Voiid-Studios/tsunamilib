package voiidstudios.tsunamilib.libs.tsunamigui.builder.gui;

import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiContainer;
import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiType;
import voiidstudios.tsunamilib.libs.tsunamigui.components.InventoryProvider;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.Legacy;
import voiidstudios.tsunamilib.libs.tsunamigui.guis.Gui;
import org.bukkit.Bukkit;

import java.util.function.Consumer;

public final class TypedGuiBuilder extends BaseGuiBuilder<Gui, TypedGuiBuilder> {
    private GuiType guiType;
    private InventoryProvider.Typed inventoryProvider = (title, owner, type) -> Bukkit.createInventory(owner, type, Legacy.SERIALIZER.serialize(title));

    public TypedGuiBuilder(final GuiType guiType) {
        this.guiType = guiType;
    }

    public TypedGuiBuilder(final GuiType guiType, final ChestGuiBuilder builder) {
        this.guiType = guiType;
        consumeBuilder(builder);
    }

    public TypedGuiBuilder type(final GuiType guiType) {
        this.guiType = guiType;
        return this;
    }

    public TypedGuiBuilder inventory(final InventoryProvider.Typed inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
        return this;
    }

    public Gui create() {
        final Gui gui = new Gui(new GuiContainer.Typed(getTitle(), inventoryProvider, guiType), getModifiers());
        final Consumer<Gui> consumer = getConsumer();
        if (consumer != null) consumer.accept(gui);
        return gui;
    }
}