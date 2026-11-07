package ru.astravisual.util;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Tracks clicks for the CPS counter. Only in-world clicks are counted.
 */
public final class ClickCounter {

    private static final long WINDOW_MS = 1000L;
    private static final Deque<Long> LEFT = new ArrayDeque<>();
    private static final Deque<Long> RIGHT = new ArrayDeque<>();

    private ClickCounter() {
    }

    public static void onClick(int button) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null || client.player == null) {
            return;
        }
        Deque<Long> clicks = button == 0 ? LEFT : button == 1 ? RIGHT : null;
        if (clicks == null) {
            return;
        }
        clicks.addLast(System.currentTimeMillis());
    }

    public static int left() {
        return count(LEFT);
    }

    public static int right() {
        return count(RIGHT);
    }

    private static int count(Deque<Long> clicks) {
        long now = System.currentTimeMillis();
        while (!clicks.isEmpty() && now - clicks.peekFirst() > WINDOW_MS) {
            clicks.pollFirst();
        }
        return clicks.size();
    }
}
