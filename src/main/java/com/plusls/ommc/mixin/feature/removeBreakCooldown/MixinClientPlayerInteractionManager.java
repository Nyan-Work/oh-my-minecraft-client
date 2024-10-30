package com.plusls.ommc.mixin.feature.removeBreakCooldown;

import com.plusls.ommc.game.Configs;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager {
    @Shadow
    private int destroyDelay;

    @Inject(
            method = "continueDestroyBlock",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyDelay:I",
                    opcode = Opcodes.PUTFIELD,
                    ordinal = 2,
                    shift = At.Shift.AFTER
            )
    )
    private void removeBreakingCooldown(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.removeBreakingCooldown.getBooleanValue() && !Configs.forceBreakingCooldown.getBooleanValue()) {
            destroyDelay = 0;
        }
    }
}