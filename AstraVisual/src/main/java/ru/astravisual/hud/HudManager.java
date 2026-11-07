package ru.astravisual.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import ru.astravisual.module.ModuleManager;
import ru.astravisual.module.hud.Anchor;
import ru.astravisual.module.hud.HudModule;
import ru.astravisual.util.Settings;

public final class HudManager {

    private HudManager() {
    }

    public static void render(DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }
        if (client.options.hudHidden) {
            return;
        }

        if (ModuleManager.DAMAGE_TINT.isEnabled()) {
            ModuleManager.DAMAGE_TINT.renderOverlay(ctx);
        }
        if (ModuleManager.CROSSHAIR.isEnabled()) {
            ModuleManager.CROSSHAIR.renderOverlay(ctx);
        }

        renderModules(ctx);

        if (ModuleManager.HIT_MARKER.isEnabled()) {
            ModuleManager.HIT_MARKER.renderOverlay(ctx);
        }
    }

    /** Renders only the HUD widgets (used by the editor to draw a crisp preview). */
    public static void renderModules(DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) {
            return;
        }
        for (HudModule module : ModuleManager.HUD_MODULES) {
            if (!module.isEnabled()) {
                continue;
            }
            int[] bounds = bounds(module, client);
            if (bounds[2] <= 0 || bounds[3] <= 0) {
                continue;
            }
            MatrixStack matrices = ctx.getMatrices();
            matrices.push();
            matrices.translate(bounds[0], bounds[1], 0.0);
            matrices.scale(Settings.hudScale, Settings.hudScale, 1.0f);
            module.render(ctx, 0, 0);
            matrices.pop();
        }
    }

    /** Returns {x, y, width, height} in scaled screen coordinates. */
    public static int[] bounds(HudModule module, MinecraftClient client) {
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        float scale = Settings.hudScale;
        int width = Math.max(0, Math.round(module.width() * scale));
        int height = Math.max(0, Math.round(module.height() * scale));
        int x;
        int y;
        switch (module.getAnchor()) {
            case TOP_RIGHT -> {
                x = screenWidth - width - module.getOffsetX();
                y = module.getOffsetY();
            }
            case BOTTOM_LEFT -> {
                x = module.getOffsetX();
                y = screenHeight - height - module.getOffsetY();
            }
            case BOTTOM_RIGHT -> {
                x = screenWidth - width - module.getOffsetX();
                y = screenHeight - height - module.getOffsetY();
            }
            case CENTER -> {
                x = (screenWidth - width) / 2 + module.getOffsetX();
                y = (screenHeight - height) / 2 + module.getOffsetY();
            }
            default -> {
                x = module.getOffsetX();
                y = module.getOffsetY();
            }
        }
        return new int[]{x, y, width, height};
    }

    /** Moves an element so its absolute top-left corner is at the given position. */
    public static void moveAbsolute(HudModule module, int absoluteX, int absoluteY, MinecraftClient client) {
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        float scale = Settings.hudScale;
        int width = Math.max(0, Math.round(module.width() * scale));
        int height = Math.max(0, Math.round(module.height() * scale));
        switch (module.getAnchor()) {
            case TOP_RIGHT -> {
                module.setOffsetX(screenWidth - width - absoluteX);
                module.setOffsetY(absoluteY);
            }
            case BOTTOM_LEFT -> {
                module.setOffsetX(absoluteX);
                module.setOffsetY(screenHeight - height - absoluteY);
            }
            case BOTTOM_RIGHT -> {
                module.setOffsetX(screenWidth - width - absoluteX);
                module.setOffsetY(screenHeight - height - absoluteY);
            }
            case CENTER -> {
                module.setOffsetX(absoluteX - (screenWidth - width) / 2);
                module.setOffsetY(absoluteY - (screenHeight - height) / 2);
            }
            default -> {
                module.setOffsetX(absoluteX);
                module.setOffsetY(absoluteY);
            }
        }
    }
}
