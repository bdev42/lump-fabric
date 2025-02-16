package com.github.bdev42.lump;

import com.github.bdev42.lump.block.AmethystBeacon;
import com.github.bdev42.lump.block.ModBlocks;
import com.github.bdev42.lump.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.poi.PointOfInterestStorage;

public class Lump implements ModInitializer {
    public static final String MOD_ID = "lump";

    public static Identifier identifier(String path) {
        return Identifier.of(MOD_ID, path);
    }


    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        ModItems.initialize();
    }

    public static boolean hasAmethystBeaconInRange(ServerWorld world, BlockPos pos) {
        int radius = AmethystBeacon.MAX_PROTECTION_DISTANCE;
        double sd = radius * radius;
        return world.getPointOfInterestStorage()
                .getInSquare(
                        poiType -> poiType.matchesId(identifier("amethyst_beacon")),
                        pos,
                        radius,
                        PointOfInterestStorage.OccupationStatus.ANY
                ).anyMatch(poi -> poi.getPos().getSquaredDistance(
                        new Vec3i(pos.getX(), poi.getPos().getY(), pos.getZ())
                ) <= sd);
    }
}
