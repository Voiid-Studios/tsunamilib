package voiidstudios.tsunamilib.libs.tsunamigui.builder.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import voiidstudios.tsunamilib.libs.tsunamigui.components.exception.GuiException;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.Legacy;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.LegacyTemplate;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.SkullUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemBuilder extends BaseItemBuilder<ItemBuilder> {
    private final Map<String, Object> templateVariables = new HashMap<>();

    ItemBuilder(final ItemStack itemStack) {
        super(itemStack, BukkitNameLoreHandler.getInstance());
    }

    public static ItemBuilder from(final ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    public static ItemBuilder from(final Material material) {
        return new ItemBuilder(new ItemStack(material));
    }

    public static BannerBuilder banner() {
        return new BannerBuilder(BukkitNameLoreHandler.getInstance());
    }

    public static BannerBuilder banner(final ItemStack itemStack) {
        return new BannerBuilder(itemStack, BukkitNameLoreHandler.getInstance());
    }

    public static BookBuilder book(final ItemStack itemStack) {
        return new BookBuilder(itemStack, BukkitNameLoreHandler.getInstance());
    }

    public static FireworkBuilder firework() {
        return new FireworkBuilder(new ItemStack(Material.FIREWORK_ROCKET), BukkitNameLoreHandler.getInstance());
    }

    public static FireworkBuilder firework(final ItemStack itemStack) {
        return new FireworkBuilder(itemStack, BukkitNameLoreHandler.getInstance());
    }

    public static MapBuilder map() {
        return new MapBuilder(BukkitNameLoreHandler.getInstance());
    }

    public static MapBuilder map(final ItemStack itemStack) {
        return new MapBuilder(itemStack, BukkitNameLoreHandler.getInstance());
    }

    public static SkullBuilder skull() {
        return new SkullBuilder(BukkitNameLoreHandler.getInstance());
    }

    public static SkullBuilder skull(final ItemStack itemStack) {
        return new SkullBuilder(itemStack, BukkitNameLoreHandler.getInstance());
    }

    public static FireworkBuilder star() {
        return new FireworkBuilder(new ItemStack(Material.FIREWORK_STAR), BukkitNameLoreHandler.getInstance());
    }

    public static FireworkBuilder star(final ItemStack itemStack) {
        return new FireworkBuilder(itemStack, BukkitNameLoreHandler.getInstance());
    }

    public ItemBuilder var(final String key, final Object value) {
        templateVariables.put(key, value);
        return this;
    }

    public ItemBuilder vars(final Map<String, Object> variables) {
        templateVariables.putAll(variables);
        return this;
    }

    public ItemBuilder name(final String text) {
        return name(toComponent(text));
    }

    public ItemBuilder lore(final String... lines) {
        final List<Component> components = new ArrayList<>(lines.length);
        for (final String line : lines) {
            components.add(toComponent(line));
        }
        return lore(components);
    }

    private Component toComponent(final String text) {
        final String resolved = LegacyTemplate.resolve(text, templateVariables);
        return Legacy.SERIALIZER.deserialize(resolved.replace('&', '\u00A7'))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}