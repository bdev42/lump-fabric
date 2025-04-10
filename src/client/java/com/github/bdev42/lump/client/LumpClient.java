package com.github.bdev42.lump.client;

import com.github.bdev42.lump.block.AmethystBeacon;
import com.github.bdev42.lump.block.ModBlocks;
import com.github.bdev42.lump.client.item.AmethystGogglesOverlayManager;
import com.github.bdev42.lump.client.networking.AmethystBeaconLocationsResponseHandler;
import com.github.bdev42.lump.item.AmethystGoggles;
import com.github.bdev42.lump.item.ModItems;
import com.github.bdev42.lump.networking.AmethystBeaconLocationsResponse;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class LumpClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, options, tooltip) -> {
            if (stack.isOf(ModItems.AMETHYST_GOGGLES)) {
                AmethystGoggles.appendTooltip(stack, context, tooltip, options);
            } else if (stack.isOf(ModBlocks.AMETHYST_BEACON.asItem())) {
                AmethystBeacon.appendTooltip(stack, context, tooltip, options);
            }
        });

        ClientTickEvents.END_WORLD_TICK.register(AmethystGogglesOverlayManager::onClientTickEvent);
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(AmethystGogglesOverlayManager::onRenderEvent);

        ClientPlayNetworking.registerGlobalReceiver(
                AmethystBeaconLocationsResponse.PACKET_ID,
                AmethystBeaconLocationsResponseHandler::onResponse
        );
    }
}
