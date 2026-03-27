package com.github.bdev42.lump.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.function.Function;

import static com.github.bdev42.lump.Lump.identifier;

public class ModItems {
    public static final Item AMETHYST_GOGGLES = registerItem(identifier("amethyst_goggles"), AmethystGoggles::new, new Item.Properties()
            .stacksTo(1)
            .equippable(EquipmentSlot.HEAD)
    );

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(
                content -> content.prepend(AMETHYST_GOGGLES.asItem())
        );
    }

    private static Item registerItem(Identifier identifier, Function<Item.Properties, Item> factory, Item.Properties settings) {
        final ResourceKey<Item> registryKey = ResourceKey.create(Registries.ITEM, identifier);
        return Items.registerItem(registryKey, factory, settings);
    }
}
