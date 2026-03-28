package com.github.bdev42.lump.block;

import com.github.bdev42.lump.Lump;
import com.github.bdev42.lump.item.ModItems;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.github.bdev42.lump.Lump.identifier;


public class ModBlocks {
    public static final Block AMETHYST_BEACON = registerWithPOI(identifier("amethyst_beacon"), AmethystBeacon::new, BlockBehaviour.Properties.of()
            .strength(3f)
            .requiresCorrectToolForDrops()
            .mapColor(MapColor.COLOR_PURPLE)
            .sound(SoundType.AMETHYST)
            .emissiveRendering(Blocks::always)
            .lightLevel(ignored -> 3)
            , 0, Lump.CONFIG.beaconProtectionRadius()
    );

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(
                content -> content.insertBefore(Items.TORCH, AMETHYST_BEACON.asItem())
        );
    }

    private static Block registerBlock(Identifier identifier, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings) {
        final ResourceKey<Block> registryKey = ResourceKey.create(Registries.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        final BiFunction<Block, Item.Properties, Item> itemFactory = BlockItem::new;
        final Item.Properties properties = new Item.Properties();

        ModItems.register(
                identifier,
                (p) -> (Item)itemFactory.apply(block, p),
                properties.useBlockDescriptionPrefix().requiredFeatures(block.requiredFeatures())
        );
        return block;
    }

    private static Block registerWithPOI(Identifier identifier, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings, int tc, int searchDistance) {
        final Block block = registerBlock(identifier, factory, settings);
        PoiHelper.register(identifier, tc, searchDistance, block);
        return block;
    }
}
