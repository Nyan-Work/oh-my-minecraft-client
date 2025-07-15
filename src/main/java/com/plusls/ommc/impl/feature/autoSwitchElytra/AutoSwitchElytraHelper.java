package com.plusls.ommc.impl.feature.autoSwitchElytra;

import java.util.List;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.plusls.ommc.util.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import top.hendrixshen.magiclib.api.compat.minecraft.world.entity.player.PlayerCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.item.ItemStackCompat;

public class AutoSwitchElytraHelper {
    public static final int CHEST_SLOT_IDX = 6;

    public static boolean checkFall(Player player) {
        PlayerCompat playerCompat = PlayerCompat.of(player);
        return !playerCompat.isOnGround() &&
                !player.isFallFlying() &&
                !player.isInWaterOrBubble() &&
                !player.isInLava() &&
                !player.hasEffect(MobEffects.LEVITATION);
    }

    public static boolean isChestArmor(ItemStack itemStack) {
        return InventoryUtil.getEquipmentSlotForItem(itemStack) == EquipmentSlot.CHEST &&
                !ItemStackCompat.of(itemStack).is(Items.ELYTRA);
    }

    public static void autoSwitch(int sourceSlot, Minecraft client, LocalPlayer localPlayer, Predicate<ItemStack> check) {
        if (client.gameMode == null) {
            return;
        }

        if (localPlayer.containerMenu != localPlayer.inventoryMenu) {
            localPlayer.closeContainer();
        }

        AbstractContainerMenu screenHandler = localPlayer.containerMenu;
        List<ItemStack> itemStacks = Lists.newArrayList();

        for (int i = 0; i < screenHandler.slots.size(); i++) {
            itemStacks.add(screenHandler.slots.get(i).getItem().copy());
        }

        int idxToSwitch = -1;

        for (int i = 0; i < itemStacks.size(); i++) {
            if (check.test(itemStacks.get(i))) {
                idxToSwitch = i;
                break;
            }
        }

        if (idxToSwitch != -1) {
            client.gameMode.handleInventoryMouseClick(screenHandler.containerId, idxToSwitch, 0, ClickType.PICKUP, localPlayer);
            client.gameMode.handleInventoryMouseClick(screenHandler.containerId, sourceSlot, 0, ClickType.PICKUP, localPlayer);
            client.gameMode.handleInventoryMouseClick(screenHandler.containerId, idxToSwitch, 0, ClickType.PICKUP, localPlayer);
        }
    }
}
