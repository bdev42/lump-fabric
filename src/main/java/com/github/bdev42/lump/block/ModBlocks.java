package com.github.bdev42.lump.block;

import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.github.bdev42.lump.Lump.identifier;

@SuppressWarnings({"SameParameterValue", "UnusedReturnValue"})
public class ModBlocks {
    public static final Block AMETHYST_BEACON = registerWithPOI(identifier("amethyst_beacon"), new AmethystBeacon(), 0, AmethystBeacon.MAX_PROTECTION_DISTANCE);

    public static void initialize() {
    }

    private static <T extends Block> T registerBlock(Identifier identifier, T block) {
        Registry.register(Registries.BLOCK, identifier, block);
        Registry.register(Registries.ITEM, identifier, new BlockItem(block, new Item.Settings()));
        return block;
    }

    private static <T extends Block> T registerWithPOI(Identifier identifier, T block, int tc, int searchDistance) {
        registerBlock(identifier, block);
        PointOfInterestHelper.register(identifier, tc, searchDistance, block);
        return block;
    }
}
