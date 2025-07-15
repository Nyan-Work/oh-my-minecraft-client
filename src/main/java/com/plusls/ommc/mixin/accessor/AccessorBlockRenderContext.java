package com.plusls.ommc.mixin.accessor;

import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;

@Dependencies(require = @Dependency(value = "sodium", versionPredicates = ">0.4.8"))
@Mixin(BlockRenderContext.class)
public interface AccessorBlockRenderContext {
    @Accessor
    void setModel(BakedModel model);
}
