package com.plusls.ommc.mixin.feature.highlightLavaSource.canvas;

import com.plusls.ommc.game.Configs;
import com.plusls.ommc.impl.feature.highlightLavaSource.LavaSourceResourceLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;

@Dependencies(require = @Dependency("frex"))
@Pseudo
@Mixin(targets = "io.vram.frex.impl.model.SimpleFluidSpriteProvider", remap = false)
public abstract class MixinSimpleFluidSpriteProvider {
    @Unique
    private boolean ommc$isLava;
    @Unique
    private final TextureAtlasSprite[] ommc$lavaSourceSpites = new TextureAtlasSprite[3];

    @Dynamic
    @Inject(method = "<init>", at = @At("RETURN"))
    private void preInit(ResourceLocation stillSpriteName, ResourceLocation flowingSpriteName,
                         ResourceLocation overlaySpriteName, CallbackInfo ci) {
        this.ommc$isLava = stillSpriteName.toString().equals("minecraft:block/lava_still");
    }

    @Dynamic
    @Inject(
            method = "getFluidSprites",
            at = @At("RETURN"),
            cancellable = true
    )
    private void setLavaSprite(BlockAndTintGetter view, BlockPos pos,
                               FluidState state, CallbackInfoReturnable<TextureAtlasSprite[]> cir) {
        if (this.ommc$isLava) {
            if (this.ommc$lavaSourceSpites[0] != LavaSourceResourceLoader.lavaSourceSpites[0]) {
                this.ommc$lavaSourceSpites[0] = LavaSourceResourceLoader.lavaSourceSpites[0];
                this.ommc$lavaSourceSpites[1] = LavaSourceResourceLoader.lavaSourceSpites[1];
            }

            if (Configs.highlightLavaSource.getBooleanValue() && state.is(FluidTags.LAVA) &&
                    view.getBlockState(pos).getValue(LiquidBlock.LEVEL) == 0) {
                cir.setReturnValue(this.ommc$lavaSourceSpites);
            }
        }
    }
}
