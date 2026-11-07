package ru.astravisual.module.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import ru.astravisual.util.RenderUtil;
import ru.astravisual.util.Settings;

public final class FpsModule extends HudModule {

    public FpsModule() {
        super("FPS", "Frames per second");
        setPosition(Anchor.TOP_LEFT, 4, 24);
    }

    private String value() {
        return Integer.toString(MinecraftClient.getInstance().getCurrentFps());
    }

    @Override
    public int width() {
        return RenderUtil.textWidth("FPS") + RenderUtil.textWidth(value()) + 18;
    }

    @Override
    public int height() {
        return 14;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        int fps = MinecraftClient.getInstance().getCurrentFps();
        int color = fps >= 120 ? Settings.SUCCESS : fps >= 60 ? Settings.WARN : Settings.DANGER;
        RenderUtil.panel(ctx, x, y, width(), height());
        RenderUtil.text(ctx, "FPS", x + 8, y + 3, Settings.MUTED);
        RenderUtil.text(ctx, Integer.toString(fps), x + 8 + RenderUtil.textWidth("FPS") + 5, y + 3, color);
    }
}
