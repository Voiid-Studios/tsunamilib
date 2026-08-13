package voiidstudios.tsunamilib.libs.tsunamigui.builder.gui;

import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiContainer;
import voiidstudios.tsunamilib.libs.tsunamigui.components.InventoryProvider;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.Legacy;
import voiidstudios.tsunamilib.libs.tsunamigui.guis.BaseGui;
import org.bukkit.Bukkit;

@SuppressWarnings("unchecked")
public abstract class BaseChestGuiBuilder<Gui extends BaseGui, Base extends BaseChestGuiBuilder<Gui, Base>> extends BaseGuiBuilder<Gui, Base> {
    private int rows = 1;
    private InventoryProvider.Chest inventoryProvider = (title, owner, rows) -> Bukkit.createInventory(owner, rows, Legacy.SERIALIZER.serialize(title));

    public Base rows(final int rows) {
        this.rows = rows;
        return (Base) this;
    }

    public Base inventory(final InventoryProvider.Chest inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
        return (Base) this;
    }

    protected int getRows() {
        return rows;
    }

    protected InventoryProvider.Chest getInventoryProvider() {
        return inventoryProvider;
    }

    protected GuiContainer.Chest createContainer() {
        return new GuiContainer.Chest(getTitle(), inventoryProvider, getRows());
    }
}