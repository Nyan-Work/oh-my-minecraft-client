package com.plusls.ommc.mixin.feature.preventIntentionalGameDesign;

import com.plusls.ommc.game.Configs;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC > 11502
import net.minecraft.world.level.block.RespawnAnchorBlock;
//#endif

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager {
    @Inject(
            method = "useItemOn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventIntentionalGameDesign(
            LocalPlayer player,
            //#if MC <= 11802
            //$$ ClientLevel level,
            //#endif
            InteractionHand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        //#if MC > 11802
        ClientLevel level = (ClientLevel) player.level();
        //#endif

        if (!Configs.preventIntentionalGameDesign.getBooleanValue()) {
            return;
        }

        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);
        if ((blockState.getBlock() instanceof BedBlock &&
                //#if MC > 11502
                !level.dimensionType().bedWorks()) ||
                (blockState.getBlock() instanceof RespawnAnchorBlock && !level.dimensionType().respawnAnchorWorks())
            //#else
            //$$ !level.getDimension().mayRespawn())
            //#endif
        ) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
