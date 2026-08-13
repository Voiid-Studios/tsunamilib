package voiidstudios.tsunamilib.libs.tsunamigui.guis;

import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiContainer;
import voiidstudios.tsunamilib.libs.tsunamigui.components.InteractionModifier;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PaginatedGui extends BaseGui {
    private final List<GuiItem> pageItems = new ArrayList<>();

    private final Map<Integer, GuiItem> currentPage;

    private int pageSize;
    private int pageNum = 1;

    public PaginatedGui(final GuiContainer guiContainer, final int pageSize, final Set<InteractionModifier> interactionModifiers) {
        super(guiContainer, interactionModifiers);
        this.pageSize = pageSize;
        this.currentPage = new LinkedHashMap<>(guiContainer.inventorySize());
    }

    public BaseGui setPageSize(final int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public void addItem(final GuiItem item) {
        pageItems.add(item);
    }

    public void addItem(final GuiItem... items) {
        pageItems.addAll(Arrays.asList(items));
    }

    public void update() {
        getInventory().clear();
        populateGui();

        updatePage();
    }

    public void updatePageItem(final int slot, final ItemStack itemStack) {
        if (!currentPage.containsKey(slot)) return;
        final GuiItem guiItem = currentPage.get(slot);
        guiItem.setItemStack(itemStack);
        getInventory().setItem(slot, guiItem.getItemStack());
    }

    public void updatePageItem(final int row, final int col, final ItemStack itemStack) {
        updateItem(getSlotFromRowCol(row, col), itemStack);
    }

    public void updatePageItem(final int slot, final GuiItem item) {
        if (!currentPage.containsKey(slot)) return;

        final GuiItem oldItem = currentPage.get(slot);
        final int index = pageItems.indexOf(currentPage.get(slot));

        currentPage.put(slot, item);
        pageItems.set(index, item);
        getInventory().setItem(slot, item.getItemStack());
    }

    public void updatePageItem(final int row, final int col, final GuiItem item) {
        updateItem(getSlotFromRowCol(row, col), item);
    }

    public void removePageItem(final GuiItem item) {
        pageItems.remove(item);
        updatePage();
    }

    public void removePageItem(final ItemStack item) {
        final Optional<GuiItem> guiItem = pageItems.stream().filter(it -> it.getItemStack().equals(item)).findFirst();
        guiItem.ifPresent(this::removePageItem);
    }

    public void open(final HumanEntity player) {
        open(player, 1);
    }

    public void open(final HumanEntity player, final int openPage) {
        if (player.isSleeping()) return;
        if (openPage <= getPagesNum() || openPage > 0) pageNum = openPage;

        getInventory().clear();
        currentPage.clear();

        populateGui();

        if (pageSize == 0) pageSize = calculatePageSize();

        populatePage();

        player.openInventory(getInventory());
    }

    public BaseGui updateTitle(final Component title) {
        setUpdating(true);

        final List<HumanEntity> viewers = new ArrayList<>(getInventory().getViewers());
        final GuiContainer guiContainer = guiContainer();

        guiContainer.title(title);
        setInventory(guiContainer.createInventory(this));

        for (final HumanEntity player : viewers) {
            open(player, getPageNum());
        }

        setUpdating(false);
        return this;
    }

    public Map<Integer, GuiItem> getCurrentPageItems() {
        return Collections.unmodifiableMap(currentPage);
    }

    public List<GuiItem> getPageItems() {
        return Collections.unmodifiableList(pageItems);
    }

    public int getCurrentPageNum() {
        return pageNum;
    }

    public int getNextPageNum() {
        if (pageNum + 1 > getPagesNum()) return pageNum;
        return pageNum + 1;
    }

    public int getPrevPageNum() {
        if (pageNum - 1 == 0) return pageNum;
        return pageNum - 1;
    }

    public boolean next() {
        if (pageNum + 1 > getPagesNum()) return false;

        pageNum++;
        updatePage();
        return true;
    }

    public boolean previous() {
        if (pageNum - 1 == 0) return false;

        pageNum--;
        updatePage();
        return true;
    }

    GuiItem getPageItem(final int slot) {
        return currentPage.get(slot);
    }

    private List<GuiItem> getPageNum(final int givenPage) {
        final int page = givenPage - 1;

        final List<GuiItem> guiPage = new ArrayList<>();

        int max = ((page * pageSize) + pageSize);
        if (max > pageItems.size()) max = pageItems.size();

        for (int i = page * pageSize; i < max; i++) {
            guiPage.add(pageItems.get(i));
        }

        return guiPage;
    }

    public int getPagesNum() {
        if (pageSize == 0) pageSize = calculatePageSize();
        return (int) Math.ceil((double) pageItems.size() / pageSize);
    }

    private void populatePage() {
        int slot = 0;
        final int inventorySize = getInventory().getSize();
        final Iterator<GuiItem> iterator = getPageNum(pageNum).iterator();
        while (iterator.hasNext()) {
            if (slot >= inventorySize) {
                break;
            }

            if (getGuiItem(slot) != null || getInventory().getItem(slot) != null) {
                slot++;
                continue;
            }

            final GuiItem guiItem = iterator.next();

            currentPage.put(slot, guiItem);
            getInventory().setItem(slot, guiItem.getItemStack());
            slot++;
        }
    }

    Map<Integer, GuiItem> getMutableCurrentPageItems() {
        return currentPage;
    }

    void clearPage() {
        for (Map.Entry<Integer, GuiItem> entry : currentPage.entrySet()) {
            getInventory().setItem(entry.getKey(), null);
        }
    }

    public void clearPageItems(final boolean update) {
        pageItems.clear();
        if (update) update();
    }

    public void clearPageItems() {
        clearPageItems(false);
    }

    int getPageSize() {
        return pageSize;
    }

    int getPageNum() {
        return pageNum;
    }

    public void setPageNum(final int pageNum) {
        this.pageNum = pageNum;
    }

    void updatePage() {
        clearPage();
        populatePage();
    }

    int calculatePageSize() {
        int counter = 0;

        for (int slot = 0; slot < getRows() * 9; slot++) {
            if (getGuiItem(slot) == null) counter++;
        }

        if (counter == 0) return 1;
        return counter;
    }
}