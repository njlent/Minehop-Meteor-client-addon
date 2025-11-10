package net.nerdorg.minehop.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.nerdorg.minehop.MinehopAddon;
import net.nerdorg.minehop.block.ModBlocks;
import net.nerdorg.minehop.item.custom.BoundsStickItem;
import net.nerdorg.minehop.item.custom.InstagibItem;

import java.util.function.Function;

public class ModItems {
    public static final Item BOUNDS_STICK = register("bounds_stick", BoundsStickItem::new, new Item.Settings());
    public static final Item INSTAGIB_GUN = register("instagib_gun", InstagibItem::new, new Item.Settings());

    private static void addItemsToOperatorTabItemGroup(FabricItemGroupEntries entries) {
        entries.add(BOUNDS_STICK);
        entries.add(ModBlocks.BOOSTER_BLOCK.asItem());
    }

    private static void addItemsToCombatTabItemGroup(FabricItemGroupEntries entries) {
        entries.add(INSTAGIB_GUN);
    }

    private static Item register(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MinehopAddon.MOD_ID, path));
        return Items.register(registryKey, factory, settings);
    }

    public static void registerModItems() {
        MinehopAddon.LOGGER.info("Registering Mod Items for " + MinehopAddon.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR).register(ModItems::addItemsToOperatorTabItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addItemsToCombatTabItemGroup);
    }
}
