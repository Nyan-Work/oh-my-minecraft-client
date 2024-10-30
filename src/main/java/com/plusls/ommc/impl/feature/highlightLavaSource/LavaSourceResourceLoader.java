package com.plusls.ommc.impl.feature.highlightLavaSource;

import com.plusls.ommc.SharedConstants;
import com.plusls.ommc.game.Configs;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

//#if MC < 11903
//$$ import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
//$$ import net.minecraft.client.renderer.texture.TextureAtlas;
//#endif

public class LavaSourceResourceLoader implements SimpleSynchronousResourceReloadListener {
    public static final TextureAtlasSprite[] lavaSourceSpites = new TextureAtlasSprite[2];
    public static final TextureAtlasSprite[] defaultLavaSourceSpites = new TextureAtlasSprite[2];
    private static final ResourceLocation listenerId = SharedConstants.identifier("lava_reload_listener");
    private static final ResourceLocation flowingSpriteId = SharedConstants.identifier("block/lava_flow");
    private static final ResourceLocation stillSpriteId = SharedConstants.identifier("block/lava_still");
    public static TextureAtlasSprite lavaSourceFlowSprite;
    public static TextureAtlasSprite lavaSourceStillSprite;
    public static TextureAtlasSprite defaultLavaSourceFlowSprite;
    public static TextureAtlasSprite defaultLavaSourceStillSprite;

    public static void init() {
        //#if MC < 11903
        //$$ ClientSpriteRegistryCallback.event(TextureAtlas.LOCATION_BLOCKS).register((atlasTexture, registry) ->
        //$$ {
        //$$     registry.register(flowingSpriteId);
        //$$     registry.register(stillSpriteId);
        //$$ });
        //#endif
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new LavaSourceResourceLoader());
    }

    @Override
    public ResourceLocation getFabricId() {
        return listenerId;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        //#if MC > 11404
        final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        lavaSourceStillSprite = atlas.apply(stillSpriteId);
        lavaSourceFlowSprite = atlas.apply(flowingSpriteId);
        //#else
        //$$ TextureAtlas atlas = Minecraft.getInstance().getTextureAtlas();
        //$$ lavaSourceStillSprite = atlas.getSprite(stillSpriteId);
        //$$ lavaSourceFlowSprite = atlas.getSprite(flowingSpriteId);
        //#endif
        lavaSourceSpites[0] = lavaSourceStillSprite;
        lavaSourceSpites[1] = lavaSourceFlowSprite;

        defaultLavaSourceStillSprite = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.LAVA.defaultBlockState()).getParticleIcon();
        //#if MC > 11404
        defaultLavaSourceFlowSprite = ModelBakery.LAVA_FLOW.sprite();
        //#else
        //$$ defaultLavaSourceFlowSprite =  Minecraft.getInstance().getTextureAtlas().getSprite(ModelBakery.LAVA_FLOW);
        //#endif
        defaultLavaSourceSpites[0] = defaultLavaSourceStillSprite;
        defaultLavaSourceSpites[1] = defaultLavaSourceFlowSprite;
        FluidRenderHandler lavaSourceRenderHandler = (view, pos, state) -> {
            if (view != null && pos != null && Configs.highlightLavaSource.getBooleanValue()) {
                BlockState blockState = view.getBlockState(pos);

                if (blockState.hasProperty(LiquidBlock.LEVEL) && blockState.getValue(LiquidBlock.LEVEL) == 0) {
                    return lavaSourceSpites;
                }
            }

            return defaultLavaSourceSpites;
        };
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.LAVA, lavaSourceRenderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.FLOWING_LAVA, lavaSourceRenderHandler);
    }
}
