package com.plusls.ommc.mixin.feature.disableMoveDownInScaffolding;

import com.plusls.ommc.game.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 11903
import net.minecraft.core.registries.BuiltInRegistries;
//#else
//$$ import net.minecraft.core.Registry;
//#endif

@Mixin(ScaffoldingBlock.class)
public class MixinScaffoldingBlock {
    @Shadow
    @Final
    private static VoxelShape STABLE_SHAPE;

    @Inject(
            method = "getCollisionShape",
            at = @At("RETURN"),
            cancellable = true
    )
    private void setNormalOutlineShape(BlockState state, BlockGetter world, BlockPos pos,
                                       CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context.isDescending() && Configs.disableMoveDownInScaffolding.getBooleanValue() &&
                context.isAbove(Shapes.block(), pos, true) &&
                cir.getReturnValue() != MixinScaffoldingBlock.STABLE_SHAPE) {

            assert Minecraft.getInstance().player != null;
            Item item = Minecraft.getInstance().player.getMainHandItem().getItem();
            //#if MC >= 11903
            String itemId = BuiltInRegistries.ITEM.getKey(item).toString();
            //#else
            //$$ String itemId = Registry.ITEM.getKey(item).toString();
            //#endif
            String itemName = item.getDescription().getString();

            if (Configs.moveDownInScaffoldingWhiteList
                    .getStrings()
                    .stream()
                    .anyMatch(s -> itemId.contains(s) || itemName.contains(s))
            ) {
                return;
            }

            cir.setReturnValue(MixinScaffoldingBlock.STABLE_SHAPE);
        }
    }
}
