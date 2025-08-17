package cassetu.mystbornhorizons.world.biome;

import cassetu.mystbornhorizons.MystbornHorizons;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class ModBiomes {
    // Define ResourceKeys for your biomes
    public static final RegistryKey<Biome> ALPINE_MEADOWS = RegistryKey.of(
            RegistryKeys.BIOME, Identifier.of(MystbornHorizons.MOD_ID, "alpine_meadows"));

    public static final RegistryKey<Biome> CRAGGY_PEAKS = RegistryKey.of(
            RegistryKeys.BIOME, Identifier.of(MystbornHorizons.MOD_ID, "craggy_peaks"));

    public static final RegistryKey<Biome> ROCKY_TUNDRA = RegistryKey.of(
            RegistryKeys.BIOME, Identifier.of(MystbornHorizons.MOD_ID, "rocky_tundra"));

    public static void registerBiomes() {
        MystbornHorizons.LOGGER.info("Registering biomes for " + MystbornHorizons.MOD_ID);

        // We don't actually need to register the biome if it's defined in JSON
        // The following line would be used if you want to register a biome programmatically
        // Registry.register(Registries.BIOME, ALPINE_MEADOWS.getValue(), createAlpineMeadowsBiome());
    }

    // This method would be used for programmatic biome creation
    // Not necessary if you're defining biomes through JSON
    private static Biome createAlpineMeadowsBiome() {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();
        return new Biome.Builder()
                .temperature(0.5f)
                .downfall(0.8f)
                .precipitation(true)
                .effects(new BiomeEffects.Builder()
                        .skyColor(8103167)
                        .fogColor(12638463)
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .grassColor(8037887)
                        .foliageColor(7318014)
                        .moodSound(BiomeMoodSound.CAVE)
                        .build())
                .spawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .build();
    }
}
