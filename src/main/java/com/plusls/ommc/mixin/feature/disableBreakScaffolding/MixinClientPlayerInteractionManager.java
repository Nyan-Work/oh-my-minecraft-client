package com.plusls.ommc.mixin.feature.disableBreakScaffolding;

import com.plusls.ommc.game.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
    private void disableBreakScaffolding(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (this.ommc$shouldDisableBreakScaffolding(pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "continueDestroyBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disableBreakScaffolding1(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (this.ommc$shouldDisableBreakScaffolding(pos)) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean ommc$shouldDisableBreakScaffolding(BlockPos pos) {
        Level level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;

        if (
                Configs.disableBreakScaffolding.getBooleanValue() &&
                level != null &&
                //#if MC <= 11502
                //$$ level.getBlockState(pos).getBlock() == Blocks.SCAFFOLDING &&
                //#else
                level.getBlockState(pos).is(Blocks.SCAFFOLDING) &&
                //#endif
                player != null
        ) {
            //#if MC >= 11903
            String itemId = BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).toString();
            //#else
            //$$ String itemId = Registry.ITEM.getKey(player.getMainHandItem().getItem()).toString();
            //#endif
            String itemName = player.getMainHandItem().getItem().getDescription().getString();
            return Configs.breakScaffoldingWhiteList.getStrings().stream().noneMatch(s -> itemId.contains(s) || itemName.contains(s));
        }

        return false;
    }
}
