package com.github.bdev42.lump;

import com.github.bdev42.lump.block.ModBlocks;
import com.github.bdev42.lump.config.LumpConfig;
import com.github.bdev42.lump.item.ModItems;
import com.github.bdev42.lump.networking.AmethystBeaconLocationsRequestHandler;
import com.github.bdev42.lump.networking.AmethystBeaconLocationsRequest;
import com.github.bdev42.lump.networking.AmethystBeaconLocationsResponse;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class Lump implements ModInitializer {
    public static final String MOD_ID = "lump";

    public static Identifier identifier(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static final LumpConfig CONFIG = LumpConfig.createAndLoad();

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        ModItems.initialize();

        PayloadTypeRegistry.playC2S().register(AmethystBeaconLocationsRequest.PACKET_ID, AmethystBeaconLocationsRequest.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(AmethystBeaconLocationsResponse.PACKET_ID, AmethystBeaconLocationsResponse.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(
                AmethystBeaconLocationsRequest.PACKET_ID,
                AmethystBeaconLocationsRequestHandler::onRequest
        );
    }
}
