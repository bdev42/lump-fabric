package com.github.bdev42.lump.client;

import com.github.bdev42.lump.item.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;

import java.util.HashMap;
import java.util.Map;

public class AmethystGogglesOverlayManager {
    // these bounds describe the margin around the player's subchunk in each direction
    // the total number of subchunks would be: (1 + 2*bound)^3 i.e. a bound of 1 means a 3x3x3 volume of subchunks
    static final int SUBCHUNK_BOUNDS_RENDER = 1;
    static final int SUBCHUNK_BOUNDS_CACHE = SUBCHUNK_BOUNDS_RENDER+1;
    static final short SUBCHUNK_SIZE = 16*16*16;

    static final byte F_BLOCK_SPAWNABLE = 1;
    static final byte F_BLOCK_LIT = 1 << 1;
    static final byte F_SKY_LIT = 1 << 2;
    static final byte F_BEACON_IN_RANGE = 1 << 3;

    private static ChunkSectionPos prevSubchunkPos;
    private static final Map<ChunkSectionPos, byte[]> overlayCache = new HashMap<>();


    public static void onRenderEvent(WorldRenderContext context) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || !isWearingGoggles(player)) return;

        context.profiler().push("lumpOverlay");
        if (!ChunkSectionPos.from(player).equals(prevSubchunkPos)) onPlayerChunkChanged(player, context.world());

        AmethystGogglesOverlayRenderer.render(context, prevSubchunkPos, overlayCache);
        context.profiler().pop();
    }

    public static void onPlayerChunkChanged(ClientPlayerEntity player, ClientWorld world) {
        prevSubchunkPos = ChunkSectionPos.from(player);

        updateOverlayCache(world);
    }

    private static void updateOverlayCache(ClientWorld world) {
        // loop through all cached chunks, remove everything now outside the caching bounds
        overlayCache.keySet().removeIf(chunkSectionPos -> !checkSubchunkBounds(
                chunkSectionPos,
                prevSubchunkPos,
                SUBCHUNK_BOUNDS_CACHE
        ));

        // loop through all chunks in the cache bounds, if no cached data is present generate it
        ChunkSectionPos.stream(prevSubchunkPos, SUBCHUNK_BOUNDS_CACHE).forEach(subchunk -> {
            if (overlayCache.containsKey(subchunk)) return;

            byte[] data = new byte[SUBCHUNK_SIZE];

            int monsterSpawnLightLevel = world.getDimension().monsterSpawnBlockLightLimit();
            boolean hasSkylight = world.getDimension().hasSkyLight();

            for (short i = 0; i < data.length; i++) {
                BlockPos pos = subchunk.unpackBlockPos(i);

                if (SpawnLocationTypes.ON_GROUND.isSpawnPositionOk(world, pos, EntityType.CREEPER)) data[i] |= F_BLOCK_SPAWNABLE;

                if (world.getLightLevel(LightType.BLOCK, pos) > monsterSpawnLightLevel) data[i] |= F_BLOCK_LIT;
                if (hasSkylight && world.getLightLevel(LightType.SKY, pos) > monsterSpawnLightLevel) data[i] |= F_SKY_LIT;

                //TODO: get amethyst beacon positions from server
            }

            overlayCache.put(subchunk, data);
        });
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
}
