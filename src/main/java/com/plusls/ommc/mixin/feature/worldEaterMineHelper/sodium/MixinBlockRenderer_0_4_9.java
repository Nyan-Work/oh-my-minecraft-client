package com.plusls.ommc.mixin.feature.worldEaterMineHelper.sodium;

import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;
import com.plusls.ommc.mixin.accessor.AccessorBlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

//#if MC == 11904
//$$ import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
//$$ import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderBounds;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

@Dependencies(require = @Dependency(value = "sodium", versionPredicates = ">=0.4.9- <0.4.11-"))
@Mixin(value = BlockRenderer.class, remap = false)
public abstract class MixinBlockRenderer_0_4_9 {
    @Shadow(remap = false)
    public abstract boolean renderModel(BlockRenderContext ctx, ChunkModelBuilder buffers);

    @Unique
    private final ThreadLocal<Boolean> ommc$renderTag = ThreadLocal.withInitial(() -> false);

    @Dynamic
    @Inject(method = "renderModel", at = @At("RETURN"))
    private void postRenderModel(@NotNull BlockRenderContext ctx, ChunkModelBuilder buffers, CallbackInfoReturnable<Boolean> cir) {
        if (WorldEaterMineHelper.shouldUseCustomModel(ctx.state(), ctx.pos()) && !this.ommc$renderTag.get()) {
            BakedModel customModel = WorldEaterMineHelper.customModels.get(ctx.state().getBlock());

            if (customModel != null) {
                this.ommc$renderTag.set(true);
                // This impl will break light systems, so disable it.
                // int originalLightEmission = ctx.state().getLightEmission();
                BakedModel originalModel = ctx.model();
                // ((AccessorBlockStateBase) ctx.state()).setLightEmission(15);
                ((AccessorBlockRenderContext) ctx).setModel(customModel);
                this.renderModel(ctx, buffers);
                ((AccessorBlockRenderContext) ctx).setModel(originalModel);
                // ((AccessorBlockStateBase) ctx.state()).setLightEmission(originalLightEmission);
                this.ommc$renderTag.set(false);
            }
        }
    }
}
