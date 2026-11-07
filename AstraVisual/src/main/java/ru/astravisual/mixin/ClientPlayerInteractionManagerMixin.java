package ru.astravisual.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.astravisual.module.ModuleManager;
import ru.astravisual.util.CombatTracker;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "attackEntity", at = @At("TAIL"))
    private void astravisual$attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (player != MinecraftClient.getInstance().player) {
            return;
        }
        if (target instanceof LivingEntity living) {
            CombatTracker.onAttack(living);
            if (ModuleManager.HIT_MARKER != null && ModuleManager.HIT_MARKER.isEnabled()) {
                ModuleManager.HIT_MARKER.onHit();
            }
        }
    }
}
