package ru.astravisual.module.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import ru.astravisual.gui.HudEditorScreen;
import ru.astravisual.util.CombatTracker;
import ru.astravisual.util.RenderUtil;
import ru.astravisual.util.Settings;

public final class ComboModule extends HudModule {

    public ComboModule() {
        super("Combo", "Consecutive hit counter");
        setPosition(Anchor.BOTTOM_RIGHT, 4, 4);
    }

    private String number() {
        return "x" + CombatTracker.getCombo();
    }

    @Override
    public int width() {
        return RenderUtil.textWidth("COMBO") + RenderUtil.textWidth("x99") + 24;
    }

    @Override
    public int height() {
        return 16;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        int combo = CombatTracker.getCombo();
        MinecraftClient client = MinecraftClient.getInstance();
        boolean editing = client.currentScreen instanceof HudEditorScreen;
        if (combo <= 0 && !editing) {
            return;
        }
        RenderUtil.panel(ctx, x, y, width(), height());
        RenderUtil.text(ctx, "COMBO", x + 8, y + 4, Settings.MUTED);
        String number = number();
        RenderUtil.text(ctx,
                number,
                x + width() - RenderUtil.textWidth(number) - 8,
                y + 4,
                combo > 0 ? Settings.ACCENT : Settings.MUTED);
    }
}
