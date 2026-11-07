package ru.astravisual.module.visual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import ru.astravisual.util.ColorUtil;
import ru.astravisual.util.RenderUtil;

public final class HitMarkerModule extends VisualModule {

    private static final long DURATION_NS = 230_000_000L;

    private long lastHitNs = Long.MIN_VALUE;

    public HitMarkerModule() {
        super("Hit Marker", "Crosshair flash when you damage an entity");
    }

    public void onHit() {
        lastHitNs = System.nanoTime();
    }

    @Override
    public void renderOverlay(DrawContext ctx) {
        long now = System.nanoTime();
        long elapsed = now - lastHitNs;
        if (elapsed < 0 || elapsed > DURATION_NS) {
            return;
        }
        float progress = elapsed / (float) DURATION_NS;
        float alpha = 1.0f - progress;
        int color = ColorUtil.withAlpha(0xFFF5F5FA, alpha);
        int shadow = ColorUtil.withAlpha(0xFF000000, alpha * 0.35f);

        MinecraftClient client = MinecraftClient.getInstance();
        int cx = client.getWindow().getScaledWidth() / 2;
        int cy = client.getWindow().getScaledHeight() / 2;
        int gap = 5 + Math.round(progress * 4);
        int length = 6;

        for (int i = 0; i < length; i++) {
            RenderUtil.rect(ctx, cx + gap + i, cy + gap + i, 2, 2, shadow);
            RenderUtil.rect(ctx, cx - gap - i - 1, cy + gap + i, 2, 2, shadow);
            RenderUtil.rect(ctx, cx + gap + i, cy - gap - i - 1, 2, 2, shadow);
            RenderUtil.rect(ctx, cx - gap - i - 1, cy - gap - i - 1, 2, 2, shadow);
        }
        for (int i = 0; i < length; i++) {
            RenderUtil.rect(ctx, cx + gap + i, cy + gap + i, 1, 1, color);
            RenderUtil.rect(ctx, cx - gap - i, cy + gap + i, 1, 1, color);
            RenderUtil.rect(ctx, cx + gap + i, cy - gap - i, 1, 1, color);
            RenderUtil.rect(ctx, cx - gap - i, cy - gap - i, 1, 1, color);
        }
    }
}
