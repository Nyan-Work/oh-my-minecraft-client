package com.plusls.ommc.mixin.generic.dontClearChatHistory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.plusls.ommc.game.Configs;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC <= 11802
//$$ import net.minecraft.client.GuiMessage;
//#endif

//#if MC > 11502
import net.minecraft.util.FormattedCharSequence;
//#else
//$$ import net.minecraft.network.chat.Component;
//#endif

import java.util.List;

// From https://www.curseforge.com/minecraft/mc-mods/dont-clear-chat-history
@Mixin(ChatComponent.class)
public class MixinChatHud {
    @Inject(
            method = "clearMessages",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;clear()V",
                    ordinal = 2
            ), cancellable = true
    )
    private void dontClearChatHistory(boolean clearHistory, CallbackInfo ci) {
        if (Configs.dontClearChatHistory.getBooleanValue()) {
            ci.cancel();
        }
    }

    @WrapOperation(
            //#if MC > 11802 && MC < 12005
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
            //#elseif MC >= 12005
            //$$ method = "addMessageToDisplayQueue",
            //#else
            //$$ method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V",
            //#endif
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;size()I",
                    //#if MC > 11802
                    ordinal = 1
                    //#else
                    //$$ ordinal = 0
                    //#endif
            )
    )
    private int modifySize(
            //#if MC > 11802
            List<FormattedCharSequence> list,
            //#elseif MC > 11502
            //$$ List<GuiMessage<FormattedCharSequence>> list,
            //#else
            //$$ List<Component> list,
            //#endif
            Operation<Integer> original
    ) {
        if (Configs.dontClearChatHistory.getBooleanValue()) {
            return 1;
        }

        return original.call(list);
    }
}
