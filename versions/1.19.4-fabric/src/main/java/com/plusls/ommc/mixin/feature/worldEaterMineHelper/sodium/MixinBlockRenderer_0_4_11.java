package com.plusls.ommc.mixin.feature.worldEaterMineHelper.sodium;

import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;
import com.plusls.ommc.mixin.accessor.AccessorBlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderBounds;
import net.minecraft.client.resources.model.BakedModel;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;

/**
 * The implementation for mc [1.19.3, ~)
 */
@Dependencies(require = @Dependency(value = "sodium", versionPredicates = ">0.4.10 <0.5"))
@Mixin(value = BlockRenderer.class, remap = false)
public abstract class MixinBlockRenderer_0_4_11 {
    @Shadow(remap = false)
    public abstract void renderModel(BlockRenderContext ctx, ChunkBuildBuffers buffers, ChunkRenderBounds.Builder bounds);

    @Unique
    private final ThreadLocal<Boolean> ommc$renderTag = ThreadLocal.withInitial(() -> false);

    @Dynamic
    @Inject(
            method = "renderModel",
            at = @At(
                    value = "RETURN"
            )
    )
    private void postRenderModel(@NotNull BlockRenderContext ctx, ChunkBuildBuffers buffers, ChunkRenderBounds.Builder bounds, CallbackInfo ci) {
        if (WorldEaterMineHelper.shouldUseCustomModel(ctx.state(), ctx.pos()) && !this.ommc$renderTag.get()) {
            BakedModel customModel = WorldEaterMineHelper.customModels.get(ctx.state().getBlock());

            if (customModel != null) {
                this.ommc$renderTag.set(true);
                // This impl will break light systems, so disable it.
                // int originalLightEmission = ctx.state().getLightEmission();
                BakedModel originalModel = ctx.model();
                // ((AccessorBlockStateBase) ctx.state()).setLightEmission(15);
                ((AccessorBlockRenderContext) ctx).setModel(customModel);
                this.renderModel(ctx, buffers, bounds);
                ((AccessorBlockRenderContext) ctx).setModel(originalModel);
                // ((AccessorBlockStateBase) ctx.state()).setLightEmission(originalLightEmission);
                this.ommc$renderTag.set(false);
            }
        }
    }
}
