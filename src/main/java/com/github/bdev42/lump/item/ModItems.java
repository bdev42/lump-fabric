package com.github.bdev42.lump.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import java.util.function.Function;

import static com.github.bdev42.lump.Lump.identifier;

public class ModItems {
    public static final Item AMETHYST_GOGGLES = register(identifier("amethyst_goggles"), AmethystGoggles::new, new Item.Properties()
            .stacksTo(1)
            .equippable(EquipmentSlot.HEAD)
    );

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(
                content -> content.prepend(AMETHYST_GOGGLES.asItem())
        );
    }

    public static <T extends Item> T register(Identifier identifier, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, identifier);

        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }
}
