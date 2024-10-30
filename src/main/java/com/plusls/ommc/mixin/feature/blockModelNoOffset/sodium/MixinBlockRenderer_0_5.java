package com.plusls.ommc.mixin.feature.blockModelNoOffset.sodium;

import com.plusls.ommc.impl.feature.blockModelNoOffset.BlockModelNoOffsetHelper;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Dependencies(require = @Dependency(value = "sodium", versionPredicates = ">=0.5- <0.6-"))
@Pseudo
@Mixin(value = BlockRenderer.class, remap = false)
public class MixinBlockRenderer_0_5 {
    @Dynamic
    @WrapOperation(
            method = "renderModel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;hasOffsetFunction()Z",
                    ordinal = 0,
                    remap = true
            ),
            remap = false
    )
    private boolean blockModelNoOffset(BlockState blockState, Operation<Boolean> original) {
        if (BlockModelNoOffsetHelper.shouldNoOffset(blockState)) {
            return false;
        }

        return original.call(blockState);
    }
}
