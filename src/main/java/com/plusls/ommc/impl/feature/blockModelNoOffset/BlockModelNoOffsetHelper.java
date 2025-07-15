package com.plusls.ommc.impl.feature.blockModelNoOffset;

import com.plusls.ommc.game.Configs;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import net.minecraft.world.level.block.state.BlockState;

//#if MC > 11902
import net.minecraft.core.registries.BuiltInRegistries;
//#else
//$$ import net.minecraft.core.Registry;
//#endif

public class BlockModelNoOffsetHelper {
    public static boolean shouldNoOffset(BlockState blockState) {
        //#if MC > 11902
        String blockId = BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();
        //#else
        //$$ String blockId = Registry.BLOCK.getKey(blockState.getBlock()).toString();
        //#endif

        String blockName = blockState.getBlock().getName().getString();

        if (Configs.blockModelNoOffsetListType.getOptionListValue() == UsageRestriction.ListType.WHITELIST) {
            return Configs.blockModelNoOffsetWhitelist.getStrings().stream().anyMatch(s -> blockId.contains(s) || blockName.contains(s));
        } else if (Configs.blockModelNoOffsetListType.getOptionListValue() == UsageRestriction.ListType.BLACKLIST) {
            return Configs.blockModelNoOffsetBlacklist.getStrings().stream().noneMatch(s -> blockId.contains(s) || blockName.contains(s));
        }

        return false;
    }
}
