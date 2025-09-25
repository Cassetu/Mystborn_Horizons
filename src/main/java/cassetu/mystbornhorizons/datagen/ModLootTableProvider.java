package cassetu.mystbornhorizons.datagen;

import cassetu.mystbornhorizons.block.ModBlocks;
import cassetu.mystbornhorizons.block.custom.HoneyBerryBushBlock;
import cassetu.mystbornhorizons.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

        addDrop(ModBlocks.FROSTSTONE_BLOCK);
        addDrop(ModBlocks.SHARD_BLOCK);
        addDrop(ModBlocks.MYST_BRICKS);
        addDrop(ModBlocks.MYST_CRATE);
        addDrop(ModBlocks.PACKED_ICE_BRICKS);
        addDrop(ModBlocks.MOLTEN_GOLD_BLACKSTONE);
        addDrop(ModBlocks.MOLTEN_GOLD_BASALT);
        addDrop(ModBlocks.DUNGEON_ROOTMASS, LootTable.builder().pool(LootPool.builder().conditionally(
                        BlockStatePropertyLootCondition.builder(ModBlocks.DUNGEON_ROOTMASS).properties(StatePredicate.Builder.create()))
                .with(ItemEntry.builder(ModItems.ROOT))
                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 5.0F)))
                .apply(ApplyBonusLootFunction.uniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE)))));

        addDrop(ModBlocks.FROSTSTONE_ORE, oreDrops(ModBlocks.FROSTSTONE_ORE, ModItems.RAW_FROSTSTONE));
        addDrop(ModBlocks.FROSTSTONE_DEEPSLATE_ORE, multipleOreDrops(ModBlocks.FROSTSTONE_DEEPSLATE_ORE, ModItems.RAW_FROSTSTONE, 4, 8));
        addDrop(ModBlocks.STORMITE_ORE, oreDrops(ModBlocks.STORMITE_ORE, ModItems.RAW_STORMITE));
        addDrop(ModBlocks.STORMITE_DEEPSLATE_ORE, multipleOreDrops(ModBlocks.STORMITE_DEEPSLATE_ORE, ModItems.RAW_STORMITE, 4, 8));
        addDrop(ModBlocks.LUMINITE_END_ORE, multipleOreDrops(ModBlocks.LUMINITE_END_ORE, ModItems.LUMINITE, 1, 2));
        addDrop(ModBlocks.TECTONITE_DEEPSLATE_ORE, multipleOreDrops(ModBlocks.TECTONITE_DEEPSLATE_ORE, ModItems.TECTONITE_GEODE, 1, 2));
        addDrop(ModBlocks.MOONSTONE_DEEPSLATE_ORE, multipleOreDrops(ModBlocks.MOONSTONE_DEEPSLATE_ORE, ModItems.MOONSTONE, 1, 2));
        addDrop(ModBlocks.SALONDITE_ORE, multipleOreDrops(ModBlocks.SALONDITE_ORE, ModItems.RAW_SALONDITE, 1, 2));
        addDrop(ModBlocks.MYSTBORN_DUST_ORE, multipleOreDrops(ModBlocks.MYSTBORN_DUST_ORE, ModItems.MYSTBORN_DUST, 1, 3));

        this.addDrop(ModBlocks.HONEY_BERRY_BUSH,
                block -> this.applyExplosionDecay(
                        block,LootTable.builder().pool(LootPool.builder().conditionally(
                                                BlockStatePropertyLootCondition.builder(ModBlocks.HONEY_BERRY_BUSH).properties(StatePredicate.Builder.create().exactMatch(HoneyBerryBushBlock.AGE, 3))
                                        )
                                        .with(ItemEntry.builder(ModItems.HONEY_BERRIES))
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 3.0F)))
                                        .apply(ApplyBonusLootFunction.uniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE)))
                        ).pool(LootPool.builder().conditionally(
                                        BlockStatePropertyLootCondition.builder(ModBlocks.HONEY_BERRY_BUSH).properties(StatePredicate.Builder.create().exactMatch(HoneyBerryBushBlock.AGE, 2))
                                ).with(ItemEntry.builder(ModItems.HONEY_BERRIES))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F)))
                                .apply(ApplyBonusLootFunction.uniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE))))));

    }

    public LootTable.Builder multipleOreDrops(Block drop, Item item, float minDrops, float maxDrops) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.dropsWithSilkTouch(drop, this.applyExplosionDecay(drop, ((LeafEntry.Builder<?>)
                ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(minDrops, maxDrops))))
                .apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))));
    }
}
