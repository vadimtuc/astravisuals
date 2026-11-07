package ru.astravisual.module.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import ru.astravisual.AstraVisualClient;
import ru.astravisual.util.RenderUtil;
import ru.astravisual.util.Settings;

public final class WatermarkModule extends HudModule {

    private static final String LOGO = "AstraVisual";

    public WatermarkModule() {
        super("Watermark", "Client name and version");
        setPosition(Anchor.TOP_LEFT, 4, 4);
    }

    @Override
    public int width() {
        return RenderUtil.textWidth(LOGO) + RenderUtil.textWidth(AstraVisualClient.VERSION) + 20;
    }

    @Override
    public int height() {
        return 16;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        RenderUtil.panel(ctx, x, y, width(), height());
        int textY = y + 4;
        RenderUtil.text(ctx, "Visual", x + 8, textY, Settings.TEXT);
        RenderUtil.text(ctx, "PvP", x + 8 + RenderUtil.textWidth("Visual"), textY, Settings.ACCENT);
        RenderUtil.text(ctx,
                AstraVisualClient.VERSION,
                x + 8 + RenderUtil.textWidth(LOGO) + 5,
                textY,
                Settings.MUTED);
    }
}
