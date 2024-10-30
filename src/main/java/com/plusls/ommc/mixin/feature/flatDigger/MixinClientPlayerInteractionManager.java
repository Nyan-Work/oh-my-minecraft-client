package com.plusls.ommc.mixin.feature.flatDigger;

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
import top.hendrixshen.magiclib.api.compat.minecraft.world.entity.player.PlayerCompat;

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager {
    @Inject(
            method = "startDestroyBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void flatDigger(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (this.ommc$shouldFlatDigger(pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "continueDestroyBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void flatDigger1(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (this.ommc$shouldFlatDigger(pos)) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean ommc$shouldFlatDigger(BlockPos pos) {
        Level level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;

        if (Configs.flatDigger.getBooleanValue() && level != null && player != null) {
            return !player.isShiftKeyDown() && pos.getY() < PlayerCompat.of(player).getBlockPosition().getY();
        }

        return false;
    }
}
