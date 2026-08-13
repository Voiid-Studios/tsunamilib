package voiidstudios.tsunamilib.libs.tsunamigui.guis;

import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiContainer;
import voiidstudios.tsunamilib.libs.tsunamigui.components.InteractionModifier;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StorageGui extends BaseGui {
    public StorageGui(final GuiContainer guiContainer, final Set<InteractionModifier> interactionModifiers) {
        super(guiContainer, interactionModifiers);
    }

    public Map<Integer, ItemStack> addItem(final ItemStack... items) {
        return Collections.unmodifiableMap(getInventory().addItem(items));
    }

    public Map<Integer, ItemStack> addItem(final List<ItemStack> items) {
        return addItem(items.toArray(new ItemStack[0]));
    }

    public void open(final HumanEntity player) {
        if (player.isSleeping()) return;
        populateGui();
        player.openInventory(getInventory());
    }

    protected void updateInventory(Inventory inventory) {
        final ItemStack[] contents = getInventory().getContents();
        super.updateInventory(inventory);
        getInventory().setContents(contents);
    }
}
