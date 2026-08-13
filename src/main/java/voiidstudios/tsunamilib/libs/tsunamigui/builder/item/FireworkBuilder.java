package voiidstudios.tsunamilib.libs.tsunamigui.builder.item;

import voiidstudios.tsunamilib.libs.tsunamigui.components.exception.GuiException;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Arrays;
import java.util.List;

public class FireworkBuilder extends BaseItemBuilder<FireworkBuilder> {
    private static final Material STAR = Material.FIREWORK_STAR;
    private static final Material ROCKET = Material.FIREWORK_ROCKET;

    FireworkBuilder(final ItemStack itemStack, final NameLoreHandler nameLoreHandler) {
        super(itemStack, nameLoreHandler);
        if (itemStack.getType() != STAR && itemStack.getType() != ROCKET) {
            throw new GuiException("FireworkBuilder requires the material to be a FIREWORK_STAR/FIREWORK_ROCKET!");
        }
    }

    public FireworkBuilder effect(final FireworkEffect... effects) {
        return effect(Arrays.asList(effects));
    }

    public FireworkBuilder effect(final List<FireworkEffect> effects) {
        if (effects.isEmpty()) {
            return this;
        }

        if (getItemStack().getType() == STAR) {
            final FireworkEffectMeta effectMeta = (FireworkEffectMeta) getMeta();

            effectMeta.setEffect(effects.get(0));
            setMeta(effectMeta);
            return this;
        }

        final FireworkMeta fireworkMeta = (FireworkMeta) getMeta();

        fireworkMeta.addEffects(effects);
        setMeta(fireworkMeta);
        return this;
    }

    public FireworkBuilder power(final int power) {
        if (getItemStack().getType() == ROCKET) {
            final FireworkMeta fireworkMeta = (FireworkMeta) getMeta();

            fireworkMeta.setPower(power);
            setMeta(fireworkMeta);
        }

        return this;
    }
}