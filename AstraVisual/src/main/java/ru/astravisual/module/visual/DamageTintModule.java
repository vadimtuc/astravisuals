package ru.astravisual.module.visual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import ru.astravisual.util.ColorUtil;

public final class DamageTintModule extends VisualModule {

    private static final int RED = 0xFFB1121A;
    private static final int EDGE = 52;

    public DamageTintModule() {
        super("Damage Tint", "Red screen edges on damage and low health");
    }

    @Override
    public void renderOverlay(DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }
        float strength = 0.0f;
        if (client.player.hurtTime > 0) {
            strength = client.player.hurtTime / 10.0f * 0.55f;
        }
        double max = Math.max(1.0, client.player.getMaxHealth());
        double ratio = client.player.getHealth() / max;
        if (ratio < 0.35) {
            double pulse = (Math.sin(System.currentTimeMillis() / 320.0) + 1.0) / 2.0;
            float low = (float) ((0.35 - ratio) / 0.35) * (float) (0.16 + 0.14 * pulse);
            strength = Math.max(strength, low);
        }
        if (strength <= 0.01f) {
            return;
        }

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        int solid = ColorUtil.withAlpha(RED, strength);
        int clear = ColorUtil.withAlpha(RED, 0.0f);

        ctx.fillGradient(0, 0, width, EDGE, solid, clear);
        ctx.fillGradient(0, height - EDGE, width, height, clear, solid);
        for (int i = 0; i < EDGE; i++) {
            int color = ColorUtil.withAlpha(RED, strength * (1.0f - i / (float) EDGE));
            ctx.fill(i, 0, i + 1, height, color);
            ctx.fill(width - 1 - i, 0, width - i, height, color);
        }
    }
}
