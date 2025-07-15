package com.plusls.ommc.mixin.feature.highlightPersistentMob;

import com.plusls.ommc.game.Configs;
import fi.dy.masa.malilib.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.util.MiscUtil;

import java.util.Arrays;
import java.util.List;

//#if MC >= 11903
import net.minecraft.core.registries.BuiltInRegistries;
//#else
//$$ import net.minecraft.core.Registry;
//#endif

@Mixin(Entity.class)
public class MixinEntity {
    private static final List<String> ommc$itemBlackList = Arrays.asList("sword", "bow", "trident", "axe", "fishing_rod");

    private static <T extends Entity> T ommc$getBestEntity(T entity) {
        // Only try to fetch the corresponding server world if the entity is in the actual client world.
        // Otherwise the entity may be for example in Litematica's schematic world.
        Level world = entity.getCommandSenderWorld();
        Minecraft client = Minecraft.getInstance();
        T ret = entity;

        if (world == client.level) {
            world = WorldUtils.getBestWorld(client);

            if (world != null && world != client.level) {
                Entity bestEntity = world.getEntity(entity.getId());

                if (entity.getClass().isInstance(bestEntity)) {
                    ret = MiscUtil.cast(bestEntity);
                }
            }
        }

        return ret;
    }

    @Inject(
            method = "isCurrentlyGlowing",
            at = @At("RETURN"),
            cancellable = true
    )
    private void checkWanderingTraderEntity(CallbackInfoReturnable<Boolean> cir) {
        if (Configs.highlightPersistentMob.getBooleanValue() && !cir.getReturnValue()) {
            Entity entity = ommc$getBestEntity(MiscUtil.cast(this));

            if (entity instanceof Mob) {
                Mob mobEntity = (Mob) entity;

                if (mobEntity.requiresCustomPersistence() || mobEntity.isPersistenceRequired()) {
                    cir.setReturnValue(true);
                    return;
                }

                if (!Configs.highlightPersistentMobClientMode.getBooleanValue()) {
                    return;
                }

                //#if MC >= 11903
                String mainHandItemName = BuiltInRegistries.ITEM.getKey(mobEntity.getMainHandItem().getItem()).toString();
                //#else
                //$$ String mainHandItemName = Registry.ITEM.getKey(mobEntity.getMainHandItem().getItem()).toString();
                //#endif
                if (!mobEntity.getMainHandItem().isEmpty() && ommc$itemBlackList.stream().noneMatch(mainHandItemName::contains) ||
                        entity.getCustomName() != null) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
