package voiidstudios.tsunamilib.libs.tsunamigui.builder.gui;

import voiidstudios.tsunamilib.libs.tsunamigui.guis.StorageGui;

import java.util.function.Consumer;

public final class StorageBuilder extends BaseChestGuiBuilder<StorageGui, StorageBuilder> {
    public StorageGui create() {
        final StorageGui gui = new StorageGui(createContainer(), getModifiers());

        final Consumer<StorageGui> consumer = getConsumer();
        if (consumer != null) consumer.accept(gui);

        return gui;
    }
}