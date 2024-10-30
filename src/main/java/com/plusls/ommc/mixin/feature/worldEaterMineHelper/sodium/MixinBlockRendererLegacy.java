package com.plusls.ommc.mixin.feature.worldEaterMineHelper.sodium;

import com.plusls.ommc.impl.feature.worldEaterMineHelper.BlockModelRendererContext;
import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;
import com.plusls.ommc.mixin.accessor.AccessorBlockStateBase;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;

@Dependencies(require = @Dependency(value = "sodium", versionPredicates = "<0.4.9"))
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer", remap = false)
public class MixinBlockRendererLegacy {
    @Unique
    private final ThreadLocal<BlockModelRendererContext> ommc$renderContext = ThreadLocal.withInitial(BlockModelRendererContext::new);
    @Unique
    private final ThreadLocal<Integer> ommc$originalLuminance = ThreadLocal.withInitial(() -> -1);

    @Dynamic
    @Inject(method = "renderModel", at = @At(value = "HEAD"))
    private void initRenderContext(
            BlockAndTintGetter world,
            BlockState state,
            BlockPos pos,
            //#if MC > 11605
            BlockPos origin,
            //#endif
            BakedModel model,
            @Coerce Object buffers,
            boolean cull,
            long seed,
            CallbackInfoReturnable<Boolean> cir
    ) {
        BlockModelRendererContext context = this.ommc$renderContext.get();
        context.pos = pos;
        context.state = state;
    }

    @Dynamic
    @ModifyVariable(
            method = "renderModel",
            at = @At("HEAD"),
            ordinal = 0
    )
    private BakedModel modifyBakedModel(BakedModel bakedModel) {
        BlockModelRendererContext context = this.ommc$renderContext.get();

        if (WorldEaterMineHelper.shouldUseCustomModel(context.state, context.pos)) {
            BakedModel customModel = WorldEaterMineHelper.customFullModels.get(context.state.getBlock());

            if (customModel != null) {
                this.ommc$originalLuminance.set(((AccessorBlockStateBase) context.state).getLightEmission());
                ((AccessorBlockStateBase) context.state).setLightEmission(15);
                return customModel;
            }
        }

        return bakedModel;
    }

    @Dynamic
    @Inject(method = "renderModel", at = @At("RETURN"))
    private void postRenderModel(
            BlockAndTintGetter world,
            BlockState state,
            BlockPos pos,
            //#if MC > 11605
            BlockPos origin,
            //#endif
            BakedModel model,
            @Coerce Object buffers,
            boolean cull,
            long seed,
            CallbackInfoReturnable<Boolean> cir
    ) {
        int originalLuminance = ommc$originalLuminance.get();

        if (originalLuminance != -1) {
            ((AccessorBlockStateBase) state).setLightEmission(originalLuminance);
            ommc$originalLuminance.set(-1);
        }
    }
}
