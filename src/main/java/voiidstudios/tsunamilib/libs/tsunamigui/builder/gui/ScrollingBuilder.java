package voiidstudios.tsunamilib.libs.tsunamigui.builder.gui;

import voiidstudios.tsunamilib.libs.tsunamigui.components.ScrollType;
import voiidstudios.tsunamilib.libs.tsunamigui.guis.ScrollingGui;

import java.util.function.Consumer;

public final class ScrollingBuilder extends BaseChestGuiBuilder<ScrollingGui, ScrollingBuilder> {
    private ScrollType scrollType;
    private int pageSize = 0;

    public ScrollingBuilder(final ScrollType scrollType) {
        this.scrollType = scrollType;
    }

    public ScrollingBuilder scrollType(final ScrollType scrollType) {
        this.scrollType = scrollType;
        return this;
    }

    public ScrollingBuilder pageSize(final int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public ScrollingGui create() {
        final ScrollingGui gui = new ScrollingGui(createContainer(), pageSize, scrollType, getModifiers());

        final Consumer<ScrollingGui> consumer = getConsumer();
        if (consumer != null) consumer.accept(gui);

        return gui;
    }
}