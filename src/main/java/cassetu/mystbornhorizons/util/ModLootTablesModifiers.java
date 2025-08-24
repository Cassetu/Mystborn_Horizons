package cassetu.mystbornhorizons.util;

import cassetu.mystbornhorizons.block.ModBlocks;
import cassetu.mystbornhorizons.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class ModLootTablesModifiers {
    private static final Identifier ENDER_DRAGON_ID
            = Identifier.of("minecraft", "entities/ender_dragon");
    private static final Identifier CREEPER_ID
            = Identifier.of("minecraft", "entities/creeper");
    private static final Identifier VINDICATOR_ID
            = Identifier.of("minecraft", "entities/vindicator");

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registry) -> {
            if(ENDER_DRAGON_ID.equals(key.getValue())) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1.0f)) // Drops 100% of the time
                        .with(ItemEntry.builder(ModItems.ENDER_FURY_MUSIC_DISC))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }

            if(VINDICATOR_ID.equals(key.getValue())) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1.0f)) // Drops 100% of the time
                        .with(ItemEntry.builder(ModItems.TOMAHAWK))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }

            if(LootTables.SHIPWRECK_SUPPLY_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1.0f)) // Drops 100% of the time
                        .with(ItemEntry.builder(ModItems.SALMON_NIGIRI))
                        .with(ItemEntry.builder(ModItems.COD_NIGIRI))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 7.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }

            if(LootTables.FISHING_JUNK_GAMEPLAY.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.12f))
                        .with(ItemEntry.builder(ModItems.TOMAHAWK))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }

            if(LootTables.WOODLAND_MANSION_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1.0f))
                        .with(ItemEntry.builder(ModItems.TOMAHAWK))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 5.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }

            if(LootTables.DESERT_PYRAMID_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.9f))
                        .with(ItemEntry.builder(ModItems.TITAN_SANDS_MUSIC_DISC))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }

            if(LootTables.VILLAGE_DESERT_HOUSE_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.7f))
                        .with(ItemEntry.builder(ModItems.TITAN_SANDS_MUSIC_DISC))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }

            if(LootTables.HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT_GAMEPLAY.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.6f))
                        .with(ItemEntry.builder(ModItems.TOMAHAWK))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }

            if(LootTables.VILLAGE_BUTCHER_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1.75f)) // 175% of the time
                        .with(ItemEntry.builder(ModItems.CHICKEN_NUGGETS))
                        .with(ItemEntry.builder(ModItems.VEGGIE_SANDWICH))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }

            if(LootTables.END_CITY_TREASURE_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.4f)) // 40% of the time
                        .with(ItemEntry.builder(ModItems.ENDER_FURY_MUSIC_DISC))
                        .with(ItemEntry.builder(ModItems.MYSTBORN_DUST))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }

            if(LootTables.BASTION_TREASURE_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.4f)) // 40% of the time
                        .with(ItemEntry.builder(ModItems.DOOMSDAY_MUSIC_DISC))
                        .with(ItemEntry.builder(ModItems.STORMITE))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }

            if(LootTables.BASTION_BRIDGE_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.6f)) // 60% of the time
                        .with(ItemEntry.builder(ModBlocks.MOLTEN_GOLD_BASALT))
                        .with(ItemEntry.builder(ModBlocks.MOLTEN_GOLD_BLACKSTONE))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 7.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }

            if(LootTables.TRIAL_CHAMBERS_INTERSECTION_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1.0f)) // 100% of the time
                        .with(ItemEntry.builder(ModBlocks.SALONDITE_ORE))
                        .with(ItemEntry.builder(ModItems.RAW_SALONDITE))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 7.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }


            if(LootTables.STRONGHOLD_CORRIDOR_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.6f)) // 60% of the time
                        .with(ItemEntry.builder(ModItems.WAFFLE))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 9.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }

            if(LootTables.STRONGHOLD_CROSSING_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1.0f)) // 60% of the time
                        .with(ItemEntry.builder(ModItems.ROYAL_TECTONITE_SWORD))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }

            if(CREEPER_ID.equals(key.getValue())) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.75f)) // Drops 75% of the time
                        .with(ItemEntry.builder(ModItems.CAULIFLOWER))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());
                tableBuilder.pool(poolBuilder.build());
            }
        });
    }
}