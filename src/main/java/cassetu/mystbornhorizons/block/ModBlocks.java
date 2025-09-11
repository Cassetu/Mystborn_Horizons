package cassetu.mystbornhorizons.block;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.block.custom.HoneyBerryBushBlock;
import cassetu.mystbornhorizons.block.custom.ShardBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class ModBlocks {

    public static final Block FROSTSTONE_BLOCK = registerBlock("froststone_block",
            new Block(AbstractBlock.Settings.create().strength(2.7f).requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block MYST_BRICKS = registerBlock("myst_bricks",
            new Block(AbstractBlock.Settings.create().strength(2.4f).requiresTool()
                    .sounds(BlockSoundGroup.TUFF_BRICKS)));

    public static final Block MYST_CRATE = registerBlock("myst_crate",
            new Block(AbstractBlock.Settings.create().strength(2.4f).requiresTool()
                    .sounds(BlockSoundGroup.POLISHED_TUFF)));

    public static final Block MOLTEN_GOLD_BLACKSTONE = registerBlock("molten_gold_blackstone",
            new Block(AbstractBlock.Settings.create().strength(3.7f).requiresTool()
                    .sounds(BlockSoundGroup.GILDED_BLACKSTONE)));

    public static final Block MOLTEN_GOLD_BASALT = registerBlock("molten_gold_basalt",
            new Block(AbstractBlock.Settings.create().strength(3.7f).requiresTool()
                    .sounds(BlockSoundGroup.BASALT)));

    public static final Block PACKED_ICE_BRICKS = registerBlock("packed_ice_bricks",
            new Block(AbstractBlock.Settings.create().strength(3f).requiresTool()
                    .sounds(BlockSoundGroup.COPPER)));
    public static final Block CARVED_ICE_BRICKS = registerBlock("carved_ice_bricks",
            new Block(AbstractBlock.Settings.create().strength(4f).requiresTool()
                    .sounds(BlockSoundGroup.COPPER)));

    public static final Block DUNGEON_ROOTMASS = registerBlock("dungeon_rootmass",
            new Block(AbstractBlock.Settings.create().strength(35f).requiresTool().burnable()
                    .sounds(BlockSoundGroup.NETHER_WOOD)));

    public static final Block DUNGEON_COBBLE = registerBlock("dungeon_cobble",
            new Block(AbstractBlock.Settings.create()
                    .strength(-1.0f, 3600000.0f)
                    .dropsNothing()
                    .allowsSpawning(Blocks::never)
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block FROSTSTONE_ORE = registerBlock("froststone_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),
                    AbstractBlock.Settings.create().requiresTool().strength(3f).sounds(BlockSoundGroup.STONE)));

    public static final Block STORMITE_ORE = registerBlock("stormite_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),
                    AbstractBlock.Settings.create().requiresTool().strength(3f).sounds(BlockSoundGroup.NETHERRACK)));

    public static final Block SALONDITE_ORE = registerBlock("salondite_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 7),
                    AbstractBlock.Settings.create().requiresTool().strength(5f).sounds(BlockSoundGroup.STONE)));


    public static final Block MYSTBORN_DUST_ORE = registerBlock("mystborn_dust_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(10, 15),
                    AbstractBlock.Settings.create().luminance(state -> 2).requiresTool().strength(2f).sounds(BlockSoundGroup.STONE)));

    public static final Block FROSTSTONE_DEEPSLATE_ORE = registerBlock("froststone_deepslate_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),
                    AbstractBlock.Settings.create().requiresTool().strength(4f).sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block LUMINITE_END_ORE = registerBlock("luminite_end_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),
                    AbstractBlock.Settings.create().requiresTool().strength(5f).luminance(state -> 15).sounds(BlockSoundGroup.CALCITE)));

    public static final Block STORMITE_DEEPSLATE_ORE = registerBlock("stormite_deepslate_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),
                    AbstractBlock.Settings.create().requiresTool().strength(4f).sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block MOONSTONE_DEEPSLATE_ORE = registerBlock("moonstone_deepslate_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(25, 55),
                    AbstractBlock.Settings.create().requiresTool().strength(5f).sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block TECTONITE_DEEPSLATE_ORE = registerBlock("tectonite_deepslate_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),
                    AbstractBlock.Settings.create().requiresTool().strength(4f).sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block SHARD_BLOCK = registerBlock("shard_block",
            new ShardBlock(AbstractBlock.Settings.create().strength(2f)
                    .luminance(state -> 7).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block HONEY_BERRY_BUSH = registerBlockWithoutBlockItem("honey_berry_bush",
            new HoneyBerryBushBlock(AbstractBlock.Settings.copy(Blocks.SWEET_BERRY_BUSH)));

    private static Block registerBlockWithoutBlockItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(MystbornHorizons.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(MystbornHorizons.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(MystbornHorizons.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        MystbornHorizons.LOGGER.info("Registering Mod Blocks for " + MystbornHorizons.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(ModBlocks.FROSTSTONE_BLOCK);
        });
    }
}
