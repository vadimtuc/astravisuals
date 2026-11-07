package ru.astravisual.module.visual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import ru.astravisual.util.ColorUtil;
import ru.astravisual.util.RenderUtil;
import ru.astravisual.util.Settings;

public final class CrosshairModule extends VisualModule {

    public CrosshairModule() {
        super("Crosshair", "Custom crosshair with attack cooldown feedback");
    }

    @Override
    public void renderOverlay(DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) {
            return;
        }
        int cx = client.getWindow().getScaledWidth() / 2;
        int cy = client.getWindow().getScaledHeight() / 2;
        float size = Settings.crosshairSize;
        int expand = 0;
        if (client.player != null) {
            float cooldown = client.player.getAttackCooldownProgress(0.0f);
            if (cooldown < 1.0f) {
                expand = Math.round((1.0f - cooldown) * 3.0f);
            }
        }
        int white = ColorUtil.withAlpha(0xFFF7F7FC, 0.95f);
        int black = ColorUtil.withAlpha(0xFF000000, 0.55f);

        switch (Settings.crosshairStyle) {
            case DOT -> {
                int dot = Math.max(2, Math.round(2 * size));
                RenderUtil.rect(ctx, cx - dot / 2 - 1, cy - dot / 2 - 1, dot + 2, dot + 2, black);
                RenderUtil.rect(ctx, cx - dot / 2, cy - dot / 2, dot, dot, white);
            }
            case CROSS -> {
                int gap = Math.round(3 * size) + expand;
                int length = Math.max(2, Math.round(4 * size));
                int thickness = Math.max(1, Math.round(size));
                for (int i = 0; i < length; i++) {
                    int arm = gap + i;
                    // dark outline
                    RenderUtil.rect(ctx, cx + arm, cy - thickness / 2 - 1, thickness, thickness + 2, black);
                    RenderUtil.rect(ctx, cx - arm - thickness, cy - thickness / 2 - 1, thickness, thickness + 2, black);
                    RenderUtil.rect(ctx, cx - thickness / 2 - 1, cy + arm, thickness + 2, thickness, black);
                    RenderUtil.rect(ctx, cx - thickness / 2 - 1, cy - arm - thickness, thickness + 2, thickness, black);
                }
                for (int i = 0; i < length; i++) {
                    int arm = gap + i;
                    RenderUtil.rect(ctx, cx + arm, cy - thickness / 2, thickness, thickness, white);
                    RenderUtil.rect(ctx, cx - arm - thickness, cy - thickness / 2, thickness, thickness, white);
                    RenderUtil.rect(ctx, cx - thickness / 2, cy + arm, thickness, thickness, white);
                    RenderUtil.rect(ctx, cx - thickness / 2, cy - arm - thickness, thickness, thickness, white);
                }
            }
            case CIRCLE -> {
                double radius = 4.0 * size + expand;
                drawRing(ctx, cx, cy, radius + 1.0, black);
                drawRing(ctx, cx, cy, radius, white);
            }
        }
    }

    private void drawRing(DrawContext ctx, int cx, int cy, double radius, int color) {
        int steps = 100;
        for (int i = 0; i < steps; i++) {
            double angle = (Math.PI * 2.0) * i / steps;
            int px = cx + (int) Math.round(Math.cos(angle) * radius);
            int py = cy + (int) Math.round(Math.sin(angle) * radius);
            RenderUtil.rect(ctx, px, py, 1, 1, color);
        }
    }
}
