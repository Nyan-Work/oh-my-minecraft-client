package com.plusls.ommc.mixin.accessor;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface AccessorSlot {
    @Accessor
    int getSlot();
}
