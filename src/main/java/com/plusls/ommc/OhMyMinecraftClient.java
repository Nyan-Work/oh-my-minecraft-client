package com.plusls.ommc;

import com.plusls.ommc.impl.feature.highlightLavaSource.LavaSourceResourceLoader;
import com.plusls.ommc.impl.feature.preventWastageOfWater.PreventWastageOfWaterHelper;
import com.plusls.ommc.impl.feature.realSneaking.RealSneakingEventHelper;
import com.plusls.ommc.game.Configs;
import com.plusls.ommc.impl.generic.highlightWaypoint.HighlightWaypointHandler;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;

public class OhMyMinecraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(() ->
                ConfigManager.getInstance().registerConfigHandler(SharedConstants.getModIdentifier(),
                        SharedConstants.getConfigHandler()));
        Configs.init();
        LavaSourceResourceLoader.init();
        HighlightWaypointHandler.init();
        RealSneakingEventHelper.init();
        PreventWastageOfWaterHelper.init();
    }
}

