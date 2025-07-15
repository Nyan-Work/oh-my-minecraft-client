package com.plusls.ommc.mixin.feature.disableBreakBlock;

import com.plusls.ommc.game.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 11903
import net.minecraft.core.registries.BuiltInRegistries;
//#else
//$$ import net.minecraft.core.Registry;
//#endif

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager {
    @Inject(
            method = "startDestroyBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disableBreakBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (this.ommc$shouldDisableBreakBlock(pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "continueDestroyBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disableBreakBlock1(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (this.ommc$shouldDisableBreakBlock(pos)) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean ommc$shouldDisableBreakBlock(BlockPos pos) {
        Level level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;

        if (Configs.disableBreakBlock.getBooleanValue() && level != null && player != null) {
            //#if MC >= 11903
            String blockId = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString();
            //#else
            //$$ String blockId = Registry.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString();
            //#endif

            String blockName = level.getBlockState(pos).getBlock().getName().getString();
            return Configs.breakBlockBlackList.getStrings().stream().anyMatch(s -> blockId.contains(s) || blockName.contains(s));
        }

        return false;
    }
}
