package net.nerdorg.minehop.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.nerdorg.minehop.MinehopAddon;

public class ModBlocks {
    public static final Block BOOSTER_BLOCK = registerBlock("boost_pad",
        new BoostBlock(Block.Settings.copy(Blocks.BEDROCK)
            .slipperiness(0.8F)
            .nonOpaque()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(MinehopAddon.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MinehopAddon.MOD_ID, name));
        return Items.register(registryKey, settings -> new BlockItem(block, settings), new Item.Settings());
    }

    public static void registerModBlocks() {
        MinehopAddon.LOGGER.info("Registering ModBlocks for " + MinehopAddon.MOD_ID);
    }
}
