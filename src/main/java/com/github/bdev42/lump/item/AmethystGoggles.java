package com.github.bdev42.lump.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;

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
}
