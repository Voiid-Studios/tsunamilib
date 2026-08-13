package voiidstudios.tsunamilib.libs.tsunamigui.components.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;

public final class SkullUtil {
    private static final Material SKULL = getSkullMaterial();
    private static final Gson GSON = new Gson();

    private static Material getSkullMaterial() {
        if (VersionHelper.IS_ITEM_LEGACY) {
            return Material.valueOf("SKULL_ITEM");
        }

        return Material.PLAYER_HEAD;
    }

    public static ItemStack skull() {
        return VersionHelper.IS_ITEM_LEGACY ? new ItemStack(SKULL, 1, (short) 3) : new ItemStack(SKULL);
    }

    public static boolean isPlayerSkull(final ItemStack item) {
        if (VersionHelper.IS_ITEM_LEGACY) {
            return item.getType() == SKULL && item.getDurability() == (short) 3;
        }

        return item.getType() == SKULL;
    }

    public static String getSkinUrl(String base64Texture) {
        final String decoded = new String(Base64.getDecoder().decode(base64Texture));
        final JsonObject object = GSON.fromJson(decoded, JsonObject.class);

        final JsonElement textures = object.get("textures");

        if (textures == null) {
            return null;
        }

        final JsonElement skin = textures.getAsJsonObject().get("SKIN");

        if (skin == null) {
            return null;
        }

        final JsonElement url = skin.getAsJsonObject().get("url");
        return url == null ? null : url.getAsString();
    }
}