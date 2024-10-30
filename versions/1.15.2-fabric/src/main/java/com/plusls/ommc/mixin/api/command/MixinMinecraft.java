package com.plusls.ommc.mixin.api.command;

import com.plusls.ommc.api.command.ClientCommandInternals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The implementation for mc [1.14.4, ~)
 * <p>
 * Code from <a href="https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/mixin/command/client/MinecraftClientMixin.java">Fabric Command API v1</a>
 */
@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(GameConfig gameConfig, CallbackInfo ci) {
        ClientCommandInternals.finalizeInit();
    }
}
