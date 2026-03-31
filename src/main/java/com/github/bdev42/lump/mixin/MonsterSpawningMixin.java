package com.github.bdev42.lump.mixin;

import com.github.bdev42.lump.block.AmethystBeacon;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Monster.class)
public class MonsterSpawningMixin {

    @Inject(method = "isDarkEnoughToSpawn", at = @At("HEAD"), cancellable = true)
    private static void lump_isSpawnDark(ServerLevelAccessor level, BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        if (!level.dimensionType().hasSkyLight()) return;

        if (!AmethystBeacon.hasAmethystBeaconInRange(level.getLevel(), pos)) return;

        int ll = level.getBrightness(LightLayer.SKY, pos);
        if (ll >= 15 || ll > level.dimensionType().monsterSpawnBlockLightLimit()) {
            cir.setReturnValue(false);
        }
    }
}
