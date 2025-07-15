package com.plusls.ommc.mixin.api.command;

import com.plusls.ommc.api.command.ClientCommandInternals;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The implementation for mc [1.14.4, ~)
 * <p>
 * Code from <a href="https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/mixin/command/client/ClientPlayerEntityMixin.java">Fabric Command API v1</a>
 */
@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {
    @Inject(method = "chat", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        if (ClientCommandInternals.executeCommand(message)) {
            ci.cancel();
        }
    }
}
