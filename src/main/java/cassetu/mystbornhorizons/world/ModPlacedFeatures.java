package cassetu.mystbornhorizons.world;

import cassetu.mystbornhorizons.MystbornHorizons;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final RegistryKey<PlacedFeature> FROSTSTONE_ORE_PLACED_KEY = registerKey("froststone_ore_placed_");
    public static final RegistryKey<PlacedFeature> MOONSTONE_ORE_PLACED_KEY = registerKey("moonstone_ore_placed_");
    public static final RegistryKey<PlacedFeature> TECTONITE_ORE_PLACED_KEY = registerKey("tectonite_ore_placed_");
    public static final RegistryKey<PlacedFeature> END_LUMINITE_ORE_PLACED_KEY = registerKey("end_luminite_ore_placed_");
    public static final RegistryKey<PlacedFeature> STORMITE_ORE_PLACED_KEY = registerKey("stormite_ore_placed_");
    public static final RegistryKey<PlacedFeature> MYSTBORN_DUST_ORE_PLACED_KEY = registerKey("mystborn_dust_ore_placed_");

    public static final RegistryKey<PlacedFeature> HONEY_BERRY_BUSH_PLACED_KEY = registerKey("honey_berry_bush_placed_");
    public static final RegistryKey<PlacedFeature> SALONDITE_ORE_PLACED_KEY = registerKey("salondite_ore_placed_");

    public static final RegistryKey<PlacedFeature> MOLTEN_GOLD_BASALT_PLACED_KEY = registerKey("molten_gold_basalt_placed_");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatures = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, FROSTSTONE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.FROSTSTONE_ORE_KEY),
                ModOrePlacement.modifiersWithCount(14,
                        HeightRangePlacementModifier.trapezoid(YOffset.fixed(-80), YOffset.fixed(80))));
        register(context, MYSTBORN_DUST_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MYSTBORN_DUST_ORE_KEY),
                ModOrePlacement.modifiersWithCount(14,
                        HeightRangePlacementModifier.trapezoid(YOffset.fixed(-80), YOffset.fixed(80))));
        register(context, TECTONITE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.TECTONITE_ORE_KEY),
                ModOrePlacement.modifiersWithCount(14,
                        HeightRangePlacementModifier.trapezoid(YOffset.fixed(-80), YOffset.fixed(80))));
        register(context, MOONSTONE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MOONSTONE_ORE_KEY),
                ModOrePlacement.modifiersWithCount(16,
                        HeightRangePlacementModifier.trapezoid(YOffset.fixed(-80), YOffset.fixed(-15))));
        register(context, STORMITE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.STORMITE_ORE_KEY),
                ModOrePlacement.modifiersWithCount(14,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(80), YOffset.fixed(127))));
        register(context, END_LUMINITE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.LUMINITE_END_ORE_KEY),
                ModOrePlacement.modifiersWithCount(14,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(80))));
        register(context, MOLTEN_GOLD_BASALT_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MOLTEN_GOLD_BASALT_KEY),
                ModOrePlacement.modifiersWithCount(38,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(80))));
        register(context, SALONDITE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SALONDITE_ORE_KEY),
                ModOrePlacement.modifiersWithCount(14,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(200))));
        register(context, HONEY_BERRY_BUSH_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.HONEY_BERRY_BUSH_KEY),
                RarityFilterPlacementModifier.of(28), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of());
    }
    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(MystbornHorizons.MOD_ID, name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                                                                   RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                                                                   PlacementModifier... modifiers) {
        register(context, key, configuration, List.of(modifiers));
    }
}