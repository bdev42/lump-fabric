package com.github.bdev42.lump.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.ChunkPos;

import static com.github.bdev42.lump.Lump.identifier;

public class AmethystBeaconLocationsRequestHandler {
    public static void onRequest(AmethystBeaconLocationsRequest request, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            var poistore = context.player().level().getPoiManager();

            var beacons = ChunkPos.rangeClosed(new ChunkPos(request.chunkX(), request.chunkZ()), request.radius())
                    .flatMap(chunkPos -> poistore.getInChunk(
                            poiType -> poiType.is(identifier("amethyst_beacon")),
                            chunkPos,
                            PoiManager.Occupancy.ANY
                    ))
                    .map(PoiRecord::getPos)
                    .toList();

            context.responseSender().sendPacket(new AmethystBeaconLocationsResponse(beacons));
        });
    }
}
