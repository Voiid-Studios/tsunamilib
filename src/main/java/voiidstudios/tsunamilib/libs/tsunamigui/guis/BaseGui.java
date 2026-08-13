package voiidstudios.tsunamilib.libs.tsunamigui.guis;

import voiidstudios.tsunamilib.libs.tsunamigui.TsunamiGui;
import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiContainer;
import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiAction;
import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiType;
import voiidstudios.tsunamilib.libs.tsunamigui.components.InteractionModifier;
import voiidstudios.tsunamilib.libs.tsunamigui.components.exception.GuiException;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.GuiFiller;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.VersionHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseGui implements InventoryHolder {
    private static final Plugin plugin = TsunamiGui.getPlugin();

    private static Method GET_SCHEDULER_METHOD = null;
    private static Method EXECUTE_METHOD = null;

    static {
        try {
            GET_SCHEDULER_METHOD = Entity.class.getMethod("getScheduler");
            final Class<?> entityScheduler = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            EXECUTE_METHOD = entityScheduler.getMethod("execute", Plugin.class, Runnable.class, Runnable.class, long.class);
        } catch (NoSuchMethodException | ClassNotFoundException ignored) {
        }

        Bukkit.getPluginManager().registerEvents(new InteractionModifierListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new GuiListener(), plugin);
    }

    private final GuiFiller filler = new GuiFiller(this);

    private final Map<Integer, GuiItem> guiItems;

    private final Map<Integer, GuiAction<InventoryClickEvent>> slotActions;

    private final Set<InteractionModifier> interactionModifiers;

    private final GuiContainer guiContainer;

    private Inventory inventory;

    private GuiAction<InventoryClickEvent> defaultClickAction;

    private GuiAction<InventoryClickEvent> defaultTopClickAction;

    private GuiAction<InventoryClickEvent> playerInventoryAction;

    private GuiAction<InventoryDragEvent> dragAction;

    private GuiAction<InventoryCloseEvent> closeGuiAction;

    private GuiAction<InventoryOpenEvent> openGuiAction;

    private GuiAction<InventoryClickEvent> outsideClickAction;

    private boolean updating;

    private boolean runCloseAction = true;
    private boolean runOpenAction = true;

    public BaseGui(final GuiContainer guiContainer, final Set<InteractionModifier> interactionModifiers) {
        this.interactionModifiers = safeCopyOf(interactionModifiers);
        this.guiContainer = guiContainer;
        this.inventory = guiContainer.createInventory(this);
        this.slotActions = new LinkedHashMap<>(guiContainer.inventorySize());
        this.guiItems = new LinkedHashMap<>(guiContainer.inventorySize());
    }

    private Set<InteractionModifier> safeCopyOf(final Set<InteractionModifier> set) {
        if (set.isEmpty()) return EnumSet.noneOf(InteractionModifier.class);
        else return EnumSet.copyOf(set);
    }

    public Component title() {
        return guiContainer.title();
    }

    public void setItem(final int slot, final GuiItem guiItem) {
        validateSlot(slot);
        guiItems.put(slot, guiItem);
    }

    public void removeItem(final GuiItem item) {
        guiItems.entrySet()
            .stream()
            .filter(it -> it.getValue().equals(item))
            .findFirst()
            .ifPresent(it -> {
                guiItems.remove(it.getKey());
                inventory.remove(it.getValue().getItemStack());
            });
    }

    public void removeItem(final ItemStack item) {
        guiItems.entrySet()
            .stream()
            .filter(it -> it.getValue().getItemStack().equals(item))
            .findFirst()
            .ifPresent(it -> {
                guiItems.remove(it.getKey());
                inventory.remove(item);
            });
    }

    public void removeItem(final int slot) {
        validateSlot(slot);
        guiItems.remove(slot);
        inventory.setItem(slot, null);
    }

    public void removeItem(final int row, final int col) {
        removeItem(getSlotFromRowCol(row, col));
    }

    public void setItem(final List<Integer> slots, final GuiItem guiItem) {
        for (final int slot : slots) {
            setItem(slot, guiItem);
        }
    }

    public void setItem(final int row, final int col, final GuiItem guiItem) {
        setItem(getSlotFromRowCol(row, col), guiItem);
    }

    public void clearItems() {
        this.guiItems.clear();
    }

    public void addItem(final GuiItem... items) {
        this.addItem(false, items);
    }

    public void addItem(final boolean expandIfFull, final GuiItem... items) {
        final List<GuiItem> notAddedItems = new ArrayList<>();
        final int rows = guiContainer.rows();
        final GuiType guiType = guiContainer.guiType();

        for (final GuiItem guiItem : items) {
            for (int slot = 0; slot < rows * 9; slot++) {
                if (guiItems.get(slot) != null) {
                    if (slot == rows * 9 - 1) {
                        notAddedItems.add(guiItem);
                    }
                    continue;
                }

                guiItems.put(slot, guiItem);
                break;
            }
        }

        if (!expandIfFull || rows >= 6 || notAddedItems.isEmpty() || guiType != GuiType.CHEST) {
            return;
        }

        if (!(guiContainer instanceof GuiContainer.Chest)) return;
        ((GuiContainer.Chest) guiContainer).rows(guiContainer.rows() + 1);
        this.inventory = guiContainer.createInventory(this);
        this.update();
        this.addItem(true, notAddedItems.toArray(new GuiItem[0]));
    }

    public void addSlotAction(final int slot, final GuiAction<InventoryClickEvent> slotAction) {
        validateSlot(slot);
        slotActions.put(slot, slotAction);
    }

    public void addSlotAction(final int row, final int col, final GuiAction<InventoryClickEvent> slotAction) {
        addSlotAction(getSlotFromRowCol(row, col), slotAction);
    }

    public GuiItem getGuiItem(final int slot) {
        return guiItems.get(slot);
    }

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(final boolean updating) {
        this.updating = updating;
    }

    public void open(final HumanEntity player) {
        if (player.isSleeping()) return;

        inventory.clear();
        populateGui();
        player.openInventory(inventory);
    }

    public void close(final HumanEntity player) {
        close(player, true);
    }

    public void close(final HumanEntity player, final boolean runCloseAction) {
        final Runnable task = () -> {
            this.runCloseAction = runCloseAction;
            player.closeInventory();
            this.runCloseAction = true;
        };

        if (VersionHelper.IS_FOLIA) {
            if (GET_SCHEDULER_METHOD == null || EXECUTE_METHOD == null) {
                throw new GuiException("Could not find Folia Scheduler methods.");
            }

            try {
                EXECUTE_METHOD.invoke(GET_SCHEDULER_METHOD.invoke(player), plugin, task, null, 2L);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new GuiException("Could not invoke Folia task.", e);
            }
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, task, 2L);
    }

    public void update() {
        inventory.clear();
        populateGui();
    }

    public BaseGui updateTitle(final Component title) {
        updating = true;

        final List<HumanEntity> viewers = new ArrayList<>(inventory.getViewers());

        guiContainer.title(title); // Update the title.
        updateInventory(guiContainer.createInventory(this));

        for (final HumanEntity player : viewers) {
            open(player);
        }

        updating = false;
        return this;
    }

    protected void updateInventory(final Inventory inventory) {
        this.inventory = inventory;
    }

    public void updateItem(final int slot, final ItemStack itemStack) {
        final GuiItem guiItem = guiItems.get(slot);

        if (guiItem == null) {
            updateItem(slot, new GuiItem(itemStack));
            return;
        }

        guiItem.setItemStack(itemStack);
        updateItem(slot, guiItem);
    }

    public void updateItem(final int row, final int col, final ItemStack itemStack) {
        updateItem(getSlotFromRowCol(row, col), itemStack);
    }

    public void updateItem(final int slot, final GuiItem item) {
        guiItems.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }

    public void updateItem(final int row, final int col, final GuiItem item) {
        updateItem(getSlotFromRowCol(row, col), item);
    }

    public BaseGui disableItemPlace() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
        return this;
    }

    public BaseGui disableItemTake() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
        return this;
    }

    public BaseGui disableItemSwap() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
        return this;
    }

    public BaseGui disableItemDrop() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
        return this;
    }

    public BaseGui disableOtherActions() {
        interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return this;
    }

    public BaseGui disableAllInteractions() {
        interactionModifiers.addAll(InteractionModifier.VALUES);
        return this;
    }

    public BaseGui enableItemPlace() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_PLACE);
        return this;
    }

    public BaseGui enableItemTake() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_TAKE);
        return this;
    }

    public BaseGui enableItemSwap() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_SWAP);
        return this;
    }

    public BaseGui enableItemDrop() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_DROP);
        return this;
    }

    public BaseGui enableOtherActions() {
        interactionModifiers.remove(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return this;
    }

    public BaseGui enableAllInteractions() {
        interactionModifiers.clear();
        return this;
    }

    public boolean allInteractionsDisabled() {
        return interactionModifiers.size() == InteractionModifier.VALUES.size();
    }

    public boolean canPlaceItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_PLACE);
    }

    public boolean canTakeItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_TAKE);
    }

    public boolean canSwapItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_SWAP);
    }

    public boolean canDropItems() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_DROP);
    }

    public boolean allowsOtherActions() {
        return !interactionModifiers.contains(InteractionModifier.PREVENT_OTHER_ACTIONS);
    }

    public GuiFiller getFiller() {
        return filler;
    }

    public Map<Integer, GuiItem> getGuiItems() {
        return Collections.unmodifiableMap(guiItems);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(final Inventory inventory) {
        this.inventory = inventory;
    }

    public int getRows() {
        return guiContainer.rows();
    }

    public GuiType guiType() {
        return guiContainer.guiType();
    }

    GuiAction<InventoryClickEvent> getDefaultClickAction() {
        return defaultClickAction;
    }

    public void setDefaultClickAction(final GuiAction<InventoryClickEvent> defaultClickAction) {
        this.defaultClickAction = defaultClickAction;
    }

    GuiAction<InventoryClickEvent> getDefaultTopClickAction() {
        return defaultTopClickAction;
    }

    public void setDefaultTopClickAction(final GuiAction<InventoryClickEvent> defaultTopClickAction) {
        this.defaultTopClickAction = defaultTopClickAction;
    }

    GuiAction<InventoryClickEvent> getPlayerInventoryAction() {
        return playerInventoryAction;
    }

    public void setPlayerInventoryAction(final GuiAction<InventoryClickEvent> playerInventoryAction) {
        this.playerInventoryAction = playerInventoryAction;
    }

    GuiAction<InventoryDragEvent> getDragAction() {
        return dragAction;
    }

    public void setDragAction(final GuiAction<InventoryDragEvent> dragAction) {
        this.dragAction = dragAction;
    }

    GuiAction<InventoryCloseEvent> getCloseGuiAction() {
        return closeGuiAction;
    }

    public void setCloseGuiAction(final GuiAction<InventoryCloseEvent> closeGuiAction) {
        this.closeGuiAction = closeGuiAction;
    }

    GuiAction<InventoryOpenEvent> getOpenGuiAction() {
        return openGuiAction;
    }

    public void setOpenGuiAction(final GuiAction<InventoryOpenEvent> openGuiAction) {
        this.openGuiAction = openGuiAction;
    }

    GuiAction<InventoryClickEvent> getOutsideClickAction() {
        return outsideClickAction;
    }

    public void setOutsideClickAction(final GuiAction<InventoryClickEvent> outsideClickAction) {
        this.outsideClickAction = outsideClickAction;
    }

    GuiAction<InventoryClickEvent> getSlotAction(final int slot) {
        return slotActions.get(slot);
    }

    void populateGui() {
        for (final Map.Entry<Integer, GuiItem> entry : guiItems.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
        }
    }

    boolean shouldRunCloseAction() {
        return runCloseAction;
    }

    boolean shouldRunOpenAction() {
        return runOpenAction;
    }

    int getSlotFromRowCol(final int row, final int col) {
        return (col + (row - 1) * 9) - 1;
    }

    private void validateSlot(final int slot) {
        final GuiType guiType = guiContainer.guiType();
        final int limit = guiType.getLimit();

        if (guiType == GuiType.CHEST) {
            if (slot < 0 || slot >= guiContainer.rows() * limit) throwInvalidSlot(slot);
            return;
        }

        if (slot < 0 || slot > limit) throwInvalidSlot(slot);
    }

    private void throwInvalidSlot(final int slot) {
        if (guiContainer.guiType() == GuiType.CHEST) {
            throw new GuiException("Slot " + slot + " is not valid for the gui type - " + guiContainer.guiType().name() + " and rows - " + guiContainer.rows() + "!");
        }

        throw new GuiException("Slot " + slot + " is not valid for the gui type - " + guiContainer.guiType().name() + "!");
    }

    protected GuiContainer guiContainer() {
        return guiContainer;
    }
}