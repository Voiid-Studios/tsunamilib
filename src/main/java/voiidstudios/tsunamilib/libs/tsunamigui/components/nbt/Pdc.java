package voiidstudios.tsunamilib.libs.tsunamigui.components.nbt;

import voiidstudios.tsunamilib.libs.tsunamigui.TsunamiGui;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class Pdc implements NbtWrapper {
    private static final Plugin PLUGIN = TsunamiGui.getPlugin();

    public ItemStack setString(final ItemStack itemStack, final String key, final String value) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.getPersistentDataContainer().set(new NamespacedKey(PLUGIN, key), PersistentDataType.STRING, value);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack removeTag(final ItemStack itemStack, final String key) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.getPersistentDataContainer().remove(new NamespacedKey(PLUGIN, key));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack setBoolean(final ItemStack itemStack, final String key, final boolean value) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.getPersistentDataContainer().set(new NamespacedKey(PLUGIN, key), PersistentDataType.BYTE, value ? (byte) 1 : 0);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public String getString(final ItemStack itemStack, final String key) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(new NamespacedKey(PLUGIN, key), PersistentDataType.STRING);
    }
}