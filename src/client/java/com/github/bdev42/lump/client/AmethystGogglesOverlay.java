package com.github.bdev42.lump.client;

import com.github.bdev42.lump.item.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class AmethystGogglesOverlay {
    // these bounds describe the margin around the player's subchunk in each direction
    // the total number of subchunks would be: (1 + 2*bound)^3 i.e. a bound of 1 means a 3x3x3 volume of subchunks
    private static final int SUBCHUNK_BOUNDS_RENDER = 1;
    private static final int SUBCHUNK_BOUNDS_CACHE = SUBCHUNK_BOUNDS_RENDER+1;
    private static final short SUBCHUNK_SIZE = 16*16*16;

    private static final byte F_BLOCK_SPAWNABLE = 1;
    private static final byte F_BLOCK_LIT = 1 << 1;
    private static final byte F_SKY_LIT = 1 << 2;
    private static final byte F_BEACON_IN_RANGE = 1 << 3;

    private static final int CLR_ALWAYS_SAFE = 0xFF00C000;
    private static final int CLR_LUMP_SAFE = 0xFFC000C0;
    private static final int CLR_DAYTIME_SAFE = 0xFFFFFF00;
    private static final int CLR_NEVER_SAFE = 0xFFFF0000;


    private static ChunkSectionPos prevSubchunkPos;
    private static final Map<ChunkSectionPos, byte[]> overlayCache = new HashMap<>();

    public static void renderEvent(WorldRenderContext context) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (!player.getEquippedStack(EquipmentSlot.HEAD).isOf(ModItems.AMETHYST_GOGGLES)) return;

        context.profiler().push("lumpOverlay");
        if (!ChunkSectionPos.from(player).equals(prevSubchunkPos)) onPlayerChunkChanged(player, context.world());

        render(context, prevSubchunkPos);
        context.profiler().pop();
    }

    private static void onPlayerChunkChanged(ClientPlayerEntity player, ClientWorld world) {
        prevSubchunkPos = ChunkSectionPos.from(player);
        System.out.println("Switched to subchunk: " + prevSubchunkPos);

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

    private static boolean checkSubchunkBounds(ChunkSectionPos pos, ChunkSectionPos center, int bound) {
        int x = pos.getSectionX();
        int z = pos.getSectionZ();
        int y = pos.getSectionY();

        int cx = center.getSectionX();
        int cz = center.getSectionZ();
        int cy = center.getSectionY();

        return x >= cx-bound && x <= cx+bound && z >= cz-bound && z <= cz+bound && y >= cy-bound && y <= cy+bound;
    }

    private static void render(WorldRenderContext ctx, ChunkSectionPos playerSubchunkPos) {
        ctx.matrixStack().push();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buff = tess.getBuffer();

        Matrix4f translationMatrix = ctx.matrixStack().peek().getPositionMatrix();
        Vec3d cam = ctx.camera().getPos();
        translationMatrix.translate((float) -cam.x, (float) -cam.y + 0.005f, (float) -cam.z);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        buff.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        // for each subchunk inside the render bounds, loop through every block and draw overlays where necessary
        ChunkSectionPos.stream(playerSubchunkPos, SUBCHUNK_BOUNDS_RENDER).forEach(subchunk -> {
            byte[] data = overlayCache.get(subchunk);
            if (data == null) return;

            for (short i = 0; i < data.length; i++) {
                if ((data[i] & F_BLOCK_SPAWNABLE) == 0) continue;

                BlockPos pos = subchunk.unpackBlockPos(i);
                if (ctx.camera().getPos().y < pos.getY()) continue;

                drawOverlay(buff, translationMatrix, pos, getColorFromData(data[i]));
            }
        });

        tess.draw();
        ctx.matrixStack().pop();
    }

    private static int getColorFromData(byte data) {
        if ((data & F_BLOCK_LIT) == F_BLOCK_LIT) return CLR_ALWAYS_SAFE;

        if ((data & F_SKY_LIT) == F_SKY_LIT) {
            if ((data & F_BEACON_IN_RANGE) == F_BEACON_IN_RANGE) return CLR_LUMP_SAFE;

            return CLR_DAYTIME_SAFE;
        }

        return CLR_NEVER_SAFE;
    }

    private static void drawOverlay(BufferBuilder buff, Matrix4f matrix, BlockPos pos, int colorARGB) {
        buff.vertex(matrix, pos.getX(), pos.getY(), pos.getZ()).color(colorARGB).next();
        buff.vertex(matrix, pos.getX()+1, pos.getY(), pos.getZ()+1).color(colorARGB).next();
        buff.vertex(matrix, pos.getX(), pos.getY(), pos.getZ()+1).color(colorARGB).next();
        buff.vertex(matrix, pos.getX()+1, pos.getY(), pos.getZ()).color(colorARGB).next();
    }
}
