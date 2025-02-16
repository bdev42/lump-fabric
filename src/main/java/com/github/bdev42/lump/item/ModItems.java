package com.github.bdev42.lump.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.github.bdev42.lump.Lump.identifier;

public class ModItems {
    public static final Item AMETHYST_GOGGLES = registerItem(identifier("amethyst_goggles"), new AmethystGoggles());

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(
                content -> content.prepend(AMETHYST_GOGGLES.asItem())
        );
    }

    private static <T extends Item> T registerItem(Identifier identifier, T item) {
        return Registry.register(Registries.ITEM, identifier, item);
    }
}
