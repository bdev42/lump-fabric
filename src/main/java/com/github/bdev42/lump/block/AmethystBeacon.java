package com.github.bdev42.lump.block;

import net.minecraft.block.*;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.List;

public class AmethystBeacon extends Block {
    public static final int MAX_PROTECTION_DISTANCE = 128;

    public AmethystBeacon() {
        this(AbstractBlock.Settings.create()
                .strength(3f)
                .requiresTool()
                .mapColor(MapColor.PURPLE)
                .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                .emissiveLighting(Blocks::always)
                .luminance(ignored -> 3)
        );
    }

    public AmethystBeacon(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 1, 0.75);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable(
                "block.lump.amethyst_beacon.tooltip" + (options.isAdvanced() ? ".advanced" : ""), MAX_PROTECTION_DISTANCE
        ));
    }
}
