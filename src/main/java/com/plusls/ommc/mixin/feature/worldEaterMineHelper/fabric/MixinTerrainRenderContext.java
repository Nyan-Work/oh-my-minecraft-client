package com.plusls.ommc.mixin.feature.worldEaterMineHelper.fabric;

import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//#if MC > 11903
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
//#else
//$$ import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$
//#if MC > 11701
//$$ import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
//#else
//$$ import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainBlockRenderInfo;
//#endif
//#endif

//#if MC > 11802
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#endif

//#if MC > 11701
//#else
//$$ import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainBlockRenderInfo;
//#endif

//#if MC > 11404
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = TerrainRenderContext.class, remap = false)
public abstract class MixinTerrainRenderContext
        //#if MC > 11903
        extends AbstractBlockRenderContext
        //#else
        //$$ implements RenderContext
        //#endif
{
    //#if MC < 11904
    //$$ @Shadow
    //$$ @Final
    //#if MC > 11701
    //$$ private BlockRenderInfo blockInfo;
    //#else
    //$$ private TerrainBlockRenderInfo blockInfo;
    //#endif
    //#endif

    @Dynamic
    @Inject(
            method = {
                    "tessellateBlock", // For fabric-renderer-indigo 0.5.0 and above
                    "tesselateBlock" // For fabric-renderer-indigo 0.5.0 below
            },
            at = @At(value = "INVOKE",
                    //#if MC > 11903
                    target = "Lnet/minecraft/client/resources/model/BakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V",
                    //#else
                    //$$ target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V",
                    //#endif
                    shift = At.Shift.AFTER, ordinal = 0, remap = true))
    private void emitCustomBlockQuads(BlockState blockState, BlockPos blockPos, BakedModel model,
                                      //#if MC > 11404
                                      PoseStack matrixStack,
                                      //#endif
                                      //#if MC > 11802
                                      CallbackInfo ci) {
        //#else
        //$$ CallbackInfoReturnable<Boolean> cir) {
        //#endif
        WorldEaterMineHelper.emitCustomBlockQuads(blockInfo.blockView, blockInfo.blockState, blockInfo.blockPos, blockInfo.randomSupplier, this);
    }
}