package com.plusls.ommc.mixin.feature.blockModelNoOffset.sodium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.plusls.ommc.impl.feature.blockModelNoOffset.BlockModelNoOffsetHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;

@Dependencies(require = @Dependency(value = "sodium", versionPredicates = "<0.0.0"))
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.render.renderer.TerrainRenderContext", remap = false)
public class MixinTerrainRenderContext {
    @Dynamic
    @Redirect(
            method = "renderBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getOffset(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 0,
                    remap = true
            )
    )
    private Vec3 blockModelNoOffset(BlockState blockState, BlockGetter world, BlockPos pos, Operation<Vec3> original) {
        if (BlockModelNoOffsetHelper.shouldNoOffset(blockState)) {
            return Vec3.ZERO;
        }

        return original.call(blockState, world, pos);
    }
}
