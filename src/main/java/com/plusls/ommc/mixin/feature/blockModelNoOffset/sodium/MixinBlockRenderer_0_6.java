package com.plusls.ommc.mixin.feature.blockModelNoOffset.sodium;

import com.plusls.ommc.impl.feature.blockModelNoOffset.BlockModelNoOffsetHelper;
import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;
import com.plusls.ommc.mixin.accessor.AccessorBlockStateBase;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Dependencies(require = @Dependency(value = "sodium", versionPredicates = ">=0.6- <0.7-"))
@Pseudo
@Mixin(value = BlockRenderer.class, remap = false)
public abstract class MixinBlockRenderer_0_6 {
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
