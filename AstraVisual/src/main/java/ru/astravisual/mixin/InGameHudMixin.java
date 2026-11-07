package ru.astravisual.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.astravisual.module.ModuleManager;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void astravisual$hideVanillaCrosshair(DrawContext context, RenderTickCounter tickCounter,
                                                CallbackInfo ci) {
        if (ModuleManager.CROSSHAIR != null && ModuleManager.CROSSHAIR.isEnabled()) {
            ci.cancel();
        }
    }
}
