package com.github.bdev42.lump.mixin;

import com.github.bdev42.lump.Lump;
import com.github.bdev42.lump.block.AmethystBeacon;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatrolEntity.class)
public class PatrolMonsterSpawningMixin {

    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    private static void lump_canSpawn(EntityType<? extends PatrolEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        if (!Lump.CONFIG.blockPatrols()) return;
        if (!(world instanceof ServerWorldAccess swa)) return;
        if (!AmethystBeacon.hasAmethystBeaconInRange(swa.toServerWorld(), pos)) return;
        cir.setReturnValue(false);
    }
}
