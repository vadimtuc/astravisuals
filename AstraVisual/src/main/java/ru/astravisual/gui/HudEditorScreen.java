package ru.astravisual.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import ru.astravisual.config.ConfigManager;
import ru.astravisual.hud.HudManager;
import ru.astravisual.module.ModuleManager;
import ru.astravisual.module.hud.HudModule;
import ru.astravisual.util.ColorUtil;
import ru.astravisual.util.RenderUtil;
import ru.astravisual.util.Settings;

public final class HudEditorScreen extends Screen {

    private HudModule dragging;
    private double grabX;
    private double grabY;

    public HudEditorScreen() {
        super(Text.literal("HUD Editor"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, width, height, 0x66000000);

        HudManager.renderModules(context);

        for (HudModule module : ModuleManager.HUD_MODULES) {
            int[] bounds = HudManager.bounds(module, client);
            boolean hover = inside(mouseX, mouseY, bounds[0] - 4, bounds[1] - 4,
                    bounds[2] + 8, bounds[3] + 8);
            int color;
            if (!module.isEnabled()) {
                color = 0xFF4A4A58;
            } else if (dragging == module || hover) {
                color = 0xFFFFFFFF;
            } else {
                color = Settings.ACCENT;
            }
            RenderUtil.outline(context, bounds[0] - 1, bounds[1] - 1,
                    Math.max(2, bounds[2] + 2), Math.max(2, bounds[3] + 2), color);

            String label = module.getName() + (module.isEnabled() ? "" : " (off)");
            int labelY = bounds[1] - 10;
            if (labelY < 4) {
                labelY = bounds[1] + 2;
            }
            RenderUtil.text(context, label, bounds[0] + 1, labelY,
                    module.isEnabled() ? color : 0xFF9A9AA8);
        }

        String title = "HUD EDITOR";
        RenderUtil.textCenter(context, title, width / 2, 14, Settings.TEXT);
        String subtitle = "Drag elements · ESC — back to menu";
        RenderUtil.textCenter(context, subtitle, width / 2, 27, Settings.MUTED);

        drawResetButton(context, mouseX, mouseY);
    }

    private void drawResetButton(DrawContext context, int mouseX, int mouseY) {
        int buttonWidth = 110;
        int buttonHeight = 22;
        int x = width / 2 - buttonWidth / 2;
        int y = height - 34;
        boolean hover = inside(mouseX, mouseY, x, y, buttonWidth, buttonHeight);
        RenderUtil.roundedRect(context, x, y, buttonWidth, buttonHeight, 5,
                ColorUtil.withAlpha(Settings.ACCENT, hover ? 0.30f : 0.18f));
        RenderUtil.textCenter(context, "Reset positions", x + buttonWidth / 2, y + 7, Settings.ACCENT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int buttonWidth = 110;
            int buttonHeight = 22;
            int resetX = width / 2 - buttonWidth / 2;
            int resetY = height - 34;
            if (inside(mouseX, mouseY, resetX, resetY, buttonWidth, buttonHeight)) {
                ModuleManager.resetHudPositions();
                ConfigManager.save();
                return true;
            }

            for (int i = ModuleManager.HUD_MODULES.size() - 1; i >= 0; i--) {
                HudModule module = ModuleManager.HUD_MODULES.get(i);
                int[] bounds = HudManager.bounds(module, client);
                if (inside(mouseX, mouseY, bounds[0] - 3, bounds[1] - 3,
                        Math.max(6, bounds[2] + 6), Math.max(6, bounds[3] + 6))) {
                    dragging = module;
                    grabX = mouseX - bounds[0];
                    grabY = mouseY - bounds[1];
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging != null) {
            int[] bounds = HudManager.bounds(dragging, client);
            int maxX = Math.max(0, client.getWindow().getScaledWidth() - bounds[2]);
            int maxY = Math.max(0, client.getWindow().getScaledHeight() - bounds[3]);
            int absoluteX = (int) Math.round(Math.max(0, Math.min(maxX, mouseX - grabX)));
            int absoluteY = (int) Math.round(Math.max(0, Math.min(maxY, mouseY - grabY)));
            HudManager.moveAbsolute(dragging, absoluteX, absoluteY, client);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging != null) {
            dragging = null;
            ConfigManager.save();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        ConfigManager.save();
        if (client != null) {
            client.setScreen(new ClickGuiScreen());
        } else {
            super.close();
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
