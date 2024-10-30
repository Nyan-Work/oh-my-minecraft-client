package com.plusls.ommc.mixin.feature.forceBreakingCooldown;

import com.plusls.ommc.game.Configs;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC > 11802
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.level.block.state.BlockState;
//#endif

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager {
    @Shadow
    private int destroyDelay;

    //#if MC > 11802
    @Inject(
            method = "method_41930",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyBlock(Lnet/minecraft/core/BlockPos;)Z",
                    remap = true,
                    ordinal = 0
            ),
            remap = false
    )
    private void addBreakingCooldown(BlockState blockState, BlockPos blockPos, Direction direction, int i, CallbackInfoReturnable<Packet<ServerGamePacketListener>> cir) {
        if (Configs.forceBreakingCooldown.getBooleanValue()) {
            this.destroyDelay = 5;
        }
    }
    //#else
    //$$ @Inject(
    //$$         method = "startDestroyBlock",
    //$$         at = @At(
    //$$                 value = "INVOKE",
    //$$                 target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyBlock(Lnet/minecraft/core/BlockPos;)Z",
    //$$                 //#if MC > 11502
    //$$                 ordinal = 1
    //$$                 //#else
    //$$                 //$$ ordinal = 0
    //$$                 //#endif
    //$$         )
    //$$ )
    //$$ private void addBreakingCooldown(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
    //$$     if (Configs.forceBreakingCooldown.getBooleanValue()) {
    //$$         this.destroyDelay = 5;
    //$$     }
    //$$ }
    //#endif
}