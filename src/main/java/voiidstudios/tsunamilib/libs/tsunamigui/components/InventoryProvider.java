package voiidstudios.tsunamilib.libs.tsunamigui.components;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class InventoryProvider {
    public interface Chest {
        Inventory getInventory(
                final Component title,
                final InventoryHolder owner,
                final int rows
        );
    }

    public interface Typed {
        Inventory getInventory(
                final Component title,
                final InventoryHolder owner,
                final InventoryType inventoryType
        );
    }
}