package net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockRenderer {
    public native void renderModel(BakedModel model, BlockState state, BlockPos pos, BlockPos origin);
}
