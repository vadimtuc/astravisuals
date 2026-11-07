package ru.astravisual.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;

/**
 * Keeps track of hit combos and the last entity the player attacked.
 */
public final class CombatTracker {

    private static final long TARGET_TIMEOUT_MS = 3000L;
    private static final long COMBO_TIMEOUT_MS = 2500L;

    private static int combo;
    private static long lastHitMs;
    private static LivingEntity lastTarget;

    private CombatTracker() {
    }

    public static void onAttack(LivingEntity target) {
        combo++;
        lastHitMs = System.currentTimeMillis();
        lastTarget = target;
    }

    public static void tick(MinecraftClient client) {
        if (client.player == null) {
            reset();
            return;
        }
        if (combo > 0 && System.currentTimeMillis() - lastHitMs > COMBO_TIMEOUT_MS) {
            combo = 0;
        }
        if (client.player.hurtTime == 10) {
            combo = 0;
        }
        if (lastTarget != null && !lastTarget.isAlive()) {
            lastTarget = null;
        }
    }

    public static int getCombo() {
        return combo;
    }

    public static LivingEntity getLastTarget() {
        if (lastTarget == null || System.currentTimeMillis() - lastHitMs > TARGET_TIMEOUT_MS) {
            return null;
        }
        return lastTarget.isAlive() ? lastTarget : null;
    }

    public static void reset() {
        combo = 0;
        lastTarget = null;
    }
}
