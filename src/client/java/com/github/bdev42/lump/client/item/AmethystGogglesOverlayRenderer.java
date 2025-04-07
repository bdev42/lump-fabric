package com.github.bdev42.lump.client.item;

import com.github.bdev42.lump.Lump;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

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

    public static void render(WorldRenderContext ctx, ChunkSectionPos playerSubchunkPos, Map<ChunkSectionPos, byte[]> overlayCache) {
        MatrixStack matrixStack = ctx.matrixStack();
        if (matrixStack == null) return;

        matrixStack.push();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buff = tess.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        Matrix4f translationMatrix = matrixStack.peek().getPositionMatrix();
        Vec3d cam = ctx.camera().getPos();
        translationMatrix.translate((float) -cam.x, (float) -cam.y + 0.005f, (float) -cam.z);

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();

        // for each subchunk inside the render bounds, loop through every block and draw overlays where necessary
        ChunkSectionPos.stream(playerSubchunkPos, Lump.CONFIG.subchunksRenderMargin()).forEach(subchunk -> {
            byte[] data = overlayCache.get(subchunk);
            if (data == null) return;

            for (short i = 0; i < data.length; i++) {
                if ((data[i] & F_BLOCK_SPAWNABLE) == 0) continue;

                BlockPos pos = subchunk.unpackBlockPos(i);
                if (ctx.camera().getPos().y < pos.getY()) continue;

                drawOverlay(buff, translationMatrix, pos, getColorFromData(data[i]));
            }
        });

        var builtBuff = buff.endNullable();
        if (builtBuff != null) BufferRenderer.drawWithGlobalProgram(builtBuff);
        matrixStack.pop();
    }

    private static void drawOverlay(BufferBuilder buff, Matrix4f matrix, BlockPos pos, int colorARGB) {
        buff.vertex(matrix, pos.getX(), pos.getY(), pos.getZ()).color(colorARGB);
        buff.vertex(matrix, pos.getX()+1, pos.getY(), pos.getZ()+1).color(colorARGB);
        buff.vertex(matrix, pos.getX(), pos.getY(), pos.getZ()+1).color(colorARGB);
        buff.vertex(matrix, pos.getX()+1, pos.getY(), pos.getZ()).color(colorARGB);
    }
}
