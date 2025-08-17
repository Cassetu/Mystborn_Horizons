package cassetu.mystbornhorizons.world;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.world.biome.ModBiomes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.biome.Biome;

import java.util.concurrent.CompletableFuture;

public class ModBiomeProvider extends FabricDynamicRegistryProvider {

    public ModBiomeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        // For Alpine Meadows biome
        entries.add(ModBiomes.ALPINE_MEADOWS,
                registries.getWrapperOrThrow(RegistryKeys.BIOME)
                        .getOrThrow(ModBiomes.ALPINE_MEADOWS).value());

        // For Craggy Peaks biome
        entries.add(ModBiomes.CRAGGY_PEAKS,
                registries.getWrapperOrThrow(RegistryKeys.BIOME)
                        .getOrThrow(ModBiomes.CRAGGY_PEAKS).value());

        // For Rocky Tundra biome
        entries.add(ModBiomes.ROCKY_TUNDRA,
                registries.getWrapperOrThrow(RegistryKeys.BIOME)
                        .getOrThrow(ModBiomes.ROCKY_TUNDRA).value());
    }
    @Override
    public String getName() {
        return MystbornHorizons.MOD_ID + ":biomes";
    }
}