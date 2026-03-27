package com.github.bdev42.lump.block;

import com.github.bdev42.lump.Lump;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.List;

import static com.github.bdev42.lump.Lump.identifier;

public class AmethystBeacon extends Block {

    public AmethystBeacon(Properties settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.4375, 0.9375, 0.4375, 0.5625, 1, 0.5625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.375, 0.8125, 0.375, 0.625, 0.9375, 0.625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.3125, 0.6875, 0.3125, 0.6875, 0.8125, 0.6875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.3125, 0.25, 0.75, 0.6875, 0.75), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.3125, 0.1875, 0.3125, 0.6875, 0.3125, 0.6875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.375, 0.0625, 0.375, 0.625, 0.1875, 0.625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.4375, 0, 0.4375, 0.5625, 0.0625, 0.5625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.4375, 0.1875, 0.75, 0.5625, 0.25), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.4375, 0.75, 0.75, 0.5625, 0.8125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0.4375, 0.25, 0.25, 0.5625, 0.75), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.75, 0.4375, 0.25, 0.8125, 0.5625, 0.75), BooleanOp.OR);

        return shape;
    }

    public static void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(Component.translatable(
                "block.lump.amethyst_beacon.tooltip" + (options.isAdvanced() ? ".advanced" : ""), Lump.CONFIG.beaconProtectionRadius()
        ));
    }

    public static boolean hasAmethystBeaconInRange(ServerLevel world, BlockPos pos) {
        int radius = Lump.CONFIG.beaconProtectionRadius();
        double sd = radius * radius;
        return world.getPoiManager()
                .getInSquare(
                        poiType -> poiType.is(identifier("amethyst_beacon")),
                        pos,
                        radius,
                        PoiManager.Occupancy.ANY
                ).anyMatch(poi -> poi.getPos().distSqr(
                        new Vec3i(pos.getX(), poi.getPos().getY(), pos.getZ())
                ) <= sd);
    }
}
