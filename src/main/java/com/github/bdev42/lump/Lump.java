package com.github.bdev42.lump;

import com.github.bdev42.lump.block.AmethystBeacon;
import com.github.bdev42.lump.block.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.poi.PointOfInterestStorage;

public class Lump implements ModInitializer {
    public static final String MOD_ID = "lump";

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.addBefore(Items.TORCH, ModBlocks.AMETHYST_BEACON.asItem());
        });
    }

    public static Identifier identifier(String path) {
        return Identifier.of(MOD_ID, path);
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
