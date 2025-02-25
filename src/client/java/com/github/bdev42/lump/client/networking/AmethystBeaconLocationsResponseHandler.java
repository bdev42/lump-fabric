package com.github.bdev42.lump.client.networking;

import com.github.bdev42.lump.client.item.AmethystGogglesOverlayManager;
import com.github.bdev42.lump.networking.AmethystBeaconLocationsResponse;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class AmethystBeaconLocationsResponseHandler {
    public static void onResponse(AmethystBeaconLocationsResponse response, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            AmethystGogglesOverlayManager.onUpdatedAmethystBeaconPositionsReceived(
                    response.positions(),
                    context.player().clientWorld
            );
        });
    }
}
