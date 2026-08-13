package voiidstudios.tsunamilib.libs.tsunamigui.guis;

import com.google.common.base.Preconditions;
import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiAction;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.ItemNbt;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GuiItem {
    private final UUID uuid = UUID.randomUUID();

    private GuiAction<InventoryClickEvent> action;

    private ItemStack itemStack;

    public GuiItem(final ItemStack itemStack, final GuiAction<InventoryClickEvent> action) {
        Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");

        this.action = action;

        setItemStack(itemStack);
    }

    public GuiItem(final ItemStack itemStack) {
        this(itemStack, null);
    }

    public GuiItem(final Material material) {
        this(new ItemStack(material), null);
    }

    public GuiItem(final Material material, final GuiAction<InventoryClickEvent> action) {
        this(new ItemStack(material), action);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(final ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack, "The ItemStack for the GUI Item cannot be null!");
        if (itemStack.getType() != Material.AIR) {
            this.itemStack = ItemNbt.setString(itemStack.clone(), "mf-gui", uuid.toString());
        } else {
            this.itemStack = itemStack.clone();
        }
    }

    public GuiAction<InventoryClickEvent> getAction() {
        return action;
    }

    public void setAction(final GuiAction<InventoryClickEvent> action) {
        this.action = action;
    }

    UUID getUuid() {
        return uuid;
    }
}