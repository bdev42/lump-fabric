package com.github.bdev42.lump.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import static com.github.bdev42.lump.Lump.identifier;

@SuppressWarnings({"SameParameterValue", "UnusedReturnValue"})
public class ModBlocks {
    public static final Block AMETHYST_BEACON = registerWithPOI(identifier("amethyst_beacon"), AmethystBeacon::new, Block.Settings.create()
            .strength(3f)
            .requiresTool()
            .mapColor(MapColor.PURPLE)
            .sounds(BlockSoundGroup.AMETHYST_BLOCK)
            .emissiveLighting(Blocks::always)
            .luminance(ignored -> 3)
            , 0, AmethystBeacon.MAX_PROTECTION_DISTANCE
    );

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(
                content -> content.addBefore(Items.TORCH, AMETHYST_BEACON.asItem())
        );
    }

    private static Block registerBlock(Identifier identifier, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        Items.register(block);
        return block;
    }

    private static Block registerWithPOI(Identifier identifier, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings, int tc, int searchDistance) {
        final Block block = registerBlock(identifier, factory, settings);
        PointOfInterestHelper.register(identifier, tc, searchDistance, block);
        return block;
    }
}
