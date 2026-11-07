package ru.astravisual.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import ru.astravisual.AstraVisualClient;
import ru.astravisual.config.ConfigManager;
import ru.astravisual.module.Category;
import ru.astravisual.module.Module;
import ru.astravisual.module.ModuleManager;
import ru.astravisual.util.ColorUtil;
import ru.astravisual.util.RenderUtil;
import ru.astravisual.util.Settings;

import java.util.List;

public final class ClickGuiScreen extends Screen {

    private static final int DESIRED_WIDTH = 408;
    private static final int DESIRED_HEIGHT = 288;
    private static final int HEADER_HEIGHT = 48;
    private static final int TAB_WIDTH = 108;
    private static final int FOOTER_HEIGHT = 42;
    private static final int ROW_HEIGHT = 36;
    private static final int ROW_GAP = 5;
    private static final int ROW_STRIDE = ROW_HEIGHT + ROW_GAP;
    private static final int SETTING_STRIDE = 45;

    private enum Tab {
        HUD("HUD"),
        VISUAL("Visual"),
        SETTINGS("Settings");

        private final String label;

        Tab(String label) {
            this.label = label;
        }
    }

    private enum Slider {
        HUD_SCALE,
        PANEL_OPACITY,
        CROSSHAIR_SIZE
    }

    private Tab tab = Tab.HUD;
    private double scroll;
    private Slider draggingSlider;

    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private int contentX;
    private int contentY;
    private int contentWidth;
    private int contentHeight;

    public ClickGuiScreen() {
        super(Text.literal("AstraVisual"));
    }

    @Override
    protected void init() {
        panelWidth = Math.min(DESIRED_WIDTH, width - 16);
        panelHeight = Math.min(DESIRED_HEIGHT, height - 16);
        panelX = (width - panelWidth) / 2;
        panelY = (height - panelHeight) / 2;
        contentX = panelX + TAB_WIDTH + 10;
        contentY = panelY + HEADER_HEIGHT;
        contentWidth = panelWidth - TAB_WIDTH - 26;
        contentHeight = panelHeight - HEADER_HEIGHT - FOOTER_HEIGHT;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        RenderUtil.roundedRect(context, panelX, panelY, panelWidth, panelHeight, 8, 0xF00A0A10);
        RenderUtil.outline(context, panelX, panelY, panelWidth, panelHeight, Settings.BORDER);
        context.fillGradient(panelX + 1, panelY + 1, panelX + panelWidth - 1, panelY + 3,
                Settings.ACCENT, Settings.ACCENT_2);

        drawHeader(context);
        drawTabs(context, mouseX, mouseY);

        context.enableScissor(contentX, contentY, contentX + contentWidth, contentY + contentHeight);
        if (tab == Tab.SETTINGS) {
            drawSettings(context, mouseX, mouseY);
        } else {
            drawModules(context, mouseX, mouseY);
        }
        context.disableScissor();

        drawFooter(context, mouseX, mouseY);
    }

    private void drawHeader(DrawContext context) {
        int textY = panelY + 17;
        RenderUtil.text(context, "Visual", panelX + 20, textY, Settings.TEXT);
        RenderUtil.text(context, "PvP", panelX + 20 + RenderUtil.textWidth("Visual"), textY, Settings.ACCENT);
        RenderUtil.text(context, AstraVisualClient.VERSION,
                panelX + 20 + RenderUtil.textWidth("AstraVisual") + 6, textY, Settings.MUTED);

        String hint = "R-SHIFT";
        int hintWidth = RenderUtil.textWidth(hint);
        int chipWidth = hintWidth + 14;
        int chipX = panelX + panelWidth - chipWidth - 16;
        int chipY = panelY + 14;
        RenderUtil.roundedRect(context, chipX, chipY, chipWidth, 18, 4,
                ColorUtil.withAlpha(Settings.ACCENT, 0.15f));
        RenderUtil.text(context, hint, chipX + 7, chipY + 5, Settings.ACCENT);
    }

