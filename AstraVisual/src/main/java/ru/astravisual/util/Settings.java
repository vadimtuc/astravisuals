package ru.astravisual.util;

/**
 * Global client settings and colour palette.
 */
public final class Settings {

    /* ---- palette ---- */
    public static final int ACCENT = 0xFF8B5CF6;
    public static final int ACCENT_2 = 0xFF22D3EE;
    public static final int TEXT = 0xFFF3F4F8;
    public static final int MUTED = 0xFF9AA1B2;
    public static final int SUCCESS = 0xFF34D399;
    public static final int WARN = 0xFFFBBF24;
    public static final int DANGER = 0xFFEF4444;
    public static final int PANEL_BG = 0xFF0B0B12;
    public static final int ROW_BG = 0xFF13131B;
    public static final int ROW_HOVER = 0xFF1B1B26;
    public static final int BORDER = 0xFF262633;

    /* ---- user settings ---- */
    public static float hudScale = 1.0f;
    public static float panelOpacity = 0.85f;
    public static float crosshairSize = 1.0f;
    public static CrosshairStyle crosshairStyle = CrosshairStyle.CROSS;

    private Settings() {
    }

    public enum CrosshairStyle {
        DOT,
        CROSS,
        CIRCLE
    }
}
