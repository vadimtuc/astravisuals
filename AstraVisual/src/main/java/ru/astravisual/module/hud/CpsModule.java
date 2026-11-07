package ru.astravisual.module.hud;

import net.minecraft.client.gui.DrawContext;
import ru.astravisual.util.ClickCounter;
import ru.astravisual.util.RenderUtil;
import ru.astravisual.util.Settings;

public final class CpsModule extends HudModule {

    public CpsModule() {
        super("CPS", "Clicks per second (left / right)");
        setPosition(Anchor.TOP_LEFT, 4, 78);
    }

    private String value() {
        return ClickCounter.left() + " | " + ClickCounter.right();
    }

    @Override
    public int width() {
        return RenderUtil.textWidth("CPS") + RenderUtil.textWidth(value()) + 18;
    }

    @Override
    public int height() {
        return 14;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        int left = ClickCounter.left();
        int right = ClickCounter.right();
        RenderUtil.panel(ctx, x, y, width(), height());
        RenderUtil.text(ctx, "CPS", x + 8, y + 3, Settings.MUTED);
        int valueX = x + 8 + RenderUtil.textWidth("CPS") + 5;
        RenderUtil.text(ctx, Integer.toString(left), valueX, y + 3, left > 0 ? Settings.ACCENT : Settings.TEXT);
        int separatorX = valueX + RenderUtil.textWidth(Integer.toString(left)) + 3;
        RenderUtil.text(ctx, "|", separatorX, y + 3, Settings.MUTED);
        RenderUtil.text(ctx,
                Integer.toString(right),
                separatorX + RenderUtil.textWidth("|") + 3,
                y + 3,
                right > 0 ? Settings.ACCENT_2 : Settings.TEXT);
    }
}
