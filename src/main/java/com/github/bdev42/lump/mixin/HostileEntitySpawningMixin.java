package com.github.bdev42.lump.mixin;

import com.github.bdev42.lump.block.AmethystBeacon;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(HostileEntity.class)
public class HostileEntitySpawningMixin {

    @Inject(method = "isSpawnDark", at = @At("HEAD"), cancellable = true)
    private static void lump_isSpawnDark(ServerWorldAccess world, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        if (!world.getDimension().hasSkyLight()) return;

        if (!AmethystBeacon.hasAmethystBeaconInRange(world.toServerWorld(), pos)) return;

        int ll = world.getLightLevel(LightType.SKY, pos);
        if (ll >= 15 || ll > world.getDimension().monsterSpawnBlockLightLimit()) {
            cir.setReturnValue(false);
        }
    }
}
