package cassetu.mystbornhorizons.world.gen;

import cassetu.mystbornhorizons.entity.ModEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

public class ModEntitySpawns {
    public static void addSpawns() {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.PLAINS, BiomeKeys.CHERRY_GROVE, BiomeKeys.FLOWER_FOREST),
                SpawnGroup.CREATURE, ModEntities.MANTIS, 45, 3, 6);

        SpawnRestriction.register(ModEntities.MANTIS, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.LUSH_CAVES,
                        BiomeKeys.SNOWY_TAIGA, BiomeKeys.SNOWY_BEACH, BiomeKeys.DRIPSTONE_CAVES,
                        BiomeKeys.SNOWY_SLOPES, BiomeKeys.SNOWY_SLOPES, BiomeKeys.FROZEN_OCEAN,
                        BiomeKeys.ICE_SPIKES, BiomeKeys.FROZEN_RIVER, BiomeKeys.FROZEN_OCEAN,
                        BiomeKeys.DEEP_FROZEN_OCEAN, BiomeKeys.COLD_OCEAN, BiomeKeys.FROZEN_PEAKS),
                SpawnGroup.MONSTER, ModEntities.ICESPIDER, 149, 5, 7);

        SpawnRestriction.register(ModEntities.ICESPIDER, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.WORLD_SURFACE_WG, HostileEntity::canSpawnIgnoreLightLevel);

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.LUSH_CAVES, BiomeKeys.DRIPSTONE_CAVES),
                SpawnGroup.MONSTER, ModEntities.COPPERBULB, 145, 4, 4);

        SpawnRestriction.register(ModEntities.COPPERBULB, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.WORLD_SURFACE_WG, HostileEntity::canSpawnIgnoreLightLevel);
}}