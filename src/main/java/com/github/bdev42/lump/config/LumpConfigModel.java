package com.github.bdev42.lump.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "lump-config")
public class LumpConfigModel implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    int beaconProtectionRadius = 128;

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    LightOverlay lightOverlay = new LightOverlay();

    static class LightOverlay {
        // these bounds describe the margin around the player's subchunk in each direction
        // the total number of subchunks would be: (1 + 2*bound)^3 i.e. a bound of 1 means a 3x3x3 volume of subchunks
        @ConfigEntry.Gui.Tooltip
        int subchunksRenderMargin = 2;
        @ConfigEntry.Gui.Tooltip
        int subchunksCacheMargin = 3;
        @ConfigEntry.Gui.Tooltip
        int ticksPerOverlayCacheUpdate = 20;
        @ConfigEntry.Gui.Tooltip
        int ticksPerBeaconPositionsUpdate = 40;
    }

    private static int boundedInt(int val, int min, int max) {
        return Math.max(Math.min(val, max), min);
    }

    public int beaconProtectionRadius() {
        return boundedInt(beaconProtectionRadius, 1, 1024);
    }

    public int subchunksRenderMargin() {
        return boundedInt(lightOverlay.subchunksRenderMargin, 0, 16);
    }

    public int subchunksCacheMargin() {
        return boundedInt(lightOverlay.subchunksCacheMargin, 0, 16);
    }

    public int ticksPerOverlayCacheUpdate() {
        return boundedInt(lightOverlay.ticksPerOverlayCacheUpdate, 1, 1200);
    }

    public int ticksPerBeaconPositionsUpdate() {
        return boundedInt(lightOverlay.ticksPerBeaconPositionsUpdate, 20, 1200);
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        beaconProtectionRadius = beaconProtectionRadius();
        lightOverlay.subchunksRenderMargin = subchunksRenderMargin();
        lightOverlay.subchunksCacheMargin = subchunksCacheMargin();
        lightOverlay.ticksPerOverlayCacheUpdate = ticksPerOverlayCacheUpdate();
        lightOverlay.ticksPerBeaconPositionsUpdate = ticksPerBeaconPositionsUpdate();
    }
}
