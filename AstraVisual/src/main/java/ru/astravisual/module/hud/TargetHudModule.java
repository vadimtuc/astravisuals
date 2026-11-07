package ru.astravisual.module.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import ru.astravisual.util.ColorUtil;
import ru.astravisual.util.CombatTracker;
import ru.astravisual.util.RenderUtil;
import ru.astravisual.util.Settings;

public final class TargetHudModule extends HudModule {

    private static final int WIDTH = 132;

    public TargetHudModule() {
        super("Target HUD", "Health of the entity you are looking at");
        setPosition(Anchor.CENTER, 0, 56);
    }

    private LivingEntity target() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return null;
        }
        if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            if (((EntityHitResult) client.crosshairTarget).getEntity() instanceof LivingEntity living
                    && living != client.player) {
                return living;
            }
        }
        LivingEntity last = CombatTracker.getLastTarget();
        if (last != null && last != client.player && client.player.distanceTo(last) <= 8.0) {
            return last;
        }
        return null;
    }

    @Override
    public int width() {
        return WIDTH;
    }

    @Override
    public int height() {
        return target() == null ? 30 : 38;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        LivingEntity target = target();
        if (target == null) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }

        int width = width();
        int height = height();
        RenderUtil.panel(ctx, x, y, width, height);

        String name = target.getName().getString();
        String distance = String.format("%.1fm", client.player.distanceTo(target));
        int distanceWidth = RenderUtil.textWidth(distance);
        RenderUtil.text(ctx,
                RenderUtil.trim(name, width - 14 - distanceWidth - 10),
                x + 8,
                y + 5,
                target instanceof PlayerEntity ? Settings.TEXT : Settings.MUTED);
        RenderUtil.text(ctx, distance, x + width - 8 - distanceWidth, y + 5, Settings.MUTED);

        double max = Math.max(1.0, target.getMaxHealth());
        double ratio = Math.max(0.0, Math.min(1.0, target.getHealth() / max));
        int barX = x + 7;
        int barY = y + 20;
        int barWidth = width - 14;
        RenderUtil.rect(ctx, barX, barY, barWidth, 5, 0xFF1F1F2A);
        RenderUtil.rect(ctx, barX, barY, Math.max(0, (int) Math.round(barWidth * ratio)), 5,
                ColorUtil.forRatio(ratio));

        String health = String.format("%.1f HP", target.getHealth());
        RenderUtil.text(ctx, health, x + 8, y + 27, Settings.TEXT);
        float absorption = target.getAbsorptionAmount();
        if (absorption > 0) {
            String absorptionText = String.format("+%.1f", absorption);
            RenderUtil.text(ctx, absorptionText, x + 8 + RenderUtil.textWidth(health) + 6, y + 27, Settings.WARN);
        }
        String maxHealth = "/ " + (int) max;
        RenderUtil.textRight(ctx, maxHealth, x + width - 8, y + 27, Settings.MUTED);
    }
}
