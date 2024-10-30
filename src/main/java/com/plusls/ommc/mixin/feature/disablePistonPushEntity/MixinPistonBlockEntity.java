package com.plusls.ommc.mixin.feature.disablePistonPushEntity;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.plusls.ommc.game.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PistonMovingBlockEntity.class)
public class MixinPistonBlockEntity {
    @WrapOperation(
            method = "moveCollidedEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            )
    )
    private
    //#if MC > 11605
    static
    //#endif
    List<Entity> removeNoPlayerEntity(Level instance, Entity entity, AABB aabb, Operation<List<Entity>> original) {
        if (instance.isClientSide() && Configs.disablePistonPushEntity.getBooleanValue()) {
            LocalPlayer playerEntity = Minecraft.getInstance().player;

            if (playerEntity != null && !playerEntity.isSpectator() &&
                    playerEntity.getBoundingBox().intersects(aabb)
            ) {
                return ImmutableList.of(playerEntity);
            }

            return ImmutableList.of();
        }

        return original.call(instance, entity, aabb);
    }
}
