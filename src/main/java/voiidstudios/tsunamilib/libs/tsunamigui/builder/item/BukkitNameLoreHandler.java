package voiidstudios.tsunamilib.libs.tsunamigui.builder.item;

import voiidstudios.tsunamilib.libs.tsunamigui.components.exception.GuiException;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.Legacy;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.VersionHelper;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class BukkitNameLoreHandler implements NameLoreHandler {
    private static final Field DISPLAY_NAME_FIELD;
    private static final Field LORE_FIELD;

    private static final BukkitNameLoreHandler INSTANCE = new BukkitNameLoreHandler();

    static {
        try {
            final Class<?> metaClass = VersionHelper.craftClass("inventory.CraftMetaItem");

            DISPLAY_NAME_FIELD = metaClass.getDeclaredField("displayName");
            DISPLAY_NAME_FIELD.setAccessible(true);

            LORE_FIELD = metaClass.getDeclaredField("lore");
            LORE_FIELD.setAccessible(true);
        } catch (NoSuchFieldException | ClassNotFoundException exception) {
            throw new GuiException("Could not retrieve displayName nor lore field for ItemBuilder.", exception);
        }
    }

    public static BukkitNameLoreHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void name(final ItemMeta itemMeta, final Component name) {
        if (VersionHelper.IS_COMPONENT_LEGACY) {
            itemMeta.setDisplayName(Legacy.SERIALIZER.serialize(name));
            return;
        }

        try {
            DISPLAY_NAME_FIELD.set(itemMeta, this.serializeComponent(name));
        } catch (IllegalAccessException exception) {
            throw new GuiException("Could not set display name for ItemBuilder.", exception);
        }
    }

    @Override
    public void lore(final ItemMeta itemMeta, final List<Component> lore) {
        if (VersionHelper.IS_COMPONENT_LEGACY) {
            itemMeta.setLore(lore.stream().filter(Objects::nonNull).map(Legacy.SERIALIZER::serialize).collect(Collectors.toList()));
            return;
        }

        final List<Object> jsonLore = lore.stream()
                .filter(Objects::nonNull)
                .map(this::serializeComponent)
                .collect(Collectors.toList());

        try {
            LORE_FIELD.set(itemMeta, jsonLore);
        } catch (IllegalAccessException exception) {
            throw new GuiException("Could not set lore for ItemBuilder.", exception);
        }
    }

    @Override
    public void lore(final ItemMeta itemMeta, final Consumer<List<Component>> lore) {
        List<Component> components;
        if (VersionHelper.IS_COMPONENT_LEGACY) {
            final List<String> stringLore = itemMeta.getLore();
            components = (stringLore == null) ? new ArrayList<>() : stringLore.stream().map(Legacy.SERIALIZER::deserialize).collect(Collectors.toList());
        } else {
            try {
                final List<Object> jsonLore = (List<Object>) LORE_FIELD.get(itemMeta);
                components = (jsonLore == null) ? new ArrayList<>() : jsonLore.stream().map(this::deserializeComponent).collect(Collectors.toList());
            } catch (IllegalAccessException exception) {
                throw new GuiException("Could not get lore for ItemBuilder.", exception);
            }
        }

        lore.accept(components);
        lore(itemMeta, components);
    }

    private Object serializeComponent(final Component component) {
        if (VersionHelper.IS_ITEM_NAME_COMPONENT) {
            return MinecraftComponentSerializer.get().serialize(component);
        } else {
            return GsonComponentSerializer.gson().serialize(component);
        }
    }

    private Component deserializeComponent(final Object obj) {
        if (VersionHelper.IS_ITEM_NAME_COMPONENT) {
            return MinecraftComponentSerializer.get().deserialize(obj);
        } else {
            return GsonComponentSerializer.gson().deserialize((String) obj);
        }
    }
}