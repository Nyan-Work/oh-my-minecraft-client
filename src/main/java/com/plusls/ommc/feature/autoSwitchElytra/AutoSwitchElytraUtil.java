package com.plusls.ommc.feature.autoSwitchElytra;

import java.util.ArrayList;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AutoSwitchElytraUtil {
    public static final int CHEST_SLOT_IDX = 6;

    public static boolean myCheckFallFlying(Player player) {
        return !player.onGround() && !player.isFallFlying() && !player.isInLiquid() && !player.hasEffect(MobEffects.LEVITATION);
    }

    public static boolean isChestArmor(ItemStack itemStack) {
        return Mob.getEquipmentSlotForItem(itemStack) == EquipmentSlot.CHEST && !itemStack.is(Items.ELYTRA);
    }

    public static void autoSwitch(int sourceSlot, Minecraft client, LocalPlayer clientPlayerEntity, Predicate<ItemStack> check) {
        if (client.gameMode == null) {
            return;
        }
        if (clientPlayerEntity.containerMenu != clientPlayerEntity.inventoryMenu) {
            clientPlayerEntity.closeContainer();
        }
        AbstractContainerMenu screenHandler = clientPlayerEntity.containerMenu;
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i < screenHandler.slots.size(); ++i) {
            itemStacks.add(screenHandler.slots.get(i).getItem().copy());
        }

        int idxToSwitch = -1;
        for (int i = 0; i < itemStacks.size(); ++i) {
            if (check.test(itemStacks.get(i))) {
                idxToSwitch = i;
                break;
            }
        }
        if (idxToSwitch != -1) {
            client.gameMode.handleInventoryMouseClick(screenHandler.containerId, idxToSwitch, 0, ClickType.PICKUP, clientPlayerEntity);
            client.gameMode.handleInventoryMouseClick(screenHandler.containerId, sourceSlot, 0, ClickType.PICKUP, clientPlayerEntity);
            client.gameMode.handleInventoryMouseClick(screenHandler.containerId, idxToSwitch, 0, ClickType.PICKUP, clientPlayerEntity);
        }
    }
}
