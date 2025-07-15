package com.plusls.ommc.impl.feature.preventWastageOfWater;

import com.plusls.ommc.game.Configs;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

//#if MC <= 11404
//$$ import net.minecraft.world.InteractionResult;
//#endif

public class PreventWastageOfWaterHelper implements UseItemCallback {
    public static void init() {
        PreventWastageOfWaterHelper handler = new PreventWastageOfWaterHelper();
        UseItemCallback.EVENT.register(handler);
    }

    //#if MC > 11404
    @Override
    public InteractionResultHolder<ItemStack> interact(Player player, Level world, InteractionHand hand) {
        return (Configs.preventWastageOfWater.getBooleanValue()
                && world.isClientSide
                && player.getItemInHand(hand).getItem() == Items.WATER_BUCKET
                //#if MC > 11502
                && world.dimensionType().ultraWarm())
                //#else
                //$$ && world.getDimension().isUltraWarm())
                //#endif
                ? InteractionResultHolder.fail(ItemStack.EMPTY)
                : InteractionResultHolder.pass(ItemStack.EMPTY);
    }
    //#else
    //$$ @Override
    //$$ public InteractionResult interact(Player player, Level world, InteractionHand hand) {
    //$$     return (Configs.preventWastageOfWater.getBooleanValue()
    //$$             && world.isClientSide
    //$$             && player.getItemInHand(hand).getItem() == Items.WATER_BUCKET
    //$$             && world.getDimension().isUltraWarm())
    //$$             ? InteractionResult.FAIL
    //$$             : InteractionResult.PASS;
    //$$ }
    //#endif
}
