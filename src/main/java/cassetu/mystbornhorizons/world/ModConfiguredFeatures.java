package cassetu.mystbornhorizons.world;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.block.ModBlocks;
import cassetu.mystbornhorizons.block.custom.HoneyBerryBushBlock;
import cassetu.mystbornhorizons.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>>  FROSTSTONE_ORE_KEY = registerKey("froststone_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>>  TECTONITE_ORE_KEY = registerKey("tectonite_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>>  MOONSTONE_ORE_KEY = registerKey("moonstone_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>>  LUMINITE_END_ORE_KEY = registerKey("luminite_end_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>>  STORMITE_ORE_KEY = registerKey("stormite_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>>  MYSTBORN_DUST_ORE_KEY = registerKey("mystborn_dust_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>>  MOLTEN_GOLD_BASALT_KEY = registerKey("molten_gold_basalt_key");

    public static final RegistryKey<ConfiguredFeature<?, ?>>  HONEY_BERRY_BUSH_KEY = registerKey("honey_berry_bush_ore");


    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherReplaceables = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
        RuleTest basaltReplaceables = new TagMatchRuleTest(ModTags.Blocks.BASALT);
        RuleTest blackstoneReplaceables = new TagMatchRuleTest(ModTags.Blocks.BLACKSTONE);
        RuleTest endReplaceables = new BlockMatchRuleTest(Blocks.END_STONE);

        List<OreFeatureConfig.Target> overworldFroststone_Ores =
                List.of(OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.FROSTSTONE_ORE.getDefaultState()),
                        OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.FROSTSTONE_DEEPSLATE_ORE.getDefaultState()));
        List<OreFeatureConfig.Target> overworldTectonite_Ores =
                List.of(OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.TECTONITE_DEEPSLATE_ORE.getDefaultState()));
        List<OreFeatureConfig.Target> overworldMoonstone_Ores =
                List.of(OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.MOONSTONE_DEEPSLATE_ORE.getDefaultState()));
        List<OreFeatureConfig.Target> endLuminite_Ores =
                List.of(OreFeatureConfig.createTarget(endReplaceables, ModBlocks.LUMINITE_END_ORE.getDefaultState()));
        List<OreFeatureConfig.Target> netherStormite_Ores =
                List.of(OreFeatureConfig.createTarget(netherReplaceables, ModBlocks.STORMITE_ORE.getDefaultState()));
        List<OreFeatureConfig.Target> overworldMystborn_Dust_Ores =
                List.of(OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.MYSTBORN_DUST_ORE.getDefaultState()));

        List<OreFeatureConfig.Target> basaltReplaceables_Ores =
                List.of(OreFeatureConfig.createTarget(netherReplaceables, ModBlocks.MOLTEN_GOLD_BASALT.getDefaultState()));

                register(context, FROSTSTONE_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldFroststone_Ores, 5));
        register(context, TECTONITE_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldTectonite_Ores, 5));
        register(context, MOONSTONE_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldMoonstone_Ores, 7));
        register(context, STORMITE_ORE_KEY, Feature.ORE, new OreFeatureConfig(netherStormite_Ores, 5));
        register(context, LUMINITE_END_ORE_KEY, Feature.ORE, new OreFeatureConfig(endLuminite_Ores, 5));
        register(context, MYSTBORN_DUST_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldMystborn_Dust_Ores, 6));

        register(context, MOLTEN_GOLD_BASALT_KEY, Feature.ORE, new OreFeatureConfig(basaltReplaceables_Ores, 14));


        register(context, HONEY_BERRY_BUSH_KEY, Feature.RANDOM_PATCH,
                ConfiguredFeatures.createRandomPatchFeatureConfig(
                        Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig(BlockStateProvider.of(ModBlocks.HONEY_BERRY_BUSH
                                .getDefaultState().with(HoneyBerryBushBlock.AGE, 3))),
                        List.of(Blocks.GRASS_BLOCK)));

    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(MystbornHorizons.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}