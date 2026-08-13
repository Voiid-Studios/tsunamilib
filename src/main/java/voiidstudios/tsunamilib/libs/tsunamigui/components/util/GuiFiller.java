package voiidstudios.tsunamilib.libs.tsunamigui.components.util;

import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiType;
import voiidstudios.tsunamilib.libs.tsunamigui.components.exception.GuiException;
import voiidstudios.tsunamilib.libs.tsunamigui.guis.BaseGui;
import voiidstudios.tsunamilib.libs.tsunamigui.guis.GuiItem;
import voiidstudios.tsunamilib.libs.tsunamigui.guis.PaginatedGui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GuiFiller {
    private final BaseGui gui;

    public GuiFiller(final BaseGui gui) {
        this.gui = gui;
    }

    public void fillTop(final GuiItem guiItem) {
        fillTop(Collections.singletonList(guiItem));
    }

    public void fillTop(final List<GuiItem> guiItems) {
        final List<GuiItem> items = repeatList(guiItems);
        for (int i = 0; i < 9; i++) {
            if (!gui.getGuiItems().containsKey(i)) gui.setItem(i, items.get(i));
        }
    }

    public void fillBottom(final GuiItem guiItem) {
        fillBottom(Collections.singletonList(guiItem));
    }

    public void fillBottom(final List<GuiItem> guiItems) {
        final int rows = gui.getRows();
        final List<GuiItem> items = repeatList(guiItems);
        for (int i = 9; i > 0; i--) {
            if (gui.getGuiItems().get((rows * 9) - i) == null) {
                gui.setItem((rows * 9) - i, items.get(i));
            }
        }
    }

    public void fillBorder(final GuiItem guiItem) {
        fillBorder(Collections.singletonList(guiItem));
    }

    public void fillBorder(final List<GuiItem> guiItems) {
        final int rows = gui.getRows();
        if (rows <= 2) return;

        final List<GuiItem> items = repeatList(guiItems);

        for (int i = 0; i < rows * 9; i++) {
            if ((i <= 8)
                    || (i >= (rows * 9) - 8) && (i <= (rows * 9) - 2)
                    || i % 9 == 0
                    || i % 9 == 8)
                gui.setItem(i, items.get(i));

        }
    }

    public void fillBetweenPoints(final int rowFrom, final int colFrom, final int rowTo, final int colTo, final GuiItem guiItem) {
        fillBetweenPoints(rowFrom, colFrom, rowTo, colTo, Collections.singletonList(guiItem));
    }

    public void fillBetweenPoints(final int rowFrom, final int colFrom, final int rowTo, final int colTo, final List<GuiItem> guiItems) {
        final int minRow = Math.min(rowFrom, rowTo);
        final int maxRow = Math.max(rowFrom, rowTo);
        final int minCol = Math.min(colFrom, colTo);
        final int maxCol = Math.max(colFrom, colTo);

        final int rows = gui.getRows();
        final List<GuiItem> items = repeatList(guiItems);

        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= 9; col++) {
                final int slot = getSlotFromRowCol(row, col);
                if (!((row >= minRow && row <= maxRow) && (col >= minCol && col <= maxCol)))
                    continue;

                gui.setItem(slot, items.get(slot));
            }
        }
    }

    public void fill(final GuiItem guiItem) {
        fill(Collections.singletonList(guiItem));
    }

    public void fill(final List<GuiItem> guiItems) {
        if (gui instanceof PaginatedGui) {
            throw new GuiException("Full filling a GUI is not supported in a Paginated GUI!");
        }

        final GuiType type = gui.guiType();

        final int fill;
        if (type == GuiType.CHEST) {
            fill = gui.getRows() * type.getLimit();
        } else {
            fill = type.getFillSize();
        }

        final List<GuiItem> items = repeatList(guiItems);
        for (int i = 0; i < fill; i++) {
            if (gui.getGuiItems().get(i) == null) gui.setItem(i, items.get(i));
        }
    }

    public void fillSide(final Side side, final List<GuiItem> guiItems) {
        switch (side) {
            case LEFT:
                this.fillBetweenPoints(1, 1, gui.getRows(), 1, guiItems);
                break;
            case RIGHT:
                this.fillBetweenPoints(1, 9, gui.getRows(), 9, guiItems);
                break;
            case BOTH:
                this.fillSide(Side.LEFT, guiItems);
                this.fillSide(Side.RIGHT, guiItems);
        }
    }

    private List<GuiItem> repeatList(final List<GuiItem> guiItems) {
        final List<GuiItem> repeated = new ArrayList<>();
        Collections.nCopies(gui.getRows() * 9, guiItems).forEach(repeated::addAll);
        return repeated;
    }

    private int getSlotFromRowCol(final int row, final int col) {
        return (col + (row - 1) * 9) - 1;
    }

    public enum Side {
        LEFT,
        RIGHT,
        BOTH
    }
}