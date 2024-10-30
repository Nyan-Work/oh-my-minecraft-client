package com.plusls.ommc.mixin.feature.autoSwitchElytra;

import com.mojang.authlib.GameProfile;
import com.plusls.ommc.impl.feature.autoSwitchElytra.AutoSwitchElytraHelper;
import com.plusls.ommc.game.Configs;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.magiclib.api.compat.minecraft.world.item.ItemStackCompat;

//#if MC < 11903
//$$ import net.minecraft.core.Registry;
//$$
//#if MC > 11901
//$$ import net.minecraft.world.entity.player.ProfilePublicKey;
//#endif
//#endif

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends AbstractClientPlayer {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Unique
    boolean ommc$prevFallFlying = false;

    public MixinLocalPlayer(
            ClientLevel world,
            GameProfile profile
            //#if MC == 11902
            //$$ , ProfilePublicKey profilePublicKey
            //#endif
    ) {
        super(
                world,
                profile
                //#if MC == 11902
                //$$ , profilePublicKey
                //#endif
        );
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private void autoSwitchElytra(CallbackInfo ci) {
        if (!Configs.autoSwitchElytra.getBooleanValue()) {
            return;
        }

        ItemStack chestItemStack = this.getItemBySlot(EquipmentSlot.CHEST);
        ItemStackCompat chestItemStackCompat = ItemStackCompat.of(chestItemStack);

        if (chestItemStackCompat.is(Items.ELYTRA) || !AutoSwitchElytraHelper.checkFall(this)) {
            return;
        }

        AutoSwitchElytraHelper.autoSwitch(AutoSwitchElytraHelper.CHEST_SLOT_IDX, this.minecraft,
                (LocalPlayer) (Object) this, itemStack -> ItemStackCompat.of(itemStack).is(Items.ELYTRA));
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(
            method = "aiStep",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/client/player/LocalPlayer;isFallFlying()Z",
                    //#if MC > 11404
                    ordinal = 0
                    //#else
                    //$$ ordinal = 1
                    //#endif
            )
    )
    private void autoSwitchChest(CallbackInfo ci) {
        if (!Configs.autoSwitchElytra.getBooleanValue()) {
            return;
        }

        ItemStack chestItemStack = this.getItemBySlot(EquipmentSlot.CHEST);
        ItemStackCompat chestItemStackCompat = ItemStackCompat.of(chestItemStack);

        if (!chestItemStackCompat.is(Items.ELYTRA) || !this.ommc$prevFallFlying || this.isFallFlying()) {
            this.ommc$prevFallFlying = this.isFallFlying();
            return;
        }

        this.ommc$prevFallFlying = this.isFallFlying();

        AutoSwitchElytraHelper.autoSwitch(
                AutoSwitchElytraHelper.CHEST_SLOT_IDX,
                this.minecraft,
                (LocalPlayer) (Object) this,
                AutoSwitchElytraHelper::isChestArmor
        );
    }
}
