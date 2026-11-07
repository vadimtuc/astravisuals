package ru.astravisual.module.hud;

import net.minecraft.client.gui.DrawContext;
import ru.astravisual.module.Category;
import ru.astravisual.module.Module;

/**
 * A HUD widget that can be positioned with the built-in HUD editor.
 */
public abstract class HudModule extends Module {

    private Anchor anchor = Anchor.TOP_LEFT;
    private int offsetX = 4;
    private int offsetY = 4;

    private Anchor defaultAnchor = Anchor.TOP_LEFT;
    private int defaultOffsetX = 4;
    private int defaultOffsetY = 4;

    protected HudModule(String name, String description) {
        super(name, description, Category.HUD);
    }

    public abstract int width();

    public abstract int height();

    public abstract void render(DrawContext ctx, int x, int y);

    public void setPosition(Anchor anchor, int x, int y) {
        this.anchor = anchor;
        this.offsetX = x;
        this.offsetY = y;
        this.defaultAnchor = anchor;
        this.defaultOffsetX = x;
        this.defaultOffsetY = y;
    }

    public void resetPosition() {
        this.anchor = defaultAnchor;
        this.offsetX = defaultOffsetX;
        this.offsetY = defaultOffsetY;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }
}
