package com.plusls.ommc.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

//#if MC > 12006
//$$ import net.minecraft.world.item.Equipable;
//#elseif MC > 11605
import net.minecraft.world.entity.LivingEntity;
//#else
//$$ import net.minecraft.world.entity.Mob;
//#endif

public class InventoryUtil {
    public static @NotNull EquipmentSlot getEquipmentSlotForItem(ItemStack itemStack) {
        //#if MC > 12006
        //$$ Equipable equipable = Equipable.get(itemStack);
        //$$ return equipable != null ? equipable.getEquipmentSlot() : EquipmentSlot.MAINHAND;
        //#elseif MC > 11605
        return LivingEntity.getEquipmentSlotForItem(itemStack);
        //#else
        //$$ return Mob.getEquipmentSlotForItem(itemStack);
        //#endif
    }
}
