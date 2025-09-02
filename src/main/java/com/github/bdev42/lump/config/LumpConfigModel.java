package com.github.bdev42.lump.config;

import com.github.bdev42.lump.Lump;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = Lump.MOD_ID)
@Config(name = "lump-config", wrapperName = "LumpConfig")
public class LumpConfigModel {
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RangeConstraint(min = 1, max = 1024)
    public int beaconProtectionRadius = 128;

    @SectionHeader("light-overlay")
    // these bounds describe the margin around the player's subchunk in each direction
    // the total number of subchunks would be: (1 + 2*bound)^3 i.e. a bound of 1 means a 3x3x3 volume of subchunks
    @RangeConstraint(min = 0, max = 16)
    public int subchunksRenderMargin = 2;
    @RangeConstraint(min = 0, max = 16)
    public int subchunksCacheMargin = 3;

    @RangeConstraint(min = 1, max = 1200)
    public int ticksPerOverlayCacheUpdate = 20;
    @RangeConstraint(min = 20, max = 1200)
    public int ticksPerBeaconPositionsUpdate = 40;
}
