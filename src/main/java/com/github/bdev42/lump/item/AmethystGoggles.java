package com.github.bdev42.lump.item;

import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class AmethystGoggles extends Item implements Equipment {

    public AmethystGoggles() {
        this(new Settings().maxCount(1));
    }

    public AmethystGoggles(Settings settings) {
        super(settings);
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable("item.lump.amethyst_goggles.tooltip"));
        if (options.isAdvanced()) {
            tooltip.add(Text.empty());
            tooltip.add(Text.translatable("item.lump.amethyst_goggles.tooltip2.advanced"));
            tooltip.add(Text.translatable("item.lump.amethyst_goggles.tooltip3.advanced"));
            tooltip.add(Text.translatable("item.lump.amethyst_goggles.tooltip4.advanced"));
            tooltip.add(Text.translatable("item.lump.amethyst_goggles.tooltip5.advanced"));
            tooltip.add(Text.translatable("item.lump.amethyst_goggles.tooltip6.advanced"));
        }
    }
}
