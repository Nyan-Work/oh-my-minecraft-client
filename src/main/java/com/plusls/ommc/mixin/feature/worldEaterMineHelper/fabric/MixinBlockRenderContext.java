package com.plusls.ommc.mixin.feature.worldEaterMineHelper.fabric;

import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//#if MC > 11903
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
//#else
//$$ import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
//$$ import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Shadow;
//#endif

//#if MC > 11802
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//$$ import java.util.Random;
//#endif

//#if MC > 11404
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
//#else
//$$ import com.mojang.blaze3d.vertex.BufferBuilder;
//$$ import net.minecraft.client.renderer.block.ModelBlockRenderer;
//#endif

@Mixin(value = BlockRenderContext.class, remap = false)
//#if MC > 11903
public abstract class MixinBlockRenderContext extends AbstractBlockRenderContext {
//#else
//$$ public abstract class MixinBlockRenderContext implements RenderContext {
//$$     @Shadow
//$$     @Final
//$$     private BlockRenderInfo blockInfo;
//#endif
    @Inject(
            //#if MC > 11404
            method = "render",
            //#else
            //$$ method = "tesselate",
            //#endif
            at = @At(value = "INVOKE",
                    //#if MC > 11903
                    target = "Lnet/minecraft/client/resources/model/BakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V",
                    //#else
                    //$$ target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V",
                    //#endif
                    shift = At.Shift.AFTER, ordinal = 0, remap = true))
    private void emitCustomBlockQuads(
            //#if MC <= 11404
            //$$ ModelBlockRenderer vanillaRenderer,
            //#endif
            BlockAndTintGetter blockView,
            BakedModel model,
            BlockState state, BlockPos pos,
            //#if MC > 11404
            PoseStack matrixStack,
            VertexConsumer buffer,
            //#else
            //$$ BufferBuilder buffer,
            //#endif
            //#if MC > 11701
            boolean cull,
            //#endif
            //#if MC > 11802
            RandomSource random,
            //#elseif MC > 11404
            //$$ Random random,
            //#endif
            long seed,
            //#if MC > 11404
            int overlay,
            //#endif
            //#if MC > 11802
            CallbackInfo cir) {
            //#else
            //$$ CallbackInfoReturnable<Boolean> cir) {
            //#endif
        WorldEaterMineHelper.emitCustomBlockQuads(blockView, state, pos, this.blockInfo.randomSupplier, this);
    }
}
