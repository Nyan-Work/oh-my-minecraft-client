package com.plusls.ommc.mixin.feature.betterSneaking;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.plusls.ommc.game.Configs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import top.hendrixshen.magiclib.api.compat.minecraft.world.entity.EntityCompat;
import top.hendrixshen.magiclib.util.collect.Provider;

//#if MC > 12004
//$$ import org.spongepowered.asm.mixin.Shadow;
//#endif

@Mixin(
        //#if MC > 11404
        Player.class
        //#else
        //$$ Entity.class
        //#endif
)

public abstract class MixinPlayerEntity {
    @Unique
    private static final float ommc$MAX_STEP_HEIGHT = 1.2F;

    @Unique
    private float ommc$original_step_height = 0.0F;

    //#if MC > 12004
    //$$ @Shadow
    //$$ protected abstract boolean canFallAtLeast(double par1, double par2, float par3);
    //#endif

    @WrapOperation(
            method = "maybeBackOffFromEdge",
            at = @At(
                    //#if MC > 11903
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;maxUpStep()F"
                    //#else
                    //$$ value = "FIELD",
                    //#if MC > 11404
                    //$$ target = "Lnet/minecraft/world/entity/player/Player;maxUpStep:F"
                    //#else
                    //$$ target = "Lnet/minecraft/world/entity/Entity;maxUpStep:F"
                    //#endif
                    //#endif
            )
    )
    private float fakeStepHeight(
            //#if MC > 11404
            Player instance,
            //#else
            //$$ Entity instance,
            //#endif
            Operation<Float> original
    ) {
        EntityCompat entityCompat = EntityCompat.of(instance);
        this.ommc$original_step_height = original.call(instance);

        if (!Configs.betterSneaking.getBooleanValue() || !entityCompat.getLevel().isClientSide()) {
            return this.ommc$original_step_height;
        }

        return MixinPlayerEntity.ommc$MAX_STEP_HEIGHT;
    }

    @WrapOperation(
            method = "maybeBackOffFromEdge",
            at = @At(
                    value = "INVOKE",
                    //#if MC > 12004
                    //$$ target = "Lnet/minecraft/world/entity/player/Player;canFallAtLeast(DDF)Z"
                    //#else
                    target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z"
                    //#endif
            )
    )
    private boolean checkFallAtLava(
            //#if MC > 12004
            //$$ Player entity,
            //$$ double d,
            //$$ double e,
            //$$ float f,
            //#else
            Level level,
            Entity entity,
            AABB aabb,
            //#endif
            Operation<Boolean> original
    ) {
        EntityCompat entityCompat = EntityCompat.of(entity);

        //#if MC > 12004
        //$$ Level level = entity.level();
        //#endif

        // Patched value if betterSneak is enabled, otherwise vanilla value.
        boolean result = original.call(
                //#if MC > 12004
                //$$ entity,
                //$$ d,
                //$$ e,
                //$$ f
                //#else
                level,
                entity,
                aabb
                //#endif
        );

        if (!Configs.betterSneaking.getBooleanValue() || !level.isClientSide()) {
            return result;
        }

        // Always vanilla value and bypass WrapOperation chain invoke.
        //#if MC > 12004
        //$$ boolean originalResult = this.canFallAtLeast(d, e, this.ommc$original_step_height);
        //#else
        boolean originalResult = level.noCollision(entity, aabb.move(0, MixinPlayerEntity.ommc$MAX_STEP_HEIGHT - this.ommc$original_step_height, 0));
        //#endif

        if ((originalResult && !result) && level.getFluidState(entityCompat.getBlockPosition().below()).getType() instanceof LavaFluid) {
            return true;
        }

        return result;
    }
}
