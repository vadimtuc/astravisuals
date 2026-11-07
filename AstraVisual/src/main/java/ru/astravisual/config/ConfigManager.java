package ru.astravisual.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.astravisual.module.Module;
import ru.astravisual.module.ModuleManager;
import ru.astravisual.module.hud.Anchor;
import ru.astravisual.module.hud.HudModule;
import ru.astravisual.util.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("AstraVisual/Config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("astravisual.json");
    private static final Path LEGACY_PATH = FabricLoader.getInstance().getConfigDir().resolve("visualpvp.json");

    private static boolean loaded;
    private static boolean loading;

    private ConfigManager() {
    }

    public static void load() {
        loading = true;
        try {
            if (!Files.exists(PATH) && Files.exists(LEGACY_PATH)) {
                Files.copy(LEGACY_PATH, PATH);
                LOGGER.info("Migrated config from {}", LEGACY_PATH.getFileName());
            }
            if (Files.exists(PATH)) {
                JsonObject root = JsonParser.parseString(Files.readString(PATH)).getAsJsonObject();

                if (root.has("modules") && root.get("modules").isJsonObject()) {
                    for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject("modules").entrySet()) {
                        Module module = ModuleManager.byId(entry.getKey());
                        if (module != null && entry.getValue().isJsonPrimitive()) {
                            module.setEnabled(entry.getValue().getAsBoolean());
                        }
                    }
                }

                if (root.has("hud") && root.get("hud").isJsonObject()) {
                    JsonObject hud = root.getAsJsonObject("hud");
                    for (HudModule module : ModuleManager.HUD_MODULES) {
                        if (!hud.has(module.getId()) || !hud.get(module.getId()).isJsonObject()) {
                            continue;
                        }
                        JsonObject data = hud.getAsJsonObject(module.getId());
                        if (data.has("anchor")) {
                            try {
                                module.setAnchor(Anchor.valueOf(data.get("anchor").getAsString()));
                            } catch (IllegalArgumentException ignored) {
                                // keep default anchor
                            }
                        }
                        if (data.has("x")) {
                            module.setOffsetX(data.get("x").getAsInt());
                        }
                        if (data.has("y")) {
                            module.setOffsetY(data.get("y").getAsInt());
                        }
                    }
                }

                if (root.has("settings") && root.get("settings").isJsonObject()) {
                    JsonObject settings = root.getAsJsonObject("settings");
                    if (settings.has("hudScale")) {
                        Settings.hudScale = clamp(settings.get("hudScale").getAsFloat(), 0.75f, 1.5f);
                    }
                    if (settings.has("panelOpacity")) {
                        Settings.panelOpacity = clamp(settings.get("panelOpacity").getAsFloat(), 0.3f, 1.0f);
                    }
                    if (settings.has("crosshairSize")) {
                        Settings.crosshairSize = clamp(settings.get("crosshairSize").getAsFloat(), 0.5f, 2.0f);
                    }
                    if (settings.has("crosshairStyle")) {
                        try {
                            Settings.crosshairStyle =
                                    Settings.CrosshairStyle.valueOf(settings.get("crosshairStyle").getAsString());
                        } catch (IllegalArgumentException ignored) {
                            // keep default style
                        }
                    }
                }
            }
        } catch (Exception exception) {
            LOGGER.warn("Failed to load AstraVisual config", exception);
        } finally {
            loading = false;
            loaded = true;
        }
    }

    public static void save() {
        if (!loaded || loading) {
            return;
        }
        try {
            JsonObject root = new JsonObject();

            JsonObject modules = new JsonObject();
            for (Module module : ModuleManager.MODULES) {
                modules.addProperty(module.getId(), module.isEnabled());
            }
            root.add("modules", modules);

            JsonObject hud = new JsonObject();
            for (HudModule module : ModuleManager.HUD_MODULES) {
                JsonObject data = new JsonObject();
                data.addProperty("anchor", module.getAnchor().name());
                data.addProperty("x", module.getOffsetX());
                data.addProperty("y", module.getOffsetY());
                hud.add(module.getId(), data);
            }
            root.add("hud", hud);

            JsonObject settings = new JsonObject();
            settings.addProperty("hudScale", Settings.hudScale);
            settings.addProperty("panelOpacity", Settings.panelOpacity);
            settings.addProperty("crosshairSize", Settings.crosshairSize);
            settings.addProperty("crosshairStyle", Settings.crosshairStyle.name());
            root.add("settings", settings);

            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(root));
        } catch (IOException exception) {
            LOGGER.error("Failed to save AstraVisual config", exception);
        }
    }

    public static void onModuleChanged() {
        if (loaded && !loading) {
            save();
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
