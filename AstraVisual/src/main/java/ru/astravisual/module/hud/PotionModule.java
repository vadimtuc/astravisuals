package ru.astravisual.module.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import ru.astravisual.util.RenderUtil;
import ru.astravisual.util.Settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PotionModule extends HudModule {

    private static final int WIDTH = 124;
    private static final int MAX_EFFECTS = 8;

    public PotionModule() {
        super("Potions", "Active status effects and timers");
        setPosition(Anchor.TOP_RIGHT, 4, 4);
    }

    private List<StatusEffectInstance> effects() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return List.of();
        }
        List<StatusEffectInstance> list = new ArrayList<>(client.player.getStatusEffects());
        list.sort(Comparator.comparingInt(StatusEffectInstance::getDuration));
        if (list.size() > MAX_EFFECTS) {
            list = list.subList(0, MAX_EFFECTS);
        }
        return list;
    }

    @Override
    public int width() {
        return WIDTH;
    }

    @Override
    public int height() {
        int count = effects().size();
        return count == 0 ? 14 : 8 + count * 11;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        List<StatusEffectInstance> effects = effects();
        if (effects.isEmpty()) {
            RenderUtil.panel(ctx, x, y, width(), height());
            RenderUtil.text(ctx, "POTIONS", x + 8, y + 3, Settings.MUTED);
            RenderUtil.text(ctx, "—", x + width() - 12, y + 3, Settings.MUTED);
            return;
        }
        RenderUtil.panel(ctx, x, y, width(), height());
        for (int i = 0; i < effects.size(); i++) {
            StatusEffectInstance effect = effects.get(i);
            int rowY = y + 5 + i * 11;
            int color = effect.getEffectType().value().getColor();
            if ((color & 0xFFFFFF) == 0) {
                color = Settings.ACCENT;
            }
            RenderUtil.rect(ctx, x + 7, rowY + 2, 4, 4, 0xFF000000 | color);

            String duration = effect.isInfinite() ? "∞" : RenderUtil.formatDuration(effect.getDuration());
            int durationWidth = RenderUtil.textWidth(duration);
            String name = effect.getEffectType().value().getName().getString();
            if (effect.getAmplifier() > 0) {
                name += " " + roman(effect.getAmplifier() + 1);
            }
            int available = width() - 16 - durationWidth - 14;
            RenderUtil.text(ctx, RenderUtil.trim(name, available), x + 15, rowY, Settings.TEXT);
            RenderUtil.text(ctx, duration, x + width() - 8 - durationWidth, rowY, Settings.MUTED);
        }
    }

    private static String roman(int value) {
        return switch (value) {
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> Integer.toString(value);
        };
    }
}
