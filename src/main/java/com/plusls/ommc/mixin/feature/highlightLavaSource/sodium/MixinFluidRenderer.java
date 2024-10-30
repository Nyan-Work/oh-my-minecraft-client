package com.plusls.ommc.mixin.feature.highlightLavaSource.sodium;

import com.plusls.ommc.game.Configs;
import com.plusls.ommc.impl.feature.highlightLavaSource.LavaSourceResourceLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;

@Dependencies(require = @Dependency(value = "sodium", versionPredicates = "<0.3"))
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer", remap = false)
public class MixinFluidRenderer {
    @Shadow
    @Final
    private TextureAtlasSprite[] lavaSprites;

    @Dynamic
    @Inject(method = "render", at = @At("HEAD"))
    public void modifyLavaSprites(BlockAndTintGetter world, FluidState fluidState, BlockPos pos,
                                  @Coerce Object buffers, CallbackInfoReturnable<Boolean> info) {
        if (Configs.highlightLavaSource.getBooleanValue() && fluidState.is(FluidTags.LAVA) &&
                world.getBlockState(pos).getValue(LiquidBlock.LEVEL) == 0) {
            lavaSprites[0] = LavaSourceResourceLoader.lavaSourceStillSprite;
            lavaSprites[1] = LavaSourceResourceLoader.lavaSourceFlowSprite;
        }
    }

    @Dynamic
    @Inject(method = "render", at = @At("RETURN"))
    public void restoreLavaSprites(BlockAndTintGetter world, FluidState fluidState, BlockPos pos,
                                   @Coerce Object buffers, CallbackInfoReturnable<Boolean> info) {
        lavaSprites[0] = LavaSourceResourceLoader.defaultLavaSourceStillSprite;
        lavaSprites[1] = LavaSourceResourceLoader.defaultLavaSourceFlowSprite;
    }
}