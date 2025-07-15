package com.plusls.ommc.mixin.feature.worldEaterMineHelper.sodium;

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

@Dependencies(require = @Dependency(value = "sodium", versionPredicates = ">=0.6- <0.7-"))
@Pseudo
@Mixin(value = BlockRenderer.class, remap = false)
public abstract class MixinBlockRenderer_0_6 {
    @Shadow(remap = false)
    public abstract void renderModel(BakedModel model, BlockState state, BlockPos pos, BlockPos origin);

    @Unique
    private final ThreadLocal<Boolean> ommc$renderTag = ThreadLocal.withInitial(() -> false);

    @Dynamic
    @Inject(method = "renderModel", at = @At("RETURN"))
    private void postRenderModel(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        if (WorldEaterMineHelper.shouldUseCustomModel(state, pos) && !this.ommc$renderTag.get()) {
            BakedModel customModel = WorldEaterMineHelper.customModels.get(state.getBlock());

            if (customModel != null) {
                this.ommc$renderTag.set(true);
                int originalLightEmission = state.getLightEmission();
                ((AccessorBlockStateBase) state).setLightEmission(15);
                this.renderModel(customModel, state, pos, origin);
                ((AccessorBlockStateBase) state).setLightEmission(originalLightEmission);
                this.ommc$renderTag.set(false);
            }
        }
    }
}
