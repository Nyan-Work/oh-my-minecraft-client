package com.plusls.ommc.mixin.accessor;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface AccessorBlock {
    @Accessor
    MaterialColor getMaterialColor();
}
