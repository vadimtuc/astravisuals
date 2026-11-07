package ru.astravisual;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.astravisual.config.ConfigManager;
import ru.astravisual.gui.ClickGuiScreen;
import ru.astravisual.hud.HudManager;
import ru.astravisual.module.ModuleManager;

public final class AstraVisualClient implements ClientModInitializer {

    public static final String MOD_ID = "astravisual";
    public static final String NAME = "AstraVisual";
    public static final String VERSION = "v1.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    private static KeyBinding menuKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        ConfigManager.load();

        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.astravisual.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.astravisual"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ClickGuiScreen());
                }
            }
            ModuleManager.tick(client);
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> HudManager.render(context));

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save());

        LOGGER.info("{} {} initialized", NAME, VERSION);
    }
}
