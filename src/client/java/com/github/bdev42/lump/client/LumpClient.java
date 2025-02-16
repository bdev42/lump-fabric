package com.github.bdev42.lump.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class LumpClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(AmethystGogglesOverlay::renderEvent);
    }
}
