package com.plusls.ommc.mixin.feature.highlightEntity;

import com.plusls.ommc.game.Configs;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
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

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    //#if MC > 11904
    private
    //#else
    //$$ public
    //#endif
    Level level;

    @Inject(
            method = "isCurrentlyGlowing",
            at = @At("RETURN"),
            cancellable = true
    )
    private void checkWanderingTraderEntity(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() || !this.level.isClientSide) {
            return;
        }

        //#if MC >= 11903
        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).toString();
        //#else
        //$$ String entityId = Registry.ENTITY_TYPE.getKey(this.getType()).toString();
        //#endif

        String entityName = this.getType().getDescription().getString();
        if (Configs.highlightEntityListType == UsageRestriction.ListType.WHITELIST) {
            cir.setReturnValue(Configs.highlightEntityWhiteList.getStrings().stream().anyMatch(s -> entityId.contains(s) || entityName.contains(s)));
        } else if (Configs.highlightEntityListType == UsageRestriction.ListType.BLACKLIST) {
            cir.setReturnValue(Configs.highlightEntityBlackList.getStrings().stream().noneMatch(s -> entityId.contains(s) || entityName.contains(s)));
        }
    }
}
