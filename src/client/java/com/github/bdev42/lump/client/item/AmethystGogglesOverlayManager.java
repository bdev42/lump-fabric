package com.github.bdev42.lump.client.item;

import com.github.bdev42.lump.Lump;
import com.github.bdev42.lump.item.ModItems;
import com.github.bdev42.lump.networking.AmethystBeaconLocationsRequest;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.LightLayer;

import java.util.*;

public class AmethystGogglesOverlayManager {
    static final byte F_BLOCK_SPAWNABLE = 1;
    static final byte F_BLOCK_LIT = 1 << 1;
    static final byte F_SKY_LIT = 1 << 2;
    static final byte F_BEACON_IN_RANGE = 1 << 3;

    private static SectionPos prevSubchunkPos;
    private static final Set<BlockPos> knownBeaconPositions = new HashSet<>();
    private static final Map<SectionPos, byte[]> overlayCache = new HashMap<>();

    private static int tickCounter = 0;
    private static boolean overlayEnabled = false;

    public static void onClientTickEvent(ClientLevel world) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        // toggle the overlay based on if the player is wearing the goggles or not
        if(overlayEnabled != isWearingGoggles(player)) {
            overlayEnabled = !overlayEnabled;
            // on disable: clean up caches
            if (!overlayEnabled) {
                overlayCache.clear();
                knownBeaconPositions.clear();
            }
        }
        if (!overlayEnabled) return;
        tickCounter++;

        // when the player changes their subchunk we need to add all the new subchunks now in range
        // and remove the subchunks now out of range so we do not leak memory
        if (!SectionPos.of(player).equals(prevSubchunkPos)) onPlayerChunkChanged(player, world);
        // since updating the beacon positions involves a request to the server it will happen once every n ticks
        if (tickCounter % Lump.CONFIG.ticksPerBeaconPositionsUpdate() == 0) updateAmethystBeaconPositions();
        // on the other hand updating the overlay all at once would cause visible lag spikes,
        // so we need to spread out the work so that a full update completes every n ticks
        invalidateOverlayCachePartByPart(
                tickCounter, Lump.CONFIG.ticksPerOverlayCacheUpdate(),
                prevSubchunkPos, Lump.CONFIG.subchunksRenderMargin()
        );
        updateOverlayCache(world);
    }

    public static void onRenderEvent(WorldRenderContext context) {
        if (!overlayEnabled) return;

        Profiler.get().push("lumpOverlay");
        AmethystGogglesOverlayRenderer.render(context, prevSubchunkPos, overlayCache);
        Profiler.get().pop();
    }

    private static void onPlayerChunkChanged(LocalPlayer player, ClientLevel world) {
        prevSubchunkPos = SectionPos.of(player);

        updateAmethystBeaconPositions();
        updateOverlayCache(world);
    }

    public static void onUpdatedAmethystBeaconPositionsReceived(List<BlockPos> receivedPositions) {
        knownBeaconPositions.clear();
        knownBeaconPositions.addAll(receivedPositions);
    }

    private static void updateAmethystBeaconPositions() {
        // request amethyst beacon positions
        ClientPlayNetworking.send(new AmethystBeaconLocationsRequest(
                prevSubchunkPos.x(),
                prevSubchunkPos.z(),
                Lump.CONFIG.beaconProtectionRadius()/16
        ));
    }

    private static void updateOverlayCache(ClientLevel world) {
        // loop through all cached chunks, remove everything now outside the caching bounds
        overlayCache.keySet().removeIf(chunkSectionPos -> !checkSubchunkBounds(
                chunkSectionPos,
                prevSubchunkPos,
                Lump.CONFIG.subchunksCacheMargin()
        ));

        // loop through all chunks in the cache bounds, if no cached data is present generate it
        SectionPos.cube(prevSubchunkPos, Lump.CONFIG.subchunksCacheMargin()).forEach(subchunk -> {
            if (overlayCache.containsKey(subchunk)) return;

            byte[] data = new byte[16*16*16];

            int monsterSpawnLightLevel = world.dimensionType().monsterSpawnBlockLightLimit();
            boolean hasSkylight = world.dimensionType().hasSkyLight();

            for (short i = 0; i < data.length; i++) {
                BlockPos pos = subchunk.relativeToBlockPos(i);

                if (SpawnPlacementTypes.ON_GROUND.isSpawnPositionOk(world, pos, EntityType.CREEPER)) data[i] |= F_BLOCK_SPAWNABLE;

                if (world.getBrightness(LightLayer.BLOCK, pos) > monsterSpawnLightLevel) data[i] |= F_BLOCK_LIT;
                if (hasSkylight && world.getBrightness(LightLayer.SKY, pos) > monsterSpawnLightLevel) data[i] |= F_SKY_LIT;

                if (hasKnownBeaconInRange(pos, Lump.CONFIG.beaconProtectionRadius())) data[i] |= F_BEACON_IN_RANGE;
            }

            overlayCache.put(subchunk, data);
        });
    }

    private static void invalidateOverlayCachePartByPart(int part, int maxPart, SectionPos center, int bounds) {
        int size = 1 + 2*bounds;
        int per_tick = size*size*size / maxPart + 1;
        int current_tick = per_tick * (part % maxPart);
        for (int i = current_tick; i < per_tick+current_tick && i < size*size*size; i++) {
            int y = i / (size*size) - bounds;
            int z = (i / size) % size - bounds;
            int x = i % size - bounds;
            invalidateOverlayCacheAt(center.offset(x, y, z));
        }
    }

    private static void invalidateOverlayCacheAt(SectionPos subchunk) {
        overlayCache.remove(subchunk);
    }

    private static boolean isWearingGoggles(LocalPlayer player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.AMETHYST_GOGGLES);
    }

    private static boolean checkSubchunkBounds(SectionPos pos, SectionPos center, int bound) {
        int x = pos.x();
        int z = pos.z();
        int y = pos.y();

        int cx = center.x();
        int cz = center.z();
        int cy = center.y();

        return x >= cx-bound && x <= cx+bound && z >= cz-bound && z <= cz+bound && y >= cy-bound && y <= cy+bound;
    }

    private static boolean hasKnownBeaconInRange(BlockPos pos, int range) {
        double sd = range * range;
        return knownBeaconPositions.stream().anyMatch(beacon -> beacon.distSqr(
                new Vec3i(pos.getX(), beacon.getY(), pos.getZ())
        ) <= sd);
    }
}
