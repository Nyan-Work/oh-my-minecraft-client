package com.plusls.ommc.impl.generic.highlightWaypoint;

import com.plusls.ommc.SharedConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import java.util.function.Function;

//#if MC < 11903
//$$ import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
//$$ import net.minecraft.client.renderer.texture.TextureAtlas;
//#endif

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HighlightWaypointResourceLoader implements SimpleSynchronousResourceReloadListener {
    private static final HighlightWaypointResourceLoader instance = new HighlightWaypointResourceLoader();
    private static final ResourceLocation listenerId = SharedConstants.identifier("target_reload_listener");
    public static final ResourceLocation targetId = SharedConstants.identifier("block/target");

    public static TextureAtlasSprite targetIdSprite;

    protected static void init() {
        //#if MC < 11903
        //$$ ClientSpriteRegistryCallback.event(TextureAtlas.LOCATION_BLOCKS).register(
        //$$         (atlasTexture, registry) -> registry.register(HighlightWaypointResourceLoader.targetId)
        //$$ );
        //#endif
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(HighlightWaypointResourceLoader.instance);
    }

    @Override
    public ResourceLocation getFabricId() {
        return HighlightWaypointResourceLoader.listenerId;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        //#if MC > 11404
        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        HighlightWaypointResourceLoader.targetIdSprite = atlas.apply(HighlightWaypointResourceLoader.targetId);
        //#else
        //$$ targetIdSprite = Minecraft.getInstance().getTextureAtlas().getSprite(HighlightWaypointResourceLoader.targetId);
        //#endif
    }
}
