package com.plusls.ommc.mixin.feature.blockModelNoOffset.fabric;

import com.plusls.ommc.impl.feature.blockModelNoOffset.BlockModelNoOffsetHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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

//#if MC > 11404
import com.mojang.blaze3d.vertex.PoseStack;
//#else
//$$ import com.mojang.blaze3d.platform.GlStateManager;
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
            at = @At("HEAD")
    )
    private void blockModelNoOffset(
            BlockState blockState,
            BlockPos blockPos,
            BakedModel model,
            //#if MC > 11404
            PoseStack poseStack,
            //#endif
            //#if MC > 11802
            CallbackInfo ci
            //#else
            //$$ CallbackInfoReturnable<Boolean> cir
            //#endif
    ) {
        Vec3 offsetPos = blockState.getOffset(this.blockInfo.blockView, blockPos);

        if (BlockModelNoOffsetHelper.shouldNoOffset(blockState)) {
            //#if MC > 11404
            poseStack.translate(-offsetPos.x, -offsetPos.y, -offsetPos.z);
            //#else
            //$$ // will cause crash, i don't know why
            //$$ ////$$ GlStateManager.translated(-offsetPos.x, -offsetPos.y, -offsetPos.z);
            //#endif
        }
    }
}