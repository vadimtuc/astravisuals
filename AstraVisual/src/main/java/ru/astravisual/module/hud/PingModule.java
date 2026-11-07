package ru.astravisual.module.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import ru.astravisual.util.RenderUtil;
import ru.astravisual.util.Settings;

public final class PingModule extends HudModule {

    public PingModule() {
        super("Ping", "Server latency in milliseconds");
        setPosition(Anchor.TOP_LEFT, 4, 42);
    }

    private int ping() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getNetworkHandler() == null) {
            return 0;
        }
        PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
        return entry == null ? 0 : entry.getLatency();
    }

    private String value() {
        return ping() + "ms";
    }

    @Override
    public int width() {
        return RenderUtil.textWidth("PING") + RenderUtil.textWidth(value()) + 18;
    }

    @Override
    public int height() {
        return 14;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        int ping = ping();
        int color = ping < 60 ? Settings.SUCCESS : ping < 130 ? Settings.WARN : Settings.DANGER;
        RenderUtil.panel(ctx, x, y, width(), height());
        RenderUtil.text(ctx, "PING", x + 8, y + 3, Settings.MUTED);
        RenderUtil.text(ctx, value(), x + 8 + RenderUtil.textWidth("PING") + 5, y + 3, color);
    }
}
