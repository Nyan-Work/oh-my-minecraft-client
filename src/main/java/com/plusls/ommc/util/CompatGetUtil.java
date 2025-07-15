package com.plusls.ommc.util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.api.compat.minecraft.world.entity.EntityCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.entity.LivingEntityCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.level.LevelCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.inventory.SlotCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.world.item.ItemStackCompat;
import top.hendrixshen.magiclib.util.MiscUtil;

public class CompatGetUtil {
    public static LivingEntityCompat getLivingEntityCompat(@NotNull Object obj) {
        LivingEntity thisObj = MiscUtil.cast(obj);
        return LivingEntityCompat.of(thisObj);
    }


    public static EntityCompat getEntityCompat(@NotNull Object obj) {
        Entity thisObj = MiscUtil.cast(obj);
        return EntityCompat.of(thisObj);
    }

    public static LevelCompat getLevelCompat(@NotNull Object obj) {
        Level thisObj = MiscUtil.cast(obj);
        return LevelCompat.of(thisObj);
    }

    public static SlotCompat getSlotCompat(@NotNull Object obj) {
        Slot thisObj = MiscUtil.cast(obj);
        return SlotCompat.of(thisObj);
    }

    public static ItemStackCompat getItemStackCompat(@NotNull Object obj) {
        ItemStack thisObj = MiscUtil.cast(obj);
        return ItemStackCompat.of(thisObj);
    }
}