    private void drawTabs(DrawContext context, int mouseX, int mouseY) {
        Tab[] tabs = Tab.values();
        for (int i = 0; i < tabs.length; i++) {
            Tab value = tabs[i];
            int x = panelX + 12;
            int y = panelY + HEADER_HEIGHT + 4 + i * 30;
            int tabWidth = TAB_WIDTH - 22;
            int tabHeight = 26;
            boolean selected = tab == value;
            boolean hover = inside(mouseX, mouseY, x, y, tabWidth, tabHeight);

            int background = selected
                    ? ColorUtil.withAlpha(Settings.ACCENT, 0.18f)
                    : hover ? ColorUtil.withAlpha(Settings.ROW_HOVER, 0.9f) : 0x00000000;
            if ((background >>> 24) != 0) {
                RenderUtil.roundedRect(context, x, y, tabWidth, tabHeight, 5, background);
            }
            if (selected) {
                RenderUtil.roundedRect(context, x, y + 6, 2, tabHeight - 12, 1, Settings.ACCENT);
            }
            RenderUtil.text(context, value.label, x + 12, y + 9,
                    selected ? Settings.TEXT : Settings.MUTED);
        }
    }

    private void drawModules(DrawContext context, int mouseX, int mouseY) {
        List<Module> modules = modulesForTab();
        int totalHeight = modules.size() * ROW_STRIDE;
        clampScroll(totalHeight);

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            int rowX = contentX;
            int rowY = contentY + 4 + i * ROW_STRIDE - (int) scroll;
            if (rowY + ROW_HEIGHT < contentY || rowY > contentY + contentHeight) {
                continue;
            }
            boolean hover = inside(mouseX, mouseY, rowX, rowY, contentWidth, ROW_HEIGHT)
                    && mouseY >= contentY && mouseY <= contentY + contentHeight;
            drawModuleRow(context, module, rowX, rowY, hover);
        }
        drawScrollbar(context, totalHeight);
    }

    private void drawModuleRow(DrawContext context, Module module, int x, int y, boolean hover) {
        boolean enabled = module.isEnabled();
        RenderUtil.roundedRect(context, x, y, contentWidth, ROW_HEIGHT, 6,
                hover ? Settings.ROW_HOVER : Settings.ROW_BG);
        if (enabled) {
            RenderUtil.roundedRect(context, x, y + 8, 2, ROW_HEIGHT - 16, 1, Settings.ACCENT);
        }
        RenderUtil.text(context, module.getName(), x + 14, y + 7,
                enabled ? Settings.TEXT : 0xFFC3C6D1);
        RenderUtil.text(context, RenderUtil.trim(module.getDescription(), contentWidth - 70),
                x + 14, y + 20, Settings.MUTED);

        int toggleWidth = 30;
        int toggleHeight = 14;
        int toggleX = x + contentWidth - toggleWidth - 14;
        int toggleY = y + (ROW_HEIGHT - toggleHeight) / 2;
        RenderUtil.roundedRect(context, toggleX, toggleY, toggleWidth, toggleHeight, 7,
                enabled ? Settings.ACCENT : 0xFF2A2A36);
        int knob = 10;
        int knobX = enabled ? toggleX + toggleWidth - knob - 2 : toggleX + 2;
        RenderUtil.roundedRect(context, knobX, toggleY + 2, knob, knob, 5,
                enabled ? 0xFFFFFFFF : 0xFF8A8F9E);
    }

    private void drawSettings(DrawContext context, int mouseX, int mouseY) {
        drawSlider(context, 0, "HUD Scale", String.format("%.2fx", Settings.hudScale),
                normalize(Settings.hudScale, 0.75f, 1.5f), mouseX, mouseY);
        drawSlider(context, 1, "Panel Opacity", Math.round(Settings.panelOpacity * 100) + "%",
                normalize(Settings.panelOpacity, 0.3f, 1.0f), mouseX, mouseY);
        drawSlider(context, 2, "Crosshair Size", String.format("%.2fx", Settings.crosshairSize),
                normalize(Settings.crosshairSize, 0.5f, 2.0f), mouseX, mouseY);

        int x = contentX;
        int y = contentY + 8 + 3 * SETTING_STRIDE;
        boolean hover = inside(mouseX, mouseY, x, y, contentWidth, 40)
                && mouseY >= contentY && mouseY <= contentY + contentHeight;
        RenderUtil.roundedRect(context, x, y, contentWidth, 40, 6,
                hover ? Settings.ROW_HOVER : Settings.ROW_BG);
        RenderUtil.text(context, "Crosshair Style", x + 14, y + 9, Settings.TEXT);

        String value = Settings.crosshairStyle.name();
        int valueWidth = RenderUtil.textWidth(value) + 18;
        int chipX = x + contentWidth - valueWidth - 14;
        RenderUtil.roundedRect(context, chipX, y + 21, valueWidth, 14, 4,
                ColorUtil.withAlpha(Settings.ACCENT, 0.18f));
        RenderUtil.textCenter(context, value, chipX + valueWidth / 2, y + 24, Settings.ACCENT);
    }

    private void drawSlider(DrawContext context, int index, String label, String value,
                            float normalized, int mouseX, int mouseY) {
        int x = contentX;
        int y = contentY + 8 + index * SETTING_STRIDE;
        boolean hover = inside(mouseX, mouseY, x, y, contentWidth, 40)
                && mouseY >= contentY && mouseY <= contentY + contentHeight;
        RenderUtil.roundedRect(context, x, y, contentWidth, 40, 6,
                hover ? Settings.ROW_HOVER : Settings.ROW_BG);
        RenderUtil.text(context, label, x + 14, y + 9, Settings.TEXT);
        RenderUtil.textRight(context, value, x + contentWidth - 14, y + 9, Settings.ACCENT);

        int trackX = x + 14;
        int trackY = y + 28;
        int trackWidth = contentWidth - 28;
        RenderUtil.roundedRect(context, trackX, trackY, trackWidth, 4, 2, 0xFF2A2A36);
        int filled = Math.round(trackWidth * normalized);
        if (filled > 0) {
            RenderUtil.roundedRect(context, trackX, trackY, filled, 4, 2, Settings.ACCENT);
        }
        int knobX = trackX + filled;
        RenderUtil.roundedRect(context, knobX - 5, trackY - 3, 10, 10, 5, 0xFFFFFFFF);
    }

    private void drawScrollbar(DrawContext context, int totalHeight) {
        if (totalHeight <= contentHeight) {
            return;
        }
        int barX = contentX + contentWidth - 3;
        int barHeight = contentHeight - 8;
        int barY = contentY + 4;
        RenderUtil.roundedRect(context, barX, barY, 3, barHeight, 1, 0x552A2A36);
        double ratio = (double) contentHeight / totalHeight;
        int thumbHeight = Math.max(18, (int) Math.round(barHeight * ratio));
        double maxScroll = totalHeight - contentHeight + 8;
        int thumbY = barY + (int) Math.round((barHeight - thumbHeight) * (scroll / maxScroll));
        RenderUtil.roundedRect(context, barX, thumbY, 3, thumbHeight, 1,
                ColorUtil.withAlpha(Settings.ACCENT, 0.7f));
    }

    private void drawFooter(DrawContext context, int mouseX, int mouseY) {
        int footerY = panelY + panelHeight - FOOTER_HEIGHT;
        RenderUtil.rect(context, panelX + 1, footerY, panelWidth - 2, 1,
                ColorUtil.withAlpha(Settings.BORDER, 0.8f));

        drawButton(context, "Edit HUD", panelX + 16, footerY + 9, 92, 24, mouseX, mouseY, true);
        drawButton(context, "Reset HUD", panelX + 116, footerY + 9, 92, 24, mouseX, mouseY, false);

        String hint = "ESC to close";
        RenderUtil.textRight(context, hint,
                panelX + panelWidth - 16, footerY + 17, Settings.MUTED);
    }

    private void drawButton(DrawContext context, String label, int x, int y, int width, int height,
                            int mouseX, int mouseY, boolean accent) {
        boolean hover = inside(mouseX, mouseY, x, y, width, height);
        int background = accent
                ? ColorUtil.withAlpha(Settings.ACCENT, hover ? 0.30f : 0.18f)
                : ColorUtil.withAlpha(Settings.ROW_HOVER, hover ? 1.0f : 0.7f);
        RenderUtil.roundedRect(context, x, y, width, height, 5, background);
        RenderUtil.textCenter(context, label, x + width / 2, y + (height - 8) / 2,
                accent ? Settings.ACCENT : Settings.MUTED);
    }

    private List<Module> modulesForTab() {
        Category category = tab == Tab.HUD ? Category.HUD : Category.VISUAL;
        return ModuleManager.MODULES.stream()
                .filter(module -> module.getCategory() == category)
                .toList();
    }

    private void clampScroll(int totalHeight) {
        double max = Math.max(0, totalHeight - contentHeight + 8);
        scroll = Math.max(0, Math.min(max, scroll));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        Tab[] tabs = Tab.values();
        for (int i = 0; i < tabs.length; i++) {
            int x = panelX + 12;
            int y = panelY + HEADER_HEIGHT + 4 + i * 30;
            if (inside(mouseX, mouseY, x, y, TAB_WIDTH - 22, 26)) {
                tab = tabs[i];
                scroll = 0;
                return true;
            }
        }

        int footerY = panelY + panelHeight - FOOTER_HEIGHT;
        if (inside(mouseX, mouseY, panelX + 16, footerY + 9, 92, 24)) {
            if (client != null) {
                client.setScreen(new HudEditorScreen());
            }
            return true;
        }
        if (inside(mouseX, mouseY, panelX + 116, footerY + 9, 92, 24)) {
            ModuleManager.resetHudPositions();
            ConfigManager.save();
            return true;
        }

        if (!inside(mouseX, mouseY, contentX, contentY, contentWidth, contentHeight)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (tab == Tab.SETTINGS) {
            return clickSetting(mouseX, mouseY);
        }

        List<Module> modules = modulesForTab();
        for (int i = 0; i < modules.size(); i++) {
            int rowY = contentY + 4 + i * ROW_STRIDE - (int) scroll;
            if (inside(mouseX, mouseY, contentX, rowY, contentWidth, ROW_HEIGHT)) {
                modules.get(i).toggle();
                return true;
            }
        }
        return true;
    }

    private boolean clickSetting(double mouseX, double mouseY) {
        for (int i = 0; i < 3; i++) {
            int y = contentY + 8 + i * SETTING_STRIDE;
            if (inside(mouseX, mouseY, contentX, y, contentWidth, 40)) {
                Slider slider = Slider.values()[i];
                draggingSlider = slider;
                updateSlider(slider, mouseX);
                return true;
            }
        }

        int y = contentY + 8 + 3 * SETTING_STRIDE;
        if (inside(mouseX, mouseY, contentX, y, contentWidth, 40)) {
            Settings.CrosshairStyle[] styles = Settings.CrosshairStyle.values();
            Settings.crosshairStyle = styles[(Settings.crosshairStyle.ordinal() + 1) % styles.length];
            ConfigManager.save();
            return true;
        }
        return true;
    }

    private void updateSlider(Slider slider, double mouseX) {
        int trackX = contentX + 14;
        int trackWidth = contentWidth - 28;
        float t = (float) Math.max(0.0, Math.min(1.0, (mouseX - trackX) / trackWidth));
        switch (slider) {
            case HUD_SCALE -> Settings.hudScale = 0.75f + t * 0.75f;
            case PANEL_OPACITY -> Settings.panelOpacity = 0.3f + t * 0.7f;
            case CROSSHAIR_SIZE -> Settings.crosshairSize = 0.5f + t * 1.5f;
        }
    }

    private static float normalize(float value, float min, float max) {
        return Math.max(0f, Math.min(1f, (value - min) / (max - min)));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingSlider != null) {
            updateSlider(draggingSlider, mouseX);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingSlider != null) {
            draggingSlider = null;
            ConfigManager.save();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (tab != Tab.SETTINGS
                && inside(mouseX, mouseY, contentX, contentY, contentWidth, contentHeight)) {
            scroll -= verticalAmount * 24;
            clampScroll(modulesForTab().size() * ROW_STRIDE);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void close() {
        ConfigManager.save();
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
