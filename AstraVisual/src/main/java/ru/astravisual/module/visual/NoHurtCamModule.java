package ru.astravisual.module.visual;

import net.minecraft.client.gui.DrawContext;

/**
 * Camera tilt on damage is cancelled in {@code GameRendererMixin}.
 */
public final class NoHurtCamModule extends VisualModule {

    public NoHurtCamModule() {
        super("No Hurt Cam", "Removes the camera tilt when taking damage");
    }

    @Override
    public void renderOverlay(DrawContext ctx) {
    }
}
