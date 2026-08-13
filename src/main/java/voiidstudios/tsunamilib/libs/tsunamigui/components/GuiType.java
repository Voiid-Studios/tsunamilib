package voiidstudios.tsunamilib.libs.tsunamigui.components;

import org.bukkit.event.inventory.InventoryType;

public enum GuiType {
    CHEST(InventoryType.CHEST, 9, 9),
    WORKBENCH(InventoryType.WORKBENCH, 9, 10),
    HOPPER(InventoryType.HOPPER, 5, 5),
    DISPENSER(InventoryType.DISPENSER, 8, 9),
    BREWING(InventoryType.BREWING, 4, 5);

    private final InventoryType inventoryType;
    private final int limit;
    private final int fillSize;

    GuiType(final InventoryType inventoryType, final int limit, final int fillSize) {
        this.inventoryType = inventoryType;
        this.limit = limit;
        this.fillSize = fillSize;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public int getLimit() {
        return limit;
    }

    public int getFillSize() {
        return fillSize;
    }
}