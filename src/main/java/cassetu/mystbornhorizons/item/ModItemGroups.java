package cassetu.mystbornhorizons.item;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup MYSTBORN_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(MystbornHorizons.MOD_ID, "mystbornhorizons_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.MYSTBORN_DUST))
                    .displayName(Text.translatable("itemgroup.mystbornhorizons.mystbornhorizons_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.DOOMSDAY_MUSIC_DISC);
                        entries.add(ModItems.ECHOES_OF_THE_ABYSS_MUSIC_DISC);
                        entries.add(ModItems.TITAN_SANDS_MUSIC_DISC);
                        entries.add(ModItems.ENDER_FURY_MUSIC_DISC);
                        entries.add(ModItems.WATER_HORIZONS_MUSIC_DISC);
                        entries.add(ModItems.FOREST_HAVEN_MUSIC_DISC);
                        entries.add(ModItems.PEACEKEEPER_SWORD);
                        entries.add(ModItems.VANQUISHER_SWORD);
                        entries.add(ModItems.MOONSTONE_RAPIER);
                        entries.add(ModItems.FOREST_HEART);
                        entries.add(ModItems.CURSE_ESSENCE);

                        entries.add(ModItems.MANTIS_SPAWN_EGG);
                        entries.add(ModItems.COPPER_BULB_SPAWN_EGG);
                        entries.add(ModItems.ICE_SPIDER_SPAWN_EGG);
                        entries.add(ModItems.BASALT_HOWLER_SPAWN_EGG);
                        entries.add(ModItems.HAVENICA_SPAWN_EGG);

//                      entries.add(ModItems.K_ARMOR_TRIM_SMITHING_TEMPLATE);

                        entries.add(ModItems.FROSTSTONE_PICKAXE);
                        entries.add(ModItems.FROSTSTONE_AXE);
                        entries.add(ModItems.FROSTSTONE_HOE);
                        entries.add(ModItems.FROSTSTONE_SHOVEL);
                        entries.add(ModItems.FROSTSTONE_SWORD);
                        entries.add(ModItems.ROYAL_FROSTSTONE_SWORD);
                        entries.add(ModItems.ROYAL_TECTONITE_SWORD);

                        entries.add(ModItems.FROSTSTONE_HELMET);
                        entries.add(ModItems.FROSTSTONE_CHESTPLATE);
                        entries.add(ModItems.FROSTSTONE_LEGGINGS);
                        entries.add(ModItems.FROSTSTONE_BOOTS);

                        entries.add(ModItems.FOREST_HAVEN_DISC_FRAGMENT);
                        entries.add(ModItems.TOMAHAWK);
                        entries.add(ModItems.AXE_HEAD);
                    }).build());

    public static final ItemGroup MYSTBORN_FOODS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(MystbornHorizons.MOD_ID, "mystbornhorizons_foods"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.CAULIFLOWER))
                    .displayName(Text.translatable("itemgroup.mystbornhorizons.mystbornhorizons_foods"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.CAULIFLOWER);
                        entries.add(ModItems.SALMON_NIGIRI);
                        entries.add(ModItems.COD_NIGIRI);
                        entries.add(ModItems.WAFFLE);
                        entries.add(ModItems.CHICKEN_NUGGETS);
                        entries.add(ModItems.VEGGIE_SANDWICH);
                        entries.add(ModItems.HONEY_BERRIES);
                        entries.add(ModItems.POWER_CORE);
                        entries.add(ModItems.ROOT);
                    }).build());
    public static final ItemGroup MYSTBORN_DECOR_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(MystbornHorizons.MOD_ID, "mystbornhorizons_decor"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.MYST_CRATE))
                    .displayName(Text.translatable("itemgroup.mystbornhorizons.mystbornhorizons_decor"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.SHARD_BLOCK);
                        entries.add(ModBlocks.ANCIENT_GROVE_ALTAR);
                        entries.add(ModBlocks.FROSTSTONE_BLOCK);
                        entries.add(ModBlocks.MYST_BRICKS);
                        entries.add(ModBlocks.MYST_CRATE);
                        entries.add(ModBlocks.PACKED_ICE_BRICKS);
                        entries.add(ModBlocks.CARVED_ICE_BRICKS);
                        entries.add(ModBlocks.MOLTEN_GOLD_BLACKSTONE);
                        entries.add(ModBlocks.MOLTEN_GOLD_BASALT);
                        entries.add(ModBlocks.DUNGEON_ROOTMASS);
                        entries.add(ModBlocks.DUNGEON_COBBLE);

                    }).build());

    public static final ItemGroup MYSTBORN_ORES_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(MystbornHorizons.MOD_ID, "mystbornhorizons_ores"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.MYSTBORN_DUST_ORE))
                    .displayName(Text.translatable("itemgroup.mystbornhorizons.mystbornhorizons_ores"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.STORMITE_ORE);
                        entries.add(ModBlocks.MYSTBORN_DUST_ORE);
                        entries.add(ModBlocks.STORMITE_DEEPSLATE_ORE);
                        entries.add(ModBlocks.TECTONITE_DEEPSLATE_ORE);
                        entries.add(ModBlocks.LUMINITE_END_ORE);
                        entries.add(ModItems.LUMINITE);
                        entries.add(ModItems.STORMITE);
                        entries.add(ModItems.MOONSTONE);
                        entries.add(ModItems.MYSTBORN_DUST);
                        entries.add(ModItems.SALONDITE);
                        entries.add(ModItems.RAW_SALONDITE);
                        entries.add(ModItems.RAW_FROSTSTONE);
                        entries.add(ModItems.FROSTSTONE);
                        entries.add(ModBlocks.SALONDITE_ORE);
                        entries.add(ModBlocks.FROSTSTONE_ORE);
                        entries.add(ModBlocks.MOONSTONE_DEEPSLATE_ORE);
                        entries.add(ModBlocks.FROSTSTONE_DEEPSLATE_ORE);
                        entries.add(ModItems.RAW_STORMITE);
                        entries.add(ModItems.TECTONITE_GEODE);
                    }).build());
    public static void registerItemGroups() {
        MystbornHorizons.LOGGER.info("Registering Item Groups for " + MystbornHorizons.MOD_ID);
    }
}
