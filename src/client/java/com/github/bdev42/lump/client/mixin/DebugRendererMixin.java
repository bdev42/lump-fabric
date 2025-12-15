package com.github.bdev42.lump.client.mixin;

import com.github.bdev42.lump.client.item.AmethystGogglesOverlayManager;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void renderLightOverlay(MatrixStack matrices, Frustum frustum, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, boolean lateDebug, CallbackInfo ci) {
        Vec3d cameraPos = new Vec3d(cameraX, cameraY, cameraZ);
        AmethystGogglesOverlayManager.onRenderEvent(matrices, cameraPos);
    }
}
