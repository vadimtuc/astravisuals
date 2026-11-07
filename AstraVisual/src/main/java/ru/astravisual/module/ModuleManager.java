package ru.astravisual.module;

import net.minecraft.client.MinecraftClient;
import ru.astravisual.module.hud.ArmorModule;
import ru.astravisual.module.hud.ComboModule;
import ru.astravisual.module.hud.CoordsModule;
import ru.astravisual.module.hud.CpsModule;
import ru.astravisual.module.hud.FpsModule;
import ru.astravisual.module.hud.HudModule;
import ru.astravisual.module.hud.KeystrokesModule;
import ru.astravisual.module.hud.PingModule;
import ru.astravisual.module.hud.PotionModule;
import ru.astravisual.module.hud.TargetHudModule;
import ru.astravisual.module.hud.WatermarkModule;
import ru.astravisual.module.visual.CrosshairModule;
import ru.astravisual.module.visual.DamageTintModule;
import ru.astravisual.module.visual.HitMarkerModule;
import ru.astravisual.module.visual.NoHurtCamModule;
import ru.astravisual.module.visual.VisualModule;
import ru.astravisual.util.CombatTracker;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {

    public static final List<Module> MODULES = new ArrayList<>();
    public static final List<HudModule> HUD_MODULES = new ArrayList<>();
    public static final List<VisualModule> VISUAL_MODULES = new ArrayList<>();

    public static WatermarkModule WATERMARK;
    public static FpsModule FPS;
    public static PingModule PING;
    public static CoordsModule COORDS;
    public static CpsModule CPS;
    public static KeystrokesModule KEYSTROKES;
    public static ArmorModule ARMOR;
    public static PotionModule POTIONS;
    public static TargetHudModule TARGET_HUD;
    public static ComboModule COMBO;

    public static DamageTintModule DAMAGE_TINT;
    public static HitMarkerModule HIT_MARKER;
    public static CrosshairModule CROSSHAIR;
    public static NoHurtCamModule NO_HURT_CAM;

    private ModuleManager() {
    }

    public static void init() {
        WATERMARK = add(new WatermarkModule());
        FPS = add(new FpsModule());
        PING = add(new PingModule());
        COORDS = add(new CoordsModule());
        CPS = add(new CpsModule());
        KEYSTROKES = add(new KeystrokesModule());
        ARMOR = add(new ArmorModule());
        POTIONS = add(new PotionModule());
        TARGET_HUD = add(new TargetHudModule());
        COMBO = add(new ComboModule());

        DAMAGE_TINT = add(new DamageTintModule());
        HIT_MARKER = add(new HitMarkerModule());
        CROSSHAIR = add(new CrosshairModule());
        NO_HURT_CAM = add(new NoHurtCamModule());

        WATERMARK.setEnabled(true);
        FPS.setEnabled(true);
        PING.setEnabled(true);
        COORDS.setEnabled(true);
        CPS.setEnabled(true);
        KEYSTROKES.setEnabled(true);
        ARMOR.setEnabled(true);
        POTIONS.setEnabled(true);
        TARGET_HUD.setEnabled(true);
        COMBO.setEnabled(true);

        DAMAGE_TINT.setEnabled(true);
        HIT_MARKER.setEnabled(true);
        CROSSHAIR.setEnabled(true);
        NO_HURT_CAM.setEnabled(false);
    }

    private static <T extends Module> T add(T module) {
        MODULES.add(module);
        if (module instanceof HudModule hud) {
            HUD_MODULES.add(hud);
        }
        if (module instanceof VisualModule visual) {
            VISUAL_MODULES.add(visual);
        }
        return module;
    }

    public static void tick(MinecraftClient client) {
        CombatTracker.tick(client);
        for (Module module : MODULES) {
            if (module.isEnabled()) {
                module.onTick(client);
            }
        }
    }

    public static Module byId(String id) {
        for (Module module : MODULES) {
            if (module.getId().equals(id)) {
                return module;
            }
        }
        return null;
    }

    public static void resetHudPositions() {
        for (HudModule module : HUD_MODULES) {
            module.resetPosition();
        }
    }
}
