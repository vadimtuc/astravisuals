package ru.astravisual.module.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import ru.astravisual.util.ColorUtil;
import ru.astravisual.util.RenderUtil;
import ru.astravisual.util.Settings;

import java.util.ArrayList;
import java.util.List;

public final class ArmorModule extends HudModule {

    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET,
            EquipmentSlot.OFFHAND
    };
    private static final int SLOT_SIZE = 18;

    public ArmorModule() {
        super("Armor", "Armor durability and offhand item");
        setPosition(Anchor.TOP_LEFT, 4, 184);
    }

    private List<ItemStack> items() {
        MinecraftClient client = MinecraftClient.getInstance();
        List<ItemStack> result = new ArrayList<>();
        if (client.player == null) {
            return result;
        }
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = client.player.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                result.add(stack);
            }
        }
        return result;
    }

    @Override
    public int width() {
        int count = items().size();
        return count == 0 ? 54 : count * SLOT_SIZE + 4;
    }

    @Override
    public int height() {
        return items().isEmpty() ? 14 : 24;
    }

    @Override
    public void render(DrawContext ctx, int x, int y) {
        List<ItemStack> items = items();
        if (items.isEmpty()) {
            RenderUtil.panel(ctx, x, y, width(), height());
            RenderUtil.text(ctx, "ARMOR", x + 8, y + 3, Settings.MUTED);
            RenderUtil.text(ctx, "—", x + width() - 12, y + 3, Settings.MUTED);
            return;
        }
        RenderUtil.panel(ctx, x, y, width(), height());
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            int slotX = x + 3 + i * SLOT_SIZE;
            ctx.drawItem(stack, slotX, y + 3);
            if (stack.isDamageable()) {
                int max = stack.getMaxDamage();
                double ratio = max <= 0 ? 1.0 : 1.0 - (double) stack.getDamage() / max;
                int barColor = ColorUtil.forRatio(ratio);
                RenderUtil.rect(ctx, slotX, y + SLOT_SIZE + 1, 16, 2, 0xFF2A2A36);
                RenderUtil.rect(ctx, slotX, y + SLOT_SIZE + 1, Math.max(1, (int) Math.round(16 * ratio)), 2, barColor);
            }
        }
    }
}
