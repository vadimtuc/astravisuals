package ru.astravisual.module.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Direction;
import ru.astravisual.util.RenderUtil;
import ru.astravisual.util.Settings;

public final class CoordsModule extends HudModule {

    public CoordsModule() {
        super("Coords", "Player position and facing direction");
        setPosition(Anchor.TOP_LEFT, 4, 60);
    }

    private String values() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return "0 0 0";
        }
        return client.player.getBlockX() + " " + client.player.getBlockY() + " " + client.player.getBlockZ();
    }

    private String facing() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return "N";
        }
        Direction direction = client.player.getHorizontalFacing();
        return switch (direction) {
            case NORTH -> "N";
            case SOUTH -> "S";
            case WEST -> "W";
            case EAST -> "E";
            default -> "?";
        };
    }

    @Override
    public int width() {
        return RenderUtil.textWidth("XYZ") + RenderUtil.textWidth(values()) + RenderUtil.textWidth(facing()) + 24;
    }

    @Override
    public int height() {
        return 14;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        RenderUtil.panel(ctx, x, y, width(), height());
        RenderUtil.text(ctx, "XYZ", x + 8, y + 3, Settings.MUTED);
        RenderUtil.text(ctx, values(), x + 8 + RenderUtil.textWidth("XYZ") + 5, y + 3, Settings.TEXT);
        RenderUtil.text(ctx,
                facing(),
                x + width() - RenderUtil.textWidth(facing()) - 8,
                y + 3,
                Settings.ACCENT);
    }
}
