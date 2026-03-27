package com.github.bdev42.lump.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class AmethystGoggles extends Item  {

    public AmethystGoggles(net.minecraft.world.item.Item.Properties settings) {
        super(settings);
    }

    public static void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(Component.translatable("item.lump.amethyst_goggles.tooltip"));
        if (options.isAdvanced()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("item.lump.amethyst_goggles.tooltip2.advanced"));
            tooltip.add(Component.translatable("item.lump.amethyst_goggles.tooltip3.advanced"));
            tooltip.add(Component.translatable("item.lump.amethyst_goggles.tooltip4.advanced"));
            tooltip.add(Component.translatable("item.lump.amethyst_goggles.tooltip5.advanced"));
            tooltip.add(Component.translatable("item.lump.amethyst_goggles.tooltip6.advanced"));
        }
    }
}
