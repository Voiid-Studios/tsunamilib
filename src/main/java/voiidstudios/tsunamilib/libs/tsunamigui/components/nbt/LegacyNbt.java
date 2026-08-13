package voiidstudios.tsunamilib.libs.tsunamigui.components.nbt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public final class LegacyNbt implements NbtWrapper {
    public static final String PACKAGE_NAME = Bukkit.getServer().getClass().getPackage().getName();
    public static final String NMS_VERSION = PACKAGE_NAME.substring(PACKAGE_NAME.lastIndexOf(46) + 1);

    private static Method getStringMethod;
    private static Method setStringMethod;
    private static Method setBooleanMethod;
    private static Method hasTagMethod;
    private static Method getTagMethod;
    private static Method setTagMethod;
    private static Method removeTagMethod;
    private static Method asNMSCopyMethod;
    private static Method asBukkitCopyMethod;

    private static Constructor<?> nbtCompoundConstructor;

    static {
        try {
            getStringMethod = Objects.requireNonNull(getNMSClass("NBTTagCompound")).getMethod("getString", String.class);
            removeTagMethod = Objects.requireNonNull(getNMSClass("NBTTagCompound")).getMethod("remove", String.class);
            setStringMethod = Objects.requireNonNull(getNMSClass("NBTTagCompound")).getMethod("setString", String.class, String.class);
            setBooleanMethod = Objects.requireNonNull(getNMSClass("NBTTagCompound")).getMethod("setBoolean", String.class, boolean.class);
            hasTagMethod = Objects.requireNonNull(getNMSClass("ItemStack")).getMethod("hasTag");
            getTagMethod = Objects.requireNonNull(getNMSClass("ItemStack")).getMethod("getTag");
            setTagMethod = Objects.requireNonNull(getNMSClass("ItemStack")).getMethod("setTag", getNMSClass("NBTTagCompound"));
            nbtCompoundConstructor = Objects.requireNonNull(getNMSClass("NBTTagCompound")).getDeclaredConstructor();
            asNMSCopyMethod = Objects.requireNonNull(getCraftItemStackClass()).getMethod("asNMSCopy", ItemStack.class);
            asBukkitCopyMethod = Objects.requireNonNull(getCraftItemStackClass()).getMethod("asBukkitCopy", getNMSClass("ItemStack"));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public ItemStack setString(final ItemStack itemStack, final String key, final String value) {
        if (itemStack.getType() == Material.AIR) return itemStack;

        Object nmsItemStack = asNMSCopy(itemStack);
        Object itemCompound = hasTag(nmsItemStack) ? getTag(nmsItemStack) : newNBTTagCompound();

        setString(itemCompound, key, value);
        setTag(nmsItemStack, itemCompound);

        return asBukkitCopy(nmsItemStack);
    }

    public ItemStack removeTag(final ItemStack itemStack, final String key) {
        if (itemStack.getType() == Material.AIR) return itemStack;

        Object nmsItemStack = asNMSCopy(itemStack);
        Object itemCompound = hasTag(nmsItemStack) ? getTag(nmsItemStack) : newNBTTagCompound();

        remove(itemCompound, key);
        setTag(nmsItemStack, itemCompound);

        return asBukkitCopy(nmsItemStack);
    }

    public ItemStack setBoolean(final ItemStack itemStack, final String key, final boolean value) {
        if (itemStack.getType() == Material.AIR) return itemStack;

        Object nmsItemStack = asNMSCopy(itemStack);
        Object itemCompound = hasTag(nmsItemStack) ? getTag(nmsItemStack) : newNBTTagCompound();

        setBoolean(itemCompound, key, value);
        setTag(nmsItemStack, itemCompound);

        return asBukkitCopy(nmsItemStack);
    }

    public String getString(final ItemStack itemStack, final String key) {
        if (itemStack.getType() == Material.AIR) return null;

        Object nmsItemStack = asNMSCopy(itemStack);
        Object itemCompound = hasTag(nmsItemStack) ? getTag(nmsItemStack) : newNBTTagCompound();

        return getString(itemCompound, key);
    }

    private static void setString(final Object itemCompound, final String key, final String value) {
        try {
            setStringMethod.invoke(itemCompound, key, value);
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
    }

    private static void setBoolean(final Object itemCompound, final String key, final boolean value) {
        try {
            setBooleanMethod.invoke(itemCompound, key, value);
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
    }

    private static void remove(final Object itemCompound, final String key) {
        try {
            removeTagMethod.invoke(itemCompound, key);
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
    }

    private static String getString(final Object itemCompound, final String key) {
        try {
            return (String) getStringMethod.invoke(itemCompound, key);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private static boolean hasTag(final Object nmsItemStack) {
        try {
            return (boolean) hasTagMethod.invoke(nmsItemStack);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    public static Object getTag(final Object nmsItemStack) {
        try {
            return getTagMethod.invoke(nmsItemStack);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private static void setTag(final Object nmsItemStack, final Object itemCompound) {
        try {
            setTagMethod.invoke(nmsItemStack, itemCompound);
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
    }

    private static Object newNBTTagCompound() {
        try {
            return nbtCompoundConstructor.newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            return null;
        }
    }

    public static Object asNMSCopy(final ItemStack itemStack) {
        try {
            return asNMSCopyMethod.invoke(null, itemStack);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    public static ItemStack asBukkitCopy(final Object nmsItemStack) {
        try {
            return (ItemStack) asBukkitCopyMethod.invoke(null, nmsItemStack);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private static Class<?> getNMSClass(final String className) {
        try {
            return Class.forName("net.minecraft.server." + NMS_VERSION + "." + className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Class<?> getCraftItemStackClass() {
        try {
            return Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + ".inventory.CraftItemStack");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}