package com.github.bdev42.lump.block;

import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.poi.PointOfInterestStorage;

import java.util.List;

import static com.github.bdev42.lump.Lump.identifier;

public class AmethystBeacon extends Block {
    public static final int MAX_PROTECTION_DISTANCE = 128;

    public AmethystBeacon(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.4375, 0.9375, 0.4375, 0.5625, 1, 0.5625), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.375, 0.8125, 0.375, 0.625, 0.9375, 0.625), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.3125, 0.6875, 0.3125, 0.6875, 0.8125, 0.6875), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.25, 0.3125, 0.25, 0.75, 0.6875, 0.75), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.3125, 0.1875, 0.3125, 0.6875, 0.3125, 0.6875), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.375, 0.0625, 0.375, 0.625, 0.1875, 0.625), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.4375, 0, 0.4375, 0.5625, 0.0625, 0.5625), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.25, 0.4375, 0.1875, 0.75, 0.5625, 0.25), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.25, 0.4375, 0.75, 0.75, 0.5625, 0.8125), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.1875, 0.4375, 0.25, 0.25, 0.5625, 0.75), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.75, 0.4375, 0.25, 0.8125, 0.5625, 0.75), BooleanBiFunction.OR);

        return shape;
    }

    public static void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable(
                "block.lump.amethyst_beacon.tooltip" + (options.isAdvanced() ? ".advanced" : ""), MAX_PROTECTION_DISTANCE
        ));
    }

    public static boolean hasAmethystBeaconInRange(ServerWorld world, BlockPos pos) {
        int radius = MAX_PROTECTION_DISTANCE;
        double sd = radius * radius;
        return world.getPointOfInterestStorage()
                .getInSquare(
                        poiType -> poiType.matchesId(identifier("amethyst_beacon")),
                        pos,
                        radius,
                        PointOfInterestStorage.OccupationStatus.ANY
                ).anyMatch(poi -> poi.getPos().getSquaredDistance(
                        new Vec3i(pos.getX(), poi.getPos().getY(), pos.getZ())
                ) <= sd);
    }
}
