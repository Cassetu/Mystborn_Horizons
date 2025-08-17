package cassetu.mystbornhorizons.world.gen;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.entity.ModEntities;
import cassetu.mystbornhorizons.world.ModPlacedFeatures;
import cassetu.mystbornhorizons.world.biome.ModBiomes;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.OverworldBiomeCreator;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.GenerationStep;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;






public class ModBiomeGeneration {
    public static void generateBiomes() {
        MystbornHorizons.LOGGER.info("Registering biome placement for " + MystbornHorizons.MOD_ID);

        // Add features to Alpine Meadows
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(ModBiomes.ALPINE_MEADOWS),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.TECTONITE_ORE_PLACED_KEY
        );

        // Add features to Craggy Peaks
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(ModBiomes.CRAGGY_PEAKS),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.TECTONITE_ORE_PLACED_KEY
        );

        // Add features to Rocky Tundra
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(ModBiomes.ROCKY_TUNDRA),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ModPlacedFeatures.TECTONITE_ORE_PLACED_KEY
        );

        registerBiomePlacement();
    }

    private static void registerBiomePlacement() {
        MystbornHorizons.LOGGER.info("Registering biome placement in the overworld");
// Create registry keys for your biomes
        RegistryKey<Biome> alpineMeadowsKey = RegistryKey.of(RegistryKeys.BIOME,
                Identifier.of("mystbornhorizons", "alpine_meadows"));
        RegistryKey<Biome> craggyPeaksKey = RegistryKey.of(RegistryKeys.BIOME,
                Identifier.of("mystbornhorizons", "craggy_peaks"));
        RegistryKey<Biome> rockyTundraKey = RegistryKey.of(RegistryKeys.BIOME,
                Identifier.of("mystbornhorizons", "rocky_tundra"));

// For Alpine Meadows (cool biome)
        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld().and(context -> {
                    // Target COOL climate biomes
                    return context.getBiome().getTemperature() < 0.6f &&
                            context.getBiome().getTemperature() > 0.2f;
                }),
                SpawnGroup.AMBIENT,
                ModEntities.ICESPIDER,
                10, 2, 4
        );

// For Craggy Peaks (mountain biome)
        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld().and(context -> {
                    // Target mountain-like biomes
                    return context.getBiome().getTemperature() < 0.6f &&
                            context.getBiome().getTemperature() > 0.2f;
                }),
                SpawnGroup.MONSTER,
                ModEntities.ICESPIDER,
                15, 1, 3
        );

// For Rocky Tundra (snowy biome)
        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld().and(context -> {
                    // Target SNOWY climate biomes
                    return context.getBiome().getTemperature() <= 0.2f;
                }),
                SpawnGroup.CREATURE,
                ModEntities.ICESPIDER,
                8, 2, 5
        );

// Add features to your biomes
// Alpine Meadows
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(alpineMeadowsKey),
                GenerationStep.Feature.VEGETAL_DECORATION,
                ModPlacedFeatures.MYSTBORN_DUST_ORE_PLACED_KEY
        );

// Craggy Peaks
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(craggyPeaksKey),
                GenerationStep.Feature.LOCAL_MODIFICATIONS,
                ModPlacedFeatures.MYSTBORN_DUST_ORE_PLACED_KEY
        );

// Rocky Tundra
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(rockyTundraKey),
                GenerationStep.Feature.SURFACE_STRUCTURES,
                ModPlacedFeatures.MYSTBORN_DUST_ORE_PLACED_KEY
        );

        MystbornHorizons.LOGGER.info("Biome placement completed successfully");

    }}