package voiidstudios.tsunamilib.libs.tsunamigui.builder.item;

import com.google.common.base.Preconditions;
import voiidstudios.tsunamilib.libs.tsunamigui.components.GuiAction;
import voiidstudios.tsunamilib.libs.tsunamigui.components.exception.GuiException;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.ItemNbt;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.VersionHelper;
import voiidstudios.tsunamilib.libs.tsunamigui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public abstract class BaseItemBuilder<Base extends BaseItemBuilder<Base>> {
    private static final EnumSet<Material> LEATHER_ARMOR = EnumSet.of(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);

    private ItemStack itemStack;
    private ItemMeta meta;
    private final NameLoreHandler nameLoreHandler;
    private static final Method SET_ITEM_MODEL;

    static {
        Method method = null;
        try {
            method = ItemMeta.class.getMethod("setItemModel", NamespacedKey.class);
        } catch (NoSuchMethodException ignored) {}
        SET_ITEM_MODEL = method;
    }

    protected BaseItemBuilder(final ItemStack itemStack, final NameLoreHandler nameLoreHandler) {
        Preconditions.checkNotNull(itemStack, "Item can't be null!");

        this.itemStack = itemStack;
        this.nameLoreHandler = nameLoreHandler;
        meta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
    }

    public Base name(final Component name) {
        nameLoreHandler.name(meta, name);
        return (Base) this;
    }

    public Base amount(final int amount) {
        itemStack.setAmount(amount);
        return (Base) this;
    }

    public Base lore(final Component ... lore) {
        return lore(Arrays.asList(lore));
    }

    public Base lore(final List<Component> lore) {
        nameLoreHandler.lore(meta, lore);
        return (Base) this;
    }

    public Base lore(final Consumer<List<Component>> lore) {
        nameLoreHandler.lore(meta, lore);
        return (Base) this;
    }

    public Base enchant(final Enchantment enchantment, final int level, final boolean ignoreLevelRestriction) {
        meta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return (Base) this;
    }

    public Base enchant(final Enchantment enchantment, final int level) {
        return enchant(enchantment, level, true);
    }

    public Base enchant(final Enchantment enchantment) {
        return enchant(enchantment, 1, true);
    }

    public Base enchant(final Map<Enchantment, Integer> enchantments, final boolean ignoreLevelRestriction) {
        enchantments.forEach((enchantment, level) -> this.enchant(enchantment, level, ignoreLevelRestriction));
        return (Base) this;
    }

    public Base enchant(final Map<Enchantment, Integer> enchantments) {
        return enchant(enchantments, true);
    }

    public Base disenchant(final Enchantment enchantment) {
        itemStack.removeEnchantment(enchantment);
        return (Base) this;
    }

    public Base flags(final ItemFlag... flags) {
        meta.addItemFlags(flags);
        return (Base) this;
    }

    public Base removeFlags(final ItemFlag... flags) {
        meta.removeItemFlags(flags);
        return (Base) this;
    }

    public Base unbreakable() {
        return unbreakable(true);
    }

    public Base unbreakable(boolean unbreakable) {
        if (VersionHelper.IS_UNBREAKABLE_LEGACY) {
            return setNbt("Unbreakable", unbreakable);
        }

        meta.setUnbreakable(unbreakable);
        return (Base) this;
    }

    public Base glow() {
        return glow(true);
    }

    public Base glow(boolean glow) {
        if (glow) {
            meta.addEnchant(Enchantment.LURE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            return (Base) this;
        }

        for (final Enchantment enchantment : meta.getEnchants().keySet()) {
            meta.removeEnchant(enchantment);
        }

        return (Base) this;
    }

    public Base pdc(final Consumer<PersistentDataContainer> consumer) {
        consumer.accept(meta.getPersistentDataContainer());
        return (Base) this;
    }

    public Base model(final int modelData) {
        if (VersionHelper.IS_CUSTOM_MODEL_DATA) {
            meta.setCustomModelData(modelData);
        }

        return (Base) this;
    }

    public Base model(final NamespacedKey modelKey) {
        if (SET_ITEM_MODEL != null) {
            try {
                SET_ITEM_MODEL.invoke(meta, modelKey);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new GuiException("Could not invoke setItemModel method.", e);
            }
        }

        return (Base) this;
    }

    public Base color(final Color color) {
        if (LEATHER_ARMOR.contains(itemStack.getType())) {
            final LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) getMeta();

            leatherArmorMeta.setColor(color);
            setMeta(leatherArmorMeta);
        }

        return (Base) this;
    }

    public Base setNbt(final String key, final String value) {
        itemStack.setItemMeta(meta);
        itemStack = ItemNbt.setString(itemStack, key, value);
        meta = itemStack.getItemMeta();
        return (Base) this;
    }

    public Base setNbt(final String key, final boolean value) {
        itemStack.setItemMeta(meta);
        itemStack = ItemNbt.setBoolean(itemStack, key, value);
        meta = itemStack.getItemMeta();
        return (Base) this;
    }

    public Base removeNbt(final String key) {
        itemStack.setItemMeta(meta);
        itemStack = ItemNbt.removeTag(itemStack, key);
        meta = itemStack.getItemMeta();
        return (Base) this;
    }

    private static final ItemFlag[] DEFAULT_HIDDEN_TOOLTIP_FLAGS = {
            ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_UNBREAKABLE,
            ItemFlag.HIDE_ENCHANTS,
            ItemFlag.HIDE_DYE,
            ItemFlag.HIDE_DESTROYS,
            ItemFlag.HIDE_PLACED_ON,
            ItemFlag.HIDE_ARMOR_TRIM
    };

    public ItemStack build() {
        meta.addItemFlags(DEFAULT_HIDDEN_TOOLTIP_FLAGS);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public GuiItem asGuiItem() {
        return new GuiItem(build());
    }

    public GuiItem asGuiItem(final GuiAction<InventoryClickEvent> action) {
        return new GuiItem(build(), action);
    }

    protected ItemStack getItemStack() {
        return itemStack;
    }

    protected void setItemStack(final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    protected ItemMeta getMeta() {
        return meta;
    }

    protected void setMeta(final ItemMeta meta) {
        this.meta = meta;
    }
}