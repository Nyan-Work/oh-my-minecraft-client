package com.plusls.ommc.mixin.feature.worldEaterMineHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;
import com.plusls.ommc.mixin.accessor.AccessorBlockModel;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.compat.minecraft.resources.ResourceLocationCompat;
import top.hendrixshen.magiclib.util.MiscUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

//#if MC >= 11903
import net.minecraft.core.registries.BuiltInRegistries;
//#else
//$$ import net.minecraft.core.Registry;
//#endif

@Mixin(value = BlockModel.class, priority = 999)
public abstract class MixinJsonUnbakedModel implements UnbakedModel {
    @Unique
    private final ThreadLocal<Boolean> ommc$bakeTag = ThreadLocal.withInitial(() -> Boolean.TRUE);

    @Shadow
    public abstract List<BlockElement> getElements();

    @Shadow
    @Nullable
    protected ResourceLocation parentLocation;

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(
            //#if MC > 12006
            //$$ method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Z)Lnet/minecraft/client/resources/model/BakedModel;",
            //#elseif MC > 11902
            method = "bake(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;",
            //#elseif MC > 11404
            //$$ method = "bake(Lnet/minecraft/client/resources/model/ModelBakery;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/resources/model/BakedModel;",
            //#else
            //$$ method = "bake(Lnet/minecraft/client/resources/model/ModelBakery;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/resources/model/BakedModel;",
            //#endif
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void generateCustomBakedModel(
            //#if MC > 11902
            ModelBaker baker,
            //#else
            //$$ ModelBakery baker,
            //#endif
            BlockModel parentModel,
            //#if MC > 11404
            Function<Material, TextureAtlasSprite> textureGetter,
            //#else
            //$$ Function<ResourceLocation, TextureAtlasSprite> textureGetter,
            //#endif
            ModelState modelSettings,
            //#if MC > 11404
            //#if MC < 12100
            ResourceLocation resourceLocation,
            //#endif
            boolean hasDepth,
            //#endif
            CallbackInfoReturnable<BakedModel> cir
    ) {
        if (!this.ommc$bakeTag.get()) {
            return;
        }

        ResourceLocation identifier = this.parentLocation;

        if (identifier == null) {
            return;
        }

        String[] splitResult = identifier.getPath().split("/");
        ResourceLocation blockId = ResourceLocationCompat.parse(splitResult[splitResult.length - 1]);
        //#if MC >= 11903
        Block block = BuiltInRegistries.BLOCK.get(blockId);
        //#else
        //$$ Block block = Registry.BLOCK.get(blockId);
        //#endif

        if (block == Blocks.AIR) {
            return;
        }

        BlockModel model = MiscUtil.cast(this);
        this.ommc$bakeTag.set(false);
        List<BlockElement> originalModelElements = this.getElements();
        List<BlockElement> originalModelElementsBackup = Lists.newArrayList(originalModelElements);
        originalModelElements.clear();

        for (BlockElement modelElement : originalModelElementsBackup) {
            Vector3f origin = new Vector3f(0F, 67F, 150F);
            origin.mul(0.0625F);
            BlockElementRotation newModelRotation = new BlockElementRotation(origin, Direction.Axis.X, 45, false);
            Map<Direction, BlockElementFace> faces = Maps.newHashMap();

            for (Map.Entry<Direction, BlockElementFace> entry : modelElement.faces.entrySet()) {
                BlockElementFace originalModelElementFace = entry.getValue();
                //#if MC > 12006
                //$$ BlockElementFace modelElementFace = new BlockElementFace(null, originalModelElementFace.tintIndex(), originalModelElementFace.texture(), originalModelElementFace.uv());
                //#else
                BlockElementFace modelElementFace = new BlockElementFace(null, originalModelElementFace.tintIndex, originalModelElementFace.texture, originalModelElementFace.uv);
                //#endif
                faces.put(entry.getKey(), modelElementFace);
            }

            originalModelElements.add(new BlockElement(modelElement.from, modelElement.to, faces, newModelRotation, modelElement.shade));
        }

        BlockModel blockModel = model;

        // Find and save original model attribute
        while ((((AccessorBlockModel) blockModel).getParent() != null)) {
            blockModel = ((AccessorBlockModel) blockModel).getParent();
        }

        //#if MC > 11903
        Boolean bool = ((AccessorBlockModel) blockModel).getHasAmbientOcclusion();
        boolean originalAmbientOcclusion = bool == null || bool;
        //#else
        //$$ boolean originalAmbientOcclusion = ((AccessorBlockModel) blockModel).getHasAmbientOcclusion();
        //#endif

        // Modify OMMC model attribute
        ((AccessorBlockModel) blockModel).setHasAmbientOcclusion(false);

        // OMMC part only model bake
        BakedModel customBakedModel = model.bake(
                baker,
                parentModel,
                textureGetter,
                //#if MC > 11404
                modelSettings,
                //#if MC <= 12006
                identifier,
                //#endif
                hasDepth
                //#else
                //$$ modelSettings
                //#endif
        );
        WorldEaterMineHelper.customModels.put(block, customBakedModel);

        // Full model bake
        originalModelElements.addAll(originalModelElementsBackup);
        BakedModel customFullBakedModel = model.bake(
                baker,
                parentModel,
                textureGetter,
                //#if MC > 11404
                modelSettings,
                //#if MC <= 12006
                identifier,
                //#endif
                hasDepth
                //#else
                //$$ modelSettings
                //#endif
        );
        WorldEaterMineHelper.customFullModels.put(block, customFullBakedModel);
        // Restore model attribute
        ((AccessorBlockModel) blockModel).setHasAmbientOcclusion(originalAmbientOcclusion);
        originalModelElements.clear();
        originalModelElements.addAll(originalModelElementsBackup);
        // Bake original model
        BakedModel ret = model.bake(
                baker,
                parentModel,
                textureGetter,
                //#if MC > 11404
                modelSettings,
                //#if MC <= 12006
                identifier,
                //#endif
                hasDepth
                //#else
                //$$ modelSettings
                //#endif
        );
        this.ommc$bakeTag.set(true);
        cir.setReturnValue(ret);
    }
}
