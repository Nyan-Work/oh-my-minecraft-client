package com.plusls.ommc.impl.feature.worldEaterMineHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockModelRendererContext {
    public BlockPos pos;
    public BlockState state;

    public void clear() {
        pos = null;
        state = null;
    }
}
