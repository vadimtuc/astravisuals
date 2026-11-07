package ru.astravisual.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Small drawing helpers shared by the HUD modules and the GUI.
 */
public final class RenderUtil {

    private RenderUtil() {
    }

    public static int textWidth(String text) {
        return MinecraftClient.getInstance().textRenderer.getWidth(text);
    }

    public static void text(DrawContext ctx, String text, int x, int y, int color) {
        ctx.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, color, true);
    }

    public static void text(DrawContext ctx, String text, int x, int y, int color, boolean shadow) {
        ctx.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, color, shadow);
    }

    public static void textRight(DrawContext ctx, String text, int right, int y, int color) {
        text(ctx, text, right - textWidth(text), y, color);
    }

    public static void textCenter(DrawContext ctx, String text, int centerX, int y, int color) {
        text(ctx, text, centerX - textWidth(text) / 2, y, color);
    }

    public static void rect(DrawContext ctx, int x, int y, int width, int height, int color) {
        if (width <= 0 || height <= 0) {
            return;
        }
        ctx.fill(x, y, x + width, y + height, color);
    }

    public static void roundedRect(DrawContext ctx, int x, int y, int width, int height, int radius, int color) {
        if (width <= 0 || height <= 0) {
            return;
        }
        int r = Math.max(0, Math.min(radius, Math.min(width, height) / 2));
        if (r == 0) {
            rect(ctx, x, y, width, height, color);
            return;
        }
        int bodyTop = y + r;
        int bodyBottom = y + height - r;
        rect(ctx, x, bodyTop, width, Math.max(0, bodyBottom - bodyTop), color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = r - (int) Math.round(Math.sqrt((double) r * r - dy * dy));
            int rowColor = color;
            rect(ctx, x + inset, y + i, width - inset * 2, 1, rowColor);
            rect(ctx, x + inset, y + height - i - 1, width - inset * 2, 1, rowColor);
        }
    }

    /** Standard HUD panel: translucent dark plate with an accent stripe on the left. */
    public static void panel(DrawContext ctx, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        roundedRect(ctx, x, y, width, height, 4, ColorUtil.withAlpha(Settings.PANEL_BG, Settings.panelOpacity));
        rect(ctx, x + 1, y + 3, 2, Math.max(0, height - 6), ColorUtil.withAlpha(Settings.ACCENT, 0.95f));
    }

    public static void outline(DrawContext ctx, int x, int y, int width, int height, int color) {
        rect(ctx, x, y, width, 1, color);
        rect(ctx, x, y + height - 1, width, 1, color);
        rect(ctx, x, y, 1, height, color);
        rect(ctx, x + width - 1, y, 1, height, color);
    }

    /** Horizontal gradient built from vertical one pixel strips. */
    public static void horizontalGradient(DrawContext ctx, int x, int y, int width, int height,
                                          int left, int right) {
        if (width <= 0 || height <= 0) {
            return;
        }
        for (int i = 0; i < width; i++) {
            float t = width == 1 ? 0f : i / (float) (width - 1);
            ctx.fill(x + i, y, x + i + 1, y + height, ColorUtil.lerp(left, right, t));
        }
    }

    public static String trim(String text, int maxWidth) {
        if (textWidth(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "...";
        int width = maxWidth - textWidth(ellipsis);
        StringBuilder builder = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (textWidth(builder.toString() + c) > width) {
                break;
            }
            builder.append(c);
        }
        return builder + ellipsis;
    }

    public static String formatDuration(int ticks) {
        int totalSeconds = Math.max(0, ticks / 20);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
