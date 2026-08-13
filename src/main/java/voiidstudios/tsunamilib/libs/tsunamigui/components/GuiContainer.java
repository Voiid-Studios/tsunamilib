package voiidstudios.tsunamilib.libs.tsunamigui.components;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface GuiContainer {
    Component title();

    void title(final Component title);

    Inventory createInventory(final InventoryHolder inventoryHolder);

    GuiType guiType();

    int inventorySize();

    int rows();

    class Chest implements GuiContainer {
        private final InventoryProvider.Chest inventoryProvider;

        private int rows;
        private Component title;

        public Chest(
            final Component title,
            final InventoryProvider.Chest inventoryProvider,
            final int rows
        ) {
            this.inventoryProvider = inventoryProvider;
            this.title = title;
            this.rows = rows;
        }

        public Component title() {
            return title;
        }

        public void title(final Component title) {
            this.title = title;
        }

        public int inventorySize() {
            return rows * 9;
        }

        public GuiType guiType() {
            return GuiType.CHEST;
        }

        public int rows() {
            return rows;
        }

        public void rows(final int rows) {
            this.rows = rows;
        }

        public Inventory createInventory(final InventoryHolder inventoryHolder) {
            return inventoryProvider.getInventory(title, inventoryHolder, inventorySize());
        }
    }

    class Typed implements GuiContainer {
        private final InventoryProvider.Typed inventoryProvider;
        private final GuiType guiType;
        private Component title;

        public Typed(
            final Component title,
            final InventoryProvider.Typed inventoryProvider,
            final GuiType guiType
        ) {
            this.inventoryProvider = inventoryProvider;
            this.title = title;
            this.guiType = guiType;
        }

        public Component title() {
            return title;
        }

        public void title(Component title) {
            this.title = title;
        }

        public int inventorySize() {
            return guiType.getLimit();
        }

        public GuiType guiType() {
            return guiType;
        }

        public int rows() {
            return 1;
        }

        public Inventory createInventory(InventoryHolder inventoryHolder) {
            return inventoryProvider.getInventory(title, inventoryHolder, guiType.getInventoryType());
        }
    }
}