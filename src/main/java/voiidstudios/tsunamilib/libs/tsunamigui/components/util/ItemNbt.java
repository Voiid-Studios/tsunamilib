package voiidstudios.tsunamilib.libs.tsunamigui.components.util;

import voiidstudios.tsunamilib.libs.tsunamigui.components.nbt.LegacyNbt;
import voiidstudios.tsunamilib.libs.tsunamigui.components.nbt.NbtWrapper;
import voiidstudios.tsunamilib.libs.tsunamigui.components.nbt.Pdc;
import org.bukkit.inventory.ItemStack;

public final class ItemNbt {
    private static final NbtWrapper nbt = selectNbt();

    public static ItemStack setString(final ItemStack itemStack, final String key, final String value) {
        return nbt.setString(itemStack, key, value);
    }

    public static String getString(final ItemStack itemStack, final String key) {
        return nbt.getString(itemStack, key);
    }

    public static ItemStack setBoolean(final ItemStack itemStack, final String key, final boolean value) {
        return nbt.setBoolean(itemStack, key, value);
    }

    public static ItemStack removeTag(final ItemStack itemStack, final String key) {
        return nbt.removeTag(itemStack, key);
    }

    private static NbtWrapper selectNbt() {
        if (VersionHelper.IS_PDC_VERSION) return new Pdc();
        return new LegacyNbt();
    }
}