package voiidstudios.tsunamilib.libs.tsunamigui.builder.gui;

import voiidstudios.tsunamilib.libs.tsunamigui.components.InteractionModifier;
import voiidstudios.tsunamilib.libs.tsunamigui.components.exception.GuiException;
import voiidstudios.tsunamilib.libs.tsunamigui.guis.BaseGui;
import net.kyori.adventure.text.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public abstract class BaseGuiBuilder<Gui extends BaseGui, Base extends BaseGuiBuilder<Gui, Base>> {
    private Component title = null;
    private final EnumSet<InteractionModifier> interactionModifiers = EnumSet.noneOf(InteractionModifier.class);

    private Consumer<Gui> consumer;

    public Base title(final Component title) {
        this.title = title;
        return (Base) this;
    }

    public Base disableItemPlace() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
        return (Base) this;
    }

    public Base disableItemTake() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
        return (Base) this;
    }

    public Base disableItemSwap() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
        return (Base) this;
    }

    public Base disableItemDrop() {
        interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
        return (Base) this;
    }

    public Base disableOtherActions() {
        interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return (Base) this;
    }

    public Base disableAllInteractions() {
        interactionModifiers.addAll(InteractionModifier.VALUES);
        return (Base) this;
    }

    public Base enableItemPlace() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_PLACE);
        return (Base) this;
    }

    public Base enableItemTake() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_TAKE);
        return (Base) this;
    }

    public Base enableItemSwap() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_SWAP);
        return (Base) this;
    }

    public Base enableItemDrop() {
        interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_DROP);
        return (Base) this;
    }

    public Base enableOtherActions() {
        interactionModifiers.remove(InteractionModifier.PREVENT_OTHER_ACTIONS);
        return (Base) this;
    }

    public Base enableAllInteractions() {
        interactionModifiers.clear();
        return (Base) this;
    }

    public Base apply(final Consumer<Gui> consumer) {
        this.consumer = consumer;
        return (Base) this;
    }

    public abstract Gui create();

    protected Component getTitle() {
        if (title == null) {
            throw new GuiException("GUI title is missing!");
        }

        return title;
    }

    protected Consumer<Gui> getConsumer() {
        return consumer;
    }

    protected Set<InteractionModifier> getModifiers() {
        return interactionModifiers;
    }

    protected void consumeBuilder(final BaseGuiBuilder<?, ?> builder) {
        this.title = builder.title;
        this.interactionModifiers.addAll(builder.interactionModifiers);
    }
}