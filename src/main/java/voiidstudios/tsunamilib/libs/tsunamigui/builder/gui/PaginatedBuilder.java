package voiidstudios.tsunamilib.libs.tsunamigui.builder.gui;

import voiidstudios.tsunamilib.libs.tsunamigui.guis.PaginatedGui;

import java.util.function.Consumer;

public class PaginatedBuilder extends BaseChestGuiBuilder<PaginatedGui, PaginatedBuilder> {
    private int pageSize = 0;

    public PaginatedBuilder pageSize(final int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public PaginatedGui create() {
        final PaginatedGui gui = new PaginatedGui(createContainer(), pageSize, getModifiers());

        final Consumer<PaginatedGui> consumer = getConsumer();
        if (consumer != null) consumer.accept(gui);

        return gui;
    }
}