package com.github.bdev42.lump.client.item;

import com.github.bdev42.lump.Lump;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

import java.util.Map;

import static com.github.bdev42.lump.client.item.AmethystGogglesOverlayManager.*;

public class AmethystGogglesOverlayRenderer {
    private static final int CLR_ALWAYS_SAFE = 0xFF00C000;
    private static final int CLR_LUMP_SAFE = 0xFFC000C0;
    private static final int CLR_DAYTIME_SAFE = 0xFFFFFF00;
    private static final int CLR_NEVER_SAFE = 0xFFFF0000;

    private static int getColorFromData(byte data) {
        if ((data & F_BLOCK_LIT) == F_BLOCK_LIT) return CLR_ALWAYS_SAFE;

        if ((data & F_SKY_LIT) == F_SKY_LIT) {
            if ((data & F_BEACON_IN_RANGE) == F_BEACON_IN_RANGE) return CLR_LUMP_SAFE;

            return CLR_DAYTIME_SAFE;
        }

        return CLR_NEVER_SAFE;
    }

    public static void render(WorldRenderContext context, ChunkSectionPos playerSubchunkPos, Map<ChunkSectionPos, byte[]> overlayCache) {
        if (context.worldState() == null) return;
        Vec3d cam = context.worldState().cameraRenderState.pos;

        // for each subchunk inside the render bounds, loop through every block and draw overlays where necessary
        ChunkSectionPos.stream(playerSubchunkPos, Lump.CONFIG.subchunksRenderMargin()).forEach(subchunk -> {
            byte[] data = overlayCache.get(subchunk);
            if (data == null) return;

            for (short i = 0; i < data.length; i++) {
                if ((data[i] & F_BLOCK_SPAWNABLE) == 0) continue;

                BlockPos pos = subchunk.unpackBlockPos(i);
                if (cam.y < pos.getY()) continue;

                drawOverlay(pos, getColorFromData(data[i]));
            }
        });
    }

    private static void drawOverlay(BlockPos pos, int colorARGB) {
        GizmoDrawing.line(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), new Vec3d(pos.getX()+1, pos.getY(), pos.getZ()+1), colorARGB, 1);
        GizmoDrawing.line(new Vec3d(pos.getX(), pos.getY(), pos.getZ()+1), new Vec3d(pos.getX()+1, pos.getY(), pos.getZ()), colorARGB, 1);
    }
}
