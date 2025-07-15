package com.plusls.ommc.mixin.feature.disableBlocklistCheck;

import com.plusls.ommc.game.Configs;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerSocialManager.class)
public class MixinSocialInteractionsManager {
    @Inject(
            method = "isBlocked",
            at = @At("HEAD"),
            cancellable = true
    )
    public void disableBlocklistCheck(UUID uuid, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.disableBlocklistCheck.getBooleanValue()) {
            cir.setReturnValue(false);
        }
    }
}
