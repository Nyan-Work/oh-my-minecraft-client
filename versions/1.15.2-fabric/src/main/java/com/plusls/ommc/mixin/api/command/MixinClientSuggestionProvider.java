package com.plusls.ommc.mixin.api.command;

import com.plusls.ommc.api.command.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * The implementation for mc [1.14.4, ~)
 * <p>
 * Code from <a href="https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/mixin/command/client/ClientCommandSourceMixin.java">Fabric Command API v1</a>
 */
@Mixin(ClientSuggestionProvider.class)
public abstract class MixinClientSuggestionProvider implements FabricClientCommandSource {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Override
    public void sendFeedback(Component message) {
        LocalPlayer localPlayer = minecraft.player;
        if (localPlayer != null) {
            localPlayer.displayClientMessage(message, false);
        }
    }

    @Override
    public void sendError(Component message) {
        LocalPlayer localPlayer = minecraft.player;
        if (localPlayer != null) {
            localPlayer.displayClientMessage(message.copy().withStyle(ChatFormatting.RED), false);
        }
    }

    @Override
    public Minecraft getClient() {
        return minecraft;
    }

    @Override
    public LocalPlayer getPlayer() {
        return minecraft.player;
    }

    @Override
    public ClientLevel getWorld() {
        return minecraft.level;
    }
}