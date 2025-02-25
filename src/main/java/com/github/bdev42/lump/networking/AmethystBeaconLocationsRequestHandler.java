package com.github.bdev42.lump.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;

import static com.github.bdev42.lump.Lump.identifier;

public class AmethystBeaconLocationsRequestHandler {
    public static void onRequest(AmethystBeaconLocationsRequest request, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            var poistore = context.player().getServerWorld().getPointOfInterestStorage();

            var beacons = ChunkPos.stream(new ChunkPos(request.chunkX(), request.chunkZ()), request.radius())
                    .flatMap(chunkPos -> poistore.getInChunk(
                            poiType -> poiType.matchesId(identifier("amethyst_beacon")),
                            chunkPos,
                            PointOfInterestStorage.OccupationStatus.ANY
                    ))
                    .map(PointOfInterest::getPos)
                    .toList();

            context.responseSender().sendPacket(new AmethystBeaconLocationsResponse(beacons));
        });
    }
}
