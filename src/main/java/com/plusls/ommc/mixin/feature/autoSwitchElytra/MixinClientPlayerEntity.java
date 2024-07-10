package com.plusls.ommc.mixin.feature.autoSwitchElytra;

import com.mojang.authlib.GameProfile;
import com.plusls.ommc.config.Configs;
import com.plusls.ommc.feature.autoSwitchElytra.AutoSwitchElytraUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 11902 && MC < 11903
//$$ import net.minecraft.world.entity.player.ProfilePublicKey;
//#endif

//#if MC >= 11903
import net.minecraft.core.registries.BuiltInRegistries;
//#else
//$$ import net.minecraft.core.Registry;
//#endif

@Mixin(LocalPlayer.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayer {
    @Shadow
    @Final
    protected Minecraft minecraft;

    boolean prevFallFlying = false;

    //#if MC != 11902
    public MixinClientPlayerEntity(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }
    //#else
    //$$ public MixinClientPlayerEntity(ClientLevel world, GameProfile profile, ProfilePublicKey profilePublicKey) {
    //$$     super(world, profile, profilePublicKey);
    //$$ }
    //#endif

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
    private void autoSwitchElytra(CallbackInfo ci) {
        if (!Configs.autoSwitchElytra) {
            return;
        }
        ItemStack chestItemStack = this.getItemBySlot(EquipmentSlot.CHEST);
        if (chestItemStack.is(Items.ELYTRA) || !AutoSwitchElytraUtil.myCheckFallFlying(this)) {
            return;
        }
        AutoSwitchElytraUtil.autoSwitch(AutoSwitchElytraUtil.CHEST_SLOT_IDX, this.minecraft, (LocalPlayer) (Object) this, itemStack -> itemStack.is(Items.ELYTRA));
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "aiStep", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/player/LocalPlayer;isFallFlying()Z",
            //#if MC > 11404
            ordinal = 0
            //#else
            //$$ ordinal = 1
            //#endif
    ))
    private void autoSwitchChest(CallbackInfo ci) {
        if (!Configs.autoSwitchElytra) {
            return;
        }
        ItemStack chestItemStack = this.getItemBySlot(EquipmentSlot.CHEST);
        if (!chestItemStack.is(Items.ELYTRA) || !prevFallFlying || this.isFallFlying()) {
            prevFallFlying = this.isFallFlying();
            return;
        }
        prevFallFlying = this.isFallFlying();
        AutoSwitchElytraUtil.autoSwitch(AutoSwitchElytraUtil.CHEST_SLOT_IDX, this.minecraft, (LocalPlayer) (Object) this, itemStack -> AutoSwitchElytraUtil.isChestArmor(itemStack));
    }
}
