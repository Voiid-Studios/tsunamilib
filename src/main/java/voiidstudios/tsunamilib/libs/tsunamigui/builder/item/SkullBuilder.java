package voiidstudios.tsunamilib.libs.tsunamigui.builder.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import voiidstudios.tsunamilib.libs.tsunamigui.components.exception.GuiException;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.SkullUtil;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.VersionHelper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public final class SkullBuilder extends BaseItemBuilder<SkullBuilder> {
    private static final Field PROFILE_FIELD;

    static {
        Field field;

        try {
            final SkullMeta skullMeta = (SkullMeta) SkullUtil.skull().getItemMeta();
            field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
        } catch (NoSuchFieldException exception) {
            throw new GuiException("Failed to find profile field in SkullMeta!", exception);
        }

        PROFILE_FIELD = field;
    }

    SkullBuilder(final NameLoreHandler nameLoreHandler) {
        super(SkullUtil.skull(), nameLoreHandler);
    }

    SkullBuilder(final ItemStack itemStack, final NameLoreHandler nameLoreHandler) {
        super(itemStack, nameLoreHandler);
        if (!SkullUtil.isPlayerSkull(itemStack)) {
            throw new GuiException("SkullBuilder requires the material to be a PLAYER_HEAD/SKULL_ITEM!");
        }
    }

    public SkullBuilder texture(final String texture, final UUID profileId) {
        if (!SkullUtil.isPlayerSkull(getItemStack())) return this;

        if (VersionHelper.IS_PLAYER_PROFILE_API) {
            final String textureUrl = SkullUtil.getSkinUrl(texture);

            if (textureUrl == null) {
                return this;
            }

            final SkullMeta skullMeta = (SkullMeta) getMeta();
            final PlayerProfile profile = Bukkit.createPlayerProfile(profileId, "");
            final PlayerTextures textures = profile.getTextures();

            try {
                textures.setSkin(new URL(textureUrl));
            } catch (MalformedURLException exception) {
                throw new GuiException("Failed to set texture url!", exception);
            }

            profile.setTextures(textures);
            skullMeta.setOwnerProfile(profile);
            setMeta(skullMeta);
            return this;
        }

        if (PROFILE_FIELD == null) {
            return this;
        }

        final SkullMeta skullMeta = (SkullMeta) getMeta();
        final GameProfile profile = new GameProfile(profileId, "");
        profile.getProperties().put("textures", new Property("textures", texture));

        try {
            PROFILE_FIELD.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException exception) {
            throw new GuiException("Failed to set profile field!", exception);
        }

        setMeta(skullMeta);
        return this;
    }

    public SkullBuilder texture(final String texture) {
        return texture(texture, UUID.randomUUID());
    }

    public SkullBuilder owner(final OfflinePlayer player) {
        if (!SkullUtil.isPlayerSkull(getItemStack())) return this;

        final SkullMeta skullMeta = (SkullMeta) getMeta();

        if (VersionHelper.IS_SKULL_OWNER_LEGACY) {
            skullMeta.setOwner(player.getName());
        } else {
            skullMeta.setOwningPlayer(player);
        }

        setMeta(skullMeta);
        return this;
    }
}