package com.github.bdev42.lump.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import static com.github.bdev42.lump.Lump.identifier;

public class ModItems {
    public static final Item AMETHYST_GOGGLES = registerItem(identifier("amethyst_goggles"), AmethystGoggles::new, new Item.Settings()
            .maxCount(1)
            .equippable(EquipmentSlot.HEAD)
    );

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(
                content -> content.prepend(AMETHYST_GOGGLES.asItem())
        );
    }

    private static Item registerItem(Identifier identifier, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, identifier);
        return Items.register(registryKey, factory, settings);
    }
}
