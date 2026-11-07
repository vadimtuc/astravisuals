package ru.astravisual.util;

public final class ColorUtil {

    private ColorUtil() {
    }

    public static int rgb(int r, int g, int b) {
        return 0xFF000000 | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b);
    }

    public static int withAlpha(int color, float alpha) {
        int a = Math.round(((color >> 24) & 0xFF) * clampF(alpha));
        a = clamp(a);
        return (a << 24) | (color & 0xFFFFFF);
    }

    public static int setAlpha(int color, int alpha) {
        return (clamp(alpha) << 24) | (color & 0xFFFFFF);
    }

    public static int lerp(int from, int to, float t) {
        t = clampF(t);
        int a = lerpChannel((from >> 24) & 0xFF, (to >> 24) & 0xFF, t);
        int r = lerpChannel((from >> 16) & 0xFF, (to >> 16) & 0xFF, t);
        int g = lerpChannel((from >> 8) & 0xFF, (to >> 8) & 0xFF, t);
        int b = lerpChannel(from & 0xFF, to & 0xFF, t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** Colour for a 0..1 health/durability ratio. */
    public static int forRatio(double ratio) {
        if (ratio > 0.6) {
            return Settings.SUCCESS;
        }
        if (ratio > 0.3) {
            return Settings.WARN;
        }
        return Settings.DANGER;
    }

    private static int lerpChannel(int a, int b, float t) {
        return Math.round(a + (b - a) * t);
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static float clampF(float value) {
        return Math.max(0f, Math.min(1f, value));
    }
}
