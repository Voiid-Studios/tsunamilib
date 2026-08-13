package voiidstudios.tsunamilib.libs.tsunamigui.builder.item;

import voiidstudios.tsunamilib.libs.tsunamigui.components.exception.GuiException;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.Legacy;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class BookBuilder extends BaseItemBuilder<BookBuilder> {
    private static final EnumSet<Material> BOOKS = EnumSet.of(Material.WRITABLE_BOOK, Material.WRITTEN_BOOK);

    BookBuilder(final ItemStack itemStack, final NameLoreHandler nameLoreHandler) {
        super(itemStack, nameLoreHandler);
        if (!BOOKS.contains(itemStack.getType())) {
            throw new GuiException("BookBuilder requires the material to be a WRITABLE_BOOK/WRITTEN_BOOK!");
        }
    }

    public BookBuilder author(@Nullable final Component author) {
        final BookMeta bookMeta = (BookMeta) getMeta();

        if (author == null) {
            bookMeta.setAuthor(null);
            setMeta(bookMeta);
            return this;
        }

        bookMeta.setAuthor(Legacy.SERIALIZER.serialize(author));
        setMeta(bookMeta);
        return this;
    }

    public BookBuilder generation(@Nullable final BookMeta.Generation generation) {
        final BookMeta bookMeta = (BookMeta) getMeta();

        bookMeta.setGeneration(generation);
        setMeta(bookMeta);
        return this;
    }

    public BookBuilder page(final Component... pages) {
        return page(Arrays.asList(pages));
    }

    public BookBuilder page(final List<Component> pages) {
        final BookMeta bookMeta = (BookMeta) getMeta();

        for (final Component page : pages) {
            bookMeta.addPage(Legacy.SERIALIZER.serialize(page));
        }

        setMeta(bookMeta);
        return this;
    }

    public BookBuilder page(final int page, final Component data) {
        final BookMeta bookMeta = (BookMeta) getMeta();

        bookMeta.setPage(page, Legacy.SERIALIZER.serialize(data));
        setMeta(bookMeta);
        return this;
    }

    public BookBuilder title(Component title) {
        final BookMeta bookMeta = (BookMeta) getMeta();

        if (title == null) {
            bookMeta.setTitle(null);
            setMeta(bookMeta);
            return this;
        }

        bookMeta.setTitle(Legacy.SERIALIZER.serialize(title));
        setMeta(bookMeta);
        return this;
    }
}