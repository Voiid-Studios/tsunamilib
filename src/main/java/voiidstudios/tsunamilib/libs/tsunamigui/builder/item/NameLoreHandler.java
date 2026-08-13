package voiidstudios.tsunamilib.libs.tsunamigui.builder.item;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;

public interface NameLoreHandler {
    void name(final ItemMeta itemMeta, final Component name);

    void lore(final ItemMeta itemMeta, final List<Component> lore);

    void lore(final ItemMeta itemMeta, final Consumer<List<Component>> lore);
}