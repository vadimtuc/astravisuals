package ru.astravisual.module.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import ru.astravisual.util.ColorUtil;
import ru.astravisual.util.RenderUtil;
import ru.astravisual.util.Settings;

public final class KeystrokesModule extends HudModule {

    private static final int KEY_W = 24;
    private static final int KEY_H = 20;
    private static final int GAP = 3;
    private static final int WIDTH = KEY_W * 3 + GAP * 2;
    private static final int SPACE_H = 10;

    public KeystrokesModule() {
        super("Keystrokes", "WASD, mouse buttons and space");
        setPosition(Anchor.TOP_LEFT, 4, 98);
    }

    @Override
    public int width() {
        return WIDTH;
    }

    @Override
    public int height() {
        return KEY_H * 3 + GAP * 3 + SPACE_H;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        MinecraftClient client = MinecraftClient.getInstance();
        int row2 = y + KEY_H + GAP;
        int row3 = row2 + KEY_H + GAP;
        int spaceY = row3 + KEY_H + GAP;

        drawKey(ctx, x + KEY_W + GAP, y, KEY_W, KEY_H, "W", client.options.forwardKey.isPressed());
        drawKey(ctx, x, row2, KEY_W, KEY_H, "A", client.options.leftKey.isPressed());
        drawKey(ctx, x + KEY_W + GAP, row2, KEY_W, KEY_H, "S", client.options.backKey.isPressed());
        drawKey(ctx, x + (KEY_W + GAP) * 2, row2, KEY_W, KEY_H, "D", client.options.rightKey.isPressed());

        long handle = client.getWindow().getHandle();
        boolean left = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean right = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        drawKey(ctx, x, row3, 37, KEY_H, "LMB", left);
        drawKey(ctx, x + 40, row3, WIDTH - 40, KEY_H, "RMB", right);

        drawKey(ctx, x, spaceY, WIDTH, SPACE_H, "SPACE", client.options.jumpKey.isPressed());
    }

    private void drawKey(DrawContext ctx, int x, int y, int width, int height, String label, boolean pressed) {
        int background = pressed
                ? Settings.ACCENT
                : ColorUtil.withAlpha(0xFF1B1B26, 0.9f);
        int border = pressed
                ? Settings.ACCENT_2
                : ColorUtil.withAlpha(Settings.BORDER, 0.9f);
        RenderUtil.roundedRect(ctx, x, y, width, height, 3, background);
        RenderUtil.roundedRect(ctx, x, y, width, 1, 0, border);
        RenderUtil.roundedRect(ctx, x, y + height - 1, width, 1, 0, border);
        RenderUtil.rect(ctx, x, y, 1, height, border);
        RenderUtil.rect(ctx, x + width - 1, y, 1, height, border);
        RenderUtil.textCenter(ctx, label, x + width / 2, y + (height - 8) / 2, pressed ? 0xFF0B0B12 : Settings.TEXT);
    }
}
