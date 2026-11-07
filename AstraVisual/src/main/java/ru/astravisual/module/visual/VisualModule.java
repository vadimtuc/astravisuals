package ru.astravisual.module.visual;

import net.minecraft.client.gui.DrawContext;
import ru.astravisual.module.Category;
import ru.astravisual.module.Module;

/**
 * A screen-space visual effect that is not anchored to a corner.
 */
public abstract class VisualModule extends Module {

    protected VisualModule(String name, String description) {
        super(name, description, Category.VISUAL);
    }

    public abstract void renderOverlay(DrawContext ctx);
}
