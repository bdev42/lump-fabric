package com.github.bdev42.lump.client.item;

import com.github.bdev42.lump.Lump;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.phys.Vec3;
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

    public static void render(WorldRenderContext context, SectionPos playerSubchunkPos, Map<SectionPos, byte[]> overlayCache) {
        if (context.worldState() == null) return;
        Vec3 cam = context.worldState().cameraRenderState.pos;

        // for each subchunk inside the render bounds, loop through every block and draw overlays where necessary
        SectionPos.cube(playerSubchunkPos, Lump.CONFIG.subchunksRenderMargin()).forEach(subchunk -> {
            byte[] data = overlayCache.get(subchunk);
            if (data == null) return;

            for (short i = 0; i < data.length; i++) {
                if ((data[i] & F_BLOCK_SPAWNABLE) == 0) continue;

                BlockPos pos = subchunk.relativeToBlockPos(i);
                if (cam.y < pos.getY()) continue;

                drawOverlay(pos, getColorFromData(data[i]));
            }
        });
    }

    private static void drawOverlay(BlockPos pos, int colorARGB) {
        Gizmos.line(new Vec3(pos.getX(), pos.getY(), pos.getZ()), new Vec3(pos.getX()+1, pos.getY(), pos.getZ()+1), colorARGB, 1);
        Gizmos.line(new Vec3(pos.getX(), pos.getY(), pos.getZ()+1), new Vec3(pos.getX()+1, pos.getY(), pos.getZ()), colorARGB, 1);
    }
}
