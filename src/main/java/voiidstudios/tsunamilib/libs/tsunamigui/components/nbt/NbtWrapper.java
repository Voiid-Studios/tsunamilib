package voiidstudios.tsunamilib.libs.tsunamigui.components.nbt;

import org.bukkit.inventory.ItemStack;

public interface NbtWrapper {
    ItemStack setString(final ItemStack itemStack, final String key, final String value);

    ItemStack removeTag(final ItemStack itemStack, final String key);

    ItemStack setBoolean(final ItemStack itemStack, final String key, final boolean value);

    String getString(final ItemStack itemStack, final String key);
}