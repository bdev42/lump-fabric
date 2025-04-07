package com.github.bdev42.lump.client.item;

import com.github.bdev42.lump.Lump;
import com.github.bdev42.lump.item.ModItems;
import com.github.bdev42.lump.networking.AmethystBeaconLocationsRequest;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.LightType;

import java.util.*;

public class AmethystGogglesOverlayManager {
    static final byte F_BLOCK_SPAWNABLE = 1;
    static final byte F_BLOCK_LIT = 1 << 1;
    static final byte F_SKY_LIT = 1 << 2;
    static final byte F_BEACON_IN_RANGE = 1 << 3;

    private static ChunkSectionPos prevSubchunkPos;
    private static final Set<BlockPos> knownBeaconPositions = new HashSet<>();
    private static final Map<ChunkSectionPos, byte[]> overlayCache = new HashMap<>();

    private static int tickCounter = 0;
    private static boolean overlayEnabled = false;

    public static void onClientTickEvent(ClientWorld world) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
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
        if (!ChunkSectionPos.from(player).equals(prevSubchunkPos)) onPlayerChunkChanged(player, world);
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

        Profilers.get().push("lumpOverlay");
        AmethystGogglesOverlayRenderer.render(context, prevSubchunkPos, overlayCache);
        Profilers.get().pop();
    }

    private static void onPlayerChunkChanged(ClientPlayerEntity player, ClientWorld world) {
        prevSubchunkPos = ChunkSectionPos.from(player);

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
                prevSubchunkPos.getSectionX(),
                prevSubchunkPos.getSectionZ(),
                Lump.CONFIG.beaconProtectionRadius()/16
        ));
    }

    private static void updateOverlayCache(ClientWorld world) {
        // loop through all cached chunks, remove everything now outside the caching bounds
        overlayCache.keySet().removeIf(chunkSectionPos -> !checkSubchunkBounds(
                chunkSectionPos,
                prevSubchunkPos,
                Lump.CONFIG.subchunksCacheMargin()
        ));

        // loop through all chunks in the cache bounds, if no cached data is present generate it
        ChunkSectionPos.stream(prevSubchunkPos, Lump.CONFIG.subchunksCacheMargin()).forEach(subchunk -> {
            if (overlayCache.containsKey(subchunk)) return;

            byte[] data = new byte[16*16*16];

            int monsterSpawnLightLevel = world.getDimension().monsterSpawnBlockLightLimit();
            boolean hasSkylight = world.getDimension().hasSkyLight();

            for (short i = 0; i < data.length; i++) {
                BlockPos pos = subchunk.unpackBlockPos(i);

                if (SpawnLocationTypes.ON_GROUND.isSpawnPositionOk(world, pos, EntityType.CREEPER)) data[i] |= F_BLOCK_SPAWNABLE;

                if (world.getLightLevel(LightType.BLOCK, pos) > monsterSpawnLightLevel) data[i] |= F_BLOCK_LIT;
                if (hasSkylight && world.getLightLevel(LightType.SKY, pos) > monsterSpawnLightLevel) data[i] |= F_SKY_LIT;

                if (hasKnownBeaconInRange(pos, Lump.CONFIG.beaconProtectionRadius())) data[i] |= F_BEACON_IN_RANGE;
            }

            overlayCache.put(subchunk, data);
        });
    }

    private static void invalidateOverlayCachePartByPart(int part, int maxPart, ChunkSectionPos center, int bounds) {
        int size = 1 + 2*bounds;
        int per_tick = size*size*size / maxPart + 1;
        int current_tick = per_tick * (part % maxPart);
        for (int i = current_tick; i < per_tick+current_tick && i < size*size*size; i++) {
            int y = i / (size*size) - bounds;
            int z = (i / size) % size - bounds;
            int x = i % size - bounds;
            invalidateOverlayCacheAt(center.add(x, y, z));
        }
    }

    private static void invalidateOverlayCacheAt(ChunkSectionPos subchunk) {
        overlayCache.remove(subchunk);
    }

    private static boolean isWearingGoggles(ClientPlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).isOf(ModItems.AMETHYST_GOGGLES);
    }

    private static boolean checkSubchunkBounds(ChunkSectionPos pos, ChunkSectionPos center, int bound) {
        int x = pos.getSectionX();
        int z = pos.getSectionZ();
        int y = pos.getSectionY();

        int cx = center.getSectionX();
        int cz = center.getSectionZ();
        int cy = center.getSectionY();

        return x >= cx-bound && x <= cx+bound && z >= cz-bound && z <= cz+bound && y >= cy-bound && y <= cy+bound;
    }

    private static boolean hasKnownBeaconInRange(BlockPos pos, int range) {
        double sd = range * range;
        return knownBeaconPositions.stream().anyMatch(beacon -> beacon.getSquaredDistance(
                new Vec3i(pos.getX(), beacon.getY(), pos.getZ())
        ) <= sd);
    }
}
