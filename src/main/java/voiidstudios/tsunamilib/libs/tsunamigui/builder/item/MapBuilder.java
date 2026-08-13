package voiidstudios.tsunamilib.libs.tsunamigui.builder.item;

import voiidstudios.tsunamilib.libs.tsunamigui.components.exception.GuiException;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

public class MapBuilder extends BaseItemBuilder<MapBuilder> {
    private static final Material MAP = Material.MAP;

    MapBuilder(final NameLoreHandler nameLoreHandler) {
        super(new ItemStack(MAP), nameLoreHandler);
    }

    MapBuilder(final ItemStack itemStack, final NameLoreHandler nameLoreHandler) {
        super(itemStack, nameLoreHandler);
        if (itemStack.getType() != MAP) {
            throw new GuiException("MapBuilder requires the material to be a MAP!");
        }
    }

    public MapBuilder color(final Color color) {
        final MapMeta mapMeta = (MapMeta) getMeta();

        mapMeta.setColor(color);
        setMeta(mapMeta);
        return this;
    }

    public MapBuilder locationName(final String name) {
        final MapMeta mapMeta = (MapMeta) getMeta();

        mapMeta.setLocationName(name);
        setMeta(mapMeta);
        return this;
    }

    public MapBuilder scaling(final boolean scaling) {
        final MapMeta mapMeta = (MapMeta) getMeta();

        mapMeta.setScaling(scaling);
        setMeta(mapMeta);
        return this;
    }

    public MapBuilder view(final MapView view) {
        final MapMeta mapMeta = (MapMeta) getMeta();

        mapMeta.setMapView(view);
        setMeta(mapMeta);
        return this;
    }
}