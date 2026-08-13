package voiidstudios.tsunamilib.libs.tsunamigui.builder.item;

import voiidstudios.tsunamilib.libs.tsunamigui.components.exception.GuiException;
import voiidstudios.tsunamilib.libs.tsunamigui.components.util.VersionHelper;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@SuppressWarnings("unused")
public final class BannerBuilder extends BaseItemBuilder<BannerBuilder> {
    private static final Material DEFAULT_BANNER;
    private static final EnumSet<Material> BANNERS;

    static {
        if (VersionHelper.IS_ITEM_LEGACY) {
            DEFAULT_BANNER = Material.valueOf("BANNER");
            BANNERS = EnumSet.of(Material.valueOf("BANNER"));
        } else {
            DEFAULT_BANNER = Material.WHITE_BANNER;
            BANNERS = EnumSet.copyOf(Tag.BANNERS.getValues());
        }
    }

    BannerBuilder(final NameLoreHandler nameLoreHandler) {
        super(new ItemStack(DEFAULT_BANNER), nameLoreHandler);
    }

    BannerBuilder(final ItemStack itemStack, final NameLoreHandler nameLoreHandler) {
        super(itemStack, nameLoreHandler);
        if (!BANNERS.contains(itemStack.getType())) {
            throw new GuiException("BannerBuilder requires the material to be a banner!");
        }
    }

    public BannerBuilder baseColor(final DyeColor color) {
        final BannerMeta bannerMeta = (BannerMeta) getMeta();
        setBannerBaseColor(bannerMeta, color);
        setMeta(bannerMeta);
        return this;
    }

    private static void setBannerBaseColor(final BannerMeta bannerMeta, final DyeColor color) {
        try {
            final Method method = bannerMeta.getClass().getMethod("setBaseColor", DyeColor.class);
            method.invoke(bannerMeta, color);
        } catch (NoSuchMethodException e) {
            try {
                final Method method = bannerMeta.getClass().getMethod("setColor", DyeColor.class);
                method.invoke(bannerMeta, color);
            } catch (ReflectiveOperationException ex) {
                throw new GuiException("Unable to set banner base color", ex);
            }
        } catch (ReflectiveOperationException e) {
            throw new GuiException("Unable to set banner base color", e);
        }
    }

    public BannerBuilder pattern(final DyeColor color, final PatternType pattern) {
        final BannerMeta bannerMeta = (BannerMeta) getMeta();

        bannerMeta.addPattern(new Pattern(color, pattern));
        setMeta(bannerMeta);
        return this;
    }

    public BannerBuilder pattern(final Pattern... pattern) {
        return pattern(Arrays.asList(pattern));
    }

    public BannerBuilder pattern(final List<Pattern> patterns) {
        final BannerMeta bannerMeta = (BannerMeta) getMeta();

        for (final Pattern it : patterns) {
            bannerMeta.addPattern(it);
        }

        setMeta(bannerMeta);
        return this;
    }

    public BannerBuilder pattern(final int index, final DyeColor color, final PatternType pattern) {
        return pattern(index, new Pattern(color, pattern));
    }

    public BannerBuilder pattern(final int index, final Pattern pattern) {
        final BannerMeta bannerMeta = (BannerMeta) getMeta();

        bannerMeta.setPattern(index, pattern);
        setMeta(bannerMeta);
        return this;
    }

    public BannerBuilder setPatterns(List<Pattern> patterns) {
        final BannerMeta bannerMeta = (BannerMeta) getMeta();

        bannerMeta.setPatterns(patterns);
        setMeta(bannerMeta);
        return this;
    }
}