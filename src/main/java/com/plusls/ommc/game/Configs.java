package com.plusls.ommc.game;

import com.google.common.collect.ImmutableList;
import com.plusls.ommc.SharedConstants;
import com.plusls.ommc.impl.feature.sortInventory.SortInventoryShulkerBoxLastType;
import com.plusls.ommc.impl.feature.sortInventory.SortInventoryHelper;
import com.plusls.ommc.impl.generic.highlightWaypoint.HighlightWaypointHandler;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.malilib.annotation.Config;
import top.hendrixshen.magiclib.api.malilib.annotation.Statistic;
import top.hendrixshen.magiclib.api.malilib.config.MagicConfigManager;
import top.hendrixshen.magiclib.impl.malilib.config.MagicConfigFactory;
import top.hendrixshen.magiclib.impl.malilib.config.MagicConfigHandler;
import top.hendrixshen.magiclib.impl.malilib.config.option.*;
import top.hendrixshen.magiclib.util.collect.ValueContainer;
import top.hendrixshen.magiclib.util.minecraft.InfoUtil;

public class Configs {
    private static final MagicConfigManager cm = SharedConstants.getConfigManager();
    private static final MagicConfigFactory cf = Configs.cm.getConfigFactory();

    // Generic
    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigHotkey clearWaypoint = Configs.cf.newConfigHotkey("clearWaypoint", "C");

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBoolean debug = Configs.cf.newConfigBoolean("debug", false);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBoolean dontClearChatHistory = Configs.cf.newConfigBoolean("dontClearChatHistory", false);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBooleanHotkeyed forceParseWaypointFromChat = Configs.cf.newConfigBooleanHotkeyed("forceParseWaypointFromChat", false);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigInteger highlightBeamTime = Configs.cf.newConfigInteger("highlightBeamTime", 10, 0, Integer.MAX_VALUE);

    @Statistic(hotkey = false)
    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigHotkey openConfigGui = Configs.cf.newConfigHotkey("openConfigGui", "O,C");

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBooleanHotkeyed parseWaypointFromChat = Configs.cf.newConfigBooleanHotkeyed("parseWaypointFromChat", true);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigHotkey sendLookingAtBlockPos = Configs.cf.newConfigHotkey("sendLookingAtBlockPos", "O,P");

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBooleanHotkeyed sortInventorySupportEmptyShulkerBoxStack = Configs.cf.newConfigBooleanHotkeyed("sortInventorySupportEmptyShulkerBoxStack", true);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigHotkey sortInventory = Configs.cf.newConfigHotkey("sortInventory", "R");

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigOptionList sortInventoryShulkerBoxLast = Configs.cf.newConfigOptionList("sortInventoryShulkerBoxLast", SortInventoryShulkerBoxLastType.AUTO);

    // Feature
    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed autoSwitchElytra = Configs.cf.newConfigBooleanHotkeyed("autoSwitchElytra", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed betterSneaking = Configs.cf.newConfigBooleanHotkeyed("betterSneaking", false);

    @Dependencies(require = @Dependency(value = "minecraft", versionPredicates = ">1.15.2"))
    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed disableBlocklistCheck = Configs.cf.newConfigBooleanHotkeyed("disableBlocklistCheck", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed disableBreakBlock = Configs.cf.newConfigBooleanHotkeyed("disableBreakBlock", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed disableBreakScaffolding = Configs.cf.newConfigBooleanHotkeyed("disableBreakScaffolding", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed disableMoveDownInScaffolding = Configs.cf.newConfigBooleanHotkeyed("disableMoveDownInScaffolding", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed disablePistonPushEntity = Configs.cf.newConfigBooleanHotkeyed("disablePistonPushEntity", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed flatDigger = Configs.cf.newConfigBooleanHotkeyed("flatDigger", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed forceBreakingCooldown = Configs.cf.newConfigBooleanHotkeyed("forceBreakingCooldown", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed highlightLavaSource = Configs.cf.newConfigBooleanHotkeyed("highlightLavaSource", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed highlightPersistentMob = Configs.cf.newConfigBooleanHotkeyed("highlightPersistentMob", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBoolean highlightPersistentMobClientMode = Configs.cf.newConfigBoolean("highlightPersistentMobClientMode", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed preventWastageOfWater = Configs.cf.newConfigBooleanHotkeyed("preventWastageOfWater", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed preventIntentionalGameDesign = Configs.cf.newConfigBooleanHotkeyed("preventIntentionalGameDesign", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed realSneaking = Configs.cf.newConfigBooleanHotkeyed("realSneaking", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed removeBreakingCooldown = Configs.cf.newConfigBooleanHotkeyed("removeBreakingCooldown", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed worldEaterMineHelper = Configs.cf.newConfigBooleanHotkeyed("worldEaterMineHelper", false);

    // List
    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList blockModelNoOffsetBlacklist = Configs.cf.newConfigStringList("blockModelNoOffsetBlacklist");

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigOptionList blockModelNoOffsetListType = Configs.cf.newConfigOptionList("blockModelNoOffsetListType", UsageRestriction.ListType.NONE);

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList blockModelNoOffsetWhitelist = Configs.cf.newConfigStringList("blockModelNoOffsetWhitelist", ImmutableList.of("minecraft:wither_rose", "minecraft:poppy", "minecraft:dandelion"));

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList breakBlockBlackList = Configs.cf.newConfigStringList("breakBlockBlackList", ImmutableList.of("minecraft:budding_amethyst", "_bud"));

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList breakScaffoldingWhiteList = Configs.cf.newConfigStringList("breakScaffoldingWhiteList", ImmutableList.of("minecraft:air", "minecraft:scaffolding"));

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList highlightEntityBlackList = Configs.cf.newConfigStringList("highlightEntityBlackList");

    @Config(category = ConfigCategory.LIST)
    public static IConfigOptionListEntry highlightEntityListType = UsageRestriction.ListType.WHITELIST;

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList highlightEntityWhiteList = Configs.cf.newConfigStringList("highlightEntityWhiteList", ImmutableList.of("minecraft:wandering_trader"));

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList moveDownInScaffoldingWhiteList = Configs.cf.newConfigStringList("moveDownInScaffoldingWhiteList", ImmutableList.of("minecraft:air", "minecraft:scaffolding"));

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList worldEaterMineHelperWhitelist = Configs.cf.newConfigStringList("worldEaterMineHelperWhitelist", ImmutableList.of("_ore", "minecraft:ancient_debris", "minecraft:obsidian"));

    public static void init() {
        Configs.cm.parseConfigClass(Configs.class);
        SharedConstants.getConfigHandler().setPostDeserializeCallback(Configs::onConfigLoaded);

        // Generic
        IValueChangeCallback<ConfigBoolean> reloadLevelRender = (option) -> Minecraft.getInstance().levelRenderer.allChanged();

        MagicConfigManager.setHotkeyCallback(Configs.clearWaypoint, () -> HighlightWaypointHandler.getInstance().clearHighlightPos(), true);
        MagicConfigManager.setHotkeyCallback(Configs.openConfigGui, ConfigGui::openGui, true);

        MagicConfigManager.setHotkeyCallback(Configs.sendLookingAtBlockPos, () -> {
            Minecraft client = Minecraft.getInstance();
            Entity cameraEntity = client.getCameraEntity();
            MultiPlayerGameMode clientPlayerInteractionManager = client.gameMode;

            if (cameraEntity != null && clientPlayerInteractionManager != null) {
                //#if MC < 12005
                HitResult hitresult = cameraEntity.pick(clientPlayerInteractionManager.getPickRange(), client.getFrameTime(), false);
                //#elseif MC >= 12005 && MC <12007
                //$$ HitResult hitresult = cameraEntity.pick(clientPlayerInteractionManager.hasInfiniteItems() ? 5.0F : 4.5F, client.getFrameTime(), false);
                //#elseif MC >= 12100
                //$$ HitResult hitresult = cameraEntity.pick(clientPlayerInteractionManager.hasInfiniteItems() ? 5.0F : 4.5F, client.getFrameTimeNs(), false);
                //#endif

                if (hitresult.getType() == HitResult.Type.BLOCK) {
                    BlockPos lookPos = ((BlockHitResult) hitresult).getBlockPos();
                    if (client.player != null) {
                        String message = String.format("[%d, %d, %d]", lookPos.getX(), lookPos.getY(), lookPos.getZ());
                        InfoUtil.sendChat(message);
                    }
                }
            }
        }, true);


        MagicConfigManager.setHotkeyCallback(Configs.sortInventory,
                () -> ValueContainer.ofNullable(SortInventoryHelper.sort()).ifPresent(Runnable::run),
                false);

        Configs.highlightLavaSource.setValueChangeCallback(reloadLevelRender);
        Configs.worldEaterMineHelper.setValueChangeCallback(reloadLevelRender);

        // List
        Configs.blockModelNoOffsetListType.setValueChangeCallback(option -> Minecraft.getInstance().levelRenderer.allChanged());
    }

    private static void onConfigLoaded(MagicConfigHandler magicConfigHandler) {
        Configs.sortInventory.getKeybind().setSettings(KeybindSettings.GUI);
    }

    public static class ConfigCategory {
        public static final String GENERIC = "generic";
        public static final String FEATURE = "feature";
        public static final String LIST = "list";
    }
}
