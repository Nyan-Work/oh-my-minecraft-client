package com.plusls.ommc.impl.feature.worldEaterMineHelper;

import com.plusls.ommc.game.Configs;
import com.plusls.ommc.mixin.accessor.AccessorBlockStateBase;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import top.hendrixshen.magiclib.util.MiscUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

//#if MC >= 11903
import net.minecraft.core.registries.BuiltInRegistries;
//#else
//$$ import net.minecraft.core.Registry;
//#endif

public class WorldEaterMineHelper {
    public static final Map<Block, BakedModel> customModels = new HashMap<>();
    public static final Map<Block, BakedModel> customFullModels = new HashMap<>();

    public static boolean blockInWorldEaterMineHelperWhitelist(Block block) {
        String blockName = block.getName().getString();
        //#if MC >= 11903
        String blockId = BuiltInRegistries.BLOCK.getKey(block).toString();
        //#else
        //$$ String blockId = Registry.BLOCK.getKey(block).toString();
        //#endif
        return Configs.worldEaterMineHelperWhitelist
                .getStrings()
                .stream()
                .anyMatch(s -> blockId.contains(s) || blockName.contains(s));
    }

    public static boolean shouldUseCustomModel(BlockState blockState, BlockPos pos) {
        Block block = blockState.getBlock();

        if (!Configs.worldEaterMineHelper.getBooleanValue() ||
                !WorldEaterMineHelper.blockInWorldEaterMineHelperWhitelist(block)) {
            return false;
        }

        ClientLevel world = Minecraft.getInstance().level;

        if (world == null) {
            return false;
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int yMax = world.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

        if (y < yMax) {
            int j = 0;

            for (int i = y + 1; i <= yMax; ++i) {
                if (world.getBlockState(new BlockPos(x, i, z))
                        //#if MC > 11904
                        .isSolid()
                        //#else
                        //$$ .getMaterial().isSolidBlocking()
                        //#endif
                        && j < 20
                ) {
                    return false;
                }

                j++;
            }
        }

        return true;
    }

    static public void emitCustomFullBlockQuads(FabricBakedModel model, BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<?> randomSupplier, RenderContext context) {
        Block block = state.getBlock();

        if (WorldEaterMineHelper.shouldUseCustomModel(state, pos)) {
            FabricBakedModel customModel = (FabricBakedModel) WorldEaterMineHelper.customFullModels.get(block);
            if (customModel != null) {
                int luminance = ((AccessorBlockStateBase) state).getLightEmission();
                ((AccessorBlockStateBase) state).setLightEmission(15);
                customModel.emitBlockQuads(blockView, state, pos, MiscUtil.cast(randomSupplier), context);
                ((AccessorBlockStateBase) state).setLightEmission(luminance);
                return;
            }
        }

        model.emitBlockQuads(blockView, state, pos, MiscUtil.cast(randomSupplier), context);
    }

    public static void emitCustomBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<?> randomSupplier, RenderContext context) {
        Block block = state.getBlock();

        if (WorldEaterMineHelper.shouldUseCustomModel(state, pos)) {
            FabricBakedModel customModel = (FabricBakedModel) WorldEaterMineHelper.customModels.get(block);

            if (customModel != null) {
                int luminance = ((AccessorBlockStateBase) state).getLightEmission();
                ((AccessorBlockStateBase) state).setLightEmission(15);
                customModel.emitBlockQuads(blockView, state, pos, MiscUtil.cast(randomSupplier), context);
                ((AccessorBlockStateBase) state).setLightEmission(luminance);
            }
        }
    }
}
