package com.github.bdev42.lump.mixin;

import com.github.bdev42.lump.Lump;
import com.github.bdev42.lump.block.AmethystBeacon;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatrollingMonster.class)
public class PatrollingMonsterSpawningMixin {

    @Inject(method = "checkPatrollingMonsterSpawnRules", at = @At("HEAD"), cancellable = true)
    private static void lump_checkPatrollingMonsterSpawnRules(final EntityType<? extends PatrollingMonster> type, final LevelAccessor level, final EntitySpawnReason spawnReason, final BlockPos pos, final RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        if (!Lump.CONFIG.blockPatrols() || spawnReason != EntitySpawnReason.PATROL) return;
        if (!(level instanceof ServerLevelAccessor swa)) return;
        if (!AmethystBeacon.hasAmethystBeaconInRange(swa.getLevel(), pos)) return;
        cir.setReturnValue(false);
    }
}
