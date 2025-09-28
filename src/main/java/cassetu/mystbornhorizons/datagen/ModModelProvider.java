package cassetu.mystbornhorizons.datagen;

import cassetu.mystbornhorizons.block.ModBlocks;
import cassetu.mystbornhorizons.block.custom.BasaltSpawnerBlock;
import cassetu.mystbornhorizons.block.custom.HoneyBerryBushBlock;
import cassetu.mystbornhorizons.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.ArmorItem;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }
    /*
        * Claude.ai was used to generate code for custom side textures for basalt spawner.
     */
    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.FROSTSTONE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MYST_BRICKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MYST_CRATE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.PACKED_ICE_BRICKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CARVED_ICE_BRICKS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MOLTEN_GOLD_BLACKSTONE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MOLTEN_GOLD_BASALT);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SALONDITE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.FROSTSTONE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.FROSTSTONE_DEEPSLATE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.STORMITE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.STORMITE_DEEPSLATE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LUMINITE_END_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TECTONITE_DEEPSLATE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MOONSTONE_DEEPSLATE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SHARD_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_ROOTMASS);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ANCIENT_GROVE_ALTAR);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ASTRAL_CRYSTAL);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CONSTELLATION_FRAGMENT);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_COBBLE);

        registerBasaltSpawner(blockStateModelGenerator);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MYSTBORN_DUST_ORE);
        blockStateModelGenerator.registerTintableCrossBlockStateWithStages(ModBlocks.HONEY_BERRY_BUSH, BlockStateModelGenerator.TintType.NOT_TINTED,
                HoneyBerryBushBlock.AGE, 0, 1, 2, 3);
    }

    private void registerBasaltSpawner(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap inactiveTextures = new TextureMap()
                .put(TextureKey.UP, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_top_inactive"))
                .put(TextureKey.DOWN, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_bottom"))
                .put(TextureKey.NORTH, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_inactive"))
                .put(TextureKey.SOUTH, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_inactive"))
                .put(TextureKey.EAST, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_inactive"))
                .put(TextureKey.WEST, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_inactive"))
                .put(TextureKey.PARTICLE, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_inactive"));

        TextureMap activeWave1Textures = new TextureMap()
                .put(TextureKey.UP, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_top_active_wave1"))
                .put(TextureKey.DOWN, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_bottom"))
                .put(TextureKey.NORTH, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave1"))
                .put(TextureKey.SOUTH, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave1"))
                .put(TextureKey.EAST, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave1"))
                .put(TextureKey.WEST, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave1"))
                .put(TextureKey.PARTICLE, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave1"));

        TextureMap activeWave2Textures = new TextureMap()
                .put(TextureKey.UP, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_top_active_wave2"))
                .put(TextureKey.DOWN, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_bottom"))
                .put(TextureKey.NORTH, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave2"))
                .put(TextureKey.SOUTH, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave2"))
                .put(TextureKey.EAST, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave2"))
                .put(TextureKey.WEST, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave2"))
                .put(TextureKey.PARTICLE, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave2"));

        TextureMap activeWave3Textures = new TextureMap()
                .put(TextureKey.UP, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_top_active_wave3"))
                .put(TextureKey.DOWN, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_bottom"))
                .put(TextureKey.NORTH, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave3"))
                .put(TextureKey.SOUTH, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave3"))
                .put(TextureKey.EAST, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave3"))
                .put(TextureKey.WEST, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave3"))
                .put(TextureKey.PARTICLE, TextureMap.getSubId(ModBlocks.BASALT_SPAWNER, "_side_active_wave3"));

        Identifier inactiveModelId = Models.CUBE.upload(ModBlocks.BASALT_SPAWNER, "_inactive", inactiveTextures, blockStateModelGenerator.modelCollector);
        Identifier activeWave1ModelId = Models.CUBE.upload(ModBlocks.BASALT_SPAWNER, "_active_wave1", activeWave1Textures, blockStateModelGenerator.modelCollector);
        Identifier activeWave2ModelId = Models.CUBE.upload(ModBlocks.BASALT_SPAWNER, "_active_wave2", activeWave2Textures, blockStateModelGenerator.modelCollector);
        Identifier activeWave3ModelId = Models.CUBE.upload(ModBlocks.BASALT_SPAWNER, "_active_wave3", activeWave3Textures, blockStateModelGenerator.modelCollector);

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.BASALT_SPAWNER)
                .coordinate(BlockStateVariantMap.create(BasaltSpawnerBlock.ACTIVE, BasaltSpawnerBlock.WAVE)
                        .register(false, 1, BlockStateVariant.create().put(VariantSettings.MODEL, inactiveModelId))
                        .register(false, 2, BlockStateVariant.create().put(VariantSettings.MODEL, inactiveModelId))
                        .register(false, 3, BlockStateVariant.create().put(VariantSettings.MODEL, inactiveModelId))
                        .register(true, 1, BlockStateVariant.create().put(VariantSettings.MODEL, activeWave1ModelId))
                        .register(true, 2, BlockStateVariant.create().put(VariantSettings.MODEL, activeWave2ModelId))
                        .register(true, 3, BlockStateVariant.create().put(VariantSettings.MODEL, activeWave3ModelId))));

        blockStateModelGenerator.registerParentedItemModel(ModBlocks.BASALT_SPAWNER, inactiveModelId);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.FROSTSTONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.SALONDITE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_SALONDITE, Models.GENERATED);
        itemModelGenerator.register(ModItems.AXE_HEAD, Models.GENERATED);
        itemModelGenerator.register(ModItems.FOREST_HEART, Models.GENERATED);
        itemModelGenerator.register(ModItems.LUMINITE, Models.GENERATED);
        itemModelGenerator.register(ModItems.FOREST_HAVEN_DISC_FRAGMENT, Models.GENERATED);
        itemModelGenerator.register(ModItems.FOREST_HAVEN_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.CURSED_ESSENCE, Models.GENERATED);
        itemModelGenerator.register(ModItems.INFECTED_ESSENCE, Models.GENERATED);

        itemModelGenerator.register(ModItems.RAW_FROSTSTONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.STORMITE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_STORMITE, Models.GENERATED);
        itemModelGenerator.register(ModItems.MOONSTONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.TECTONITE_GEODE, Models.GENERATED);
        itemModelGenerator.register(ModItems.DOOMSDAY_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.TITAN_SANDS_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.ECHOES_OF_THE_ABYSS_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.ENDER_FURY_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.MYSTBORN_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.WATER_HORIZONS_MUSIC_DISC, Models.GENERATED);

        itemModelGenerator.register(ModItems.SALMON_NIGIRI, Models.GENERATED);
        itemModelGenerator.register(ModItems.COD_NIGIRI, Models.GENERATED);
        itemModelGenerator.register(ModItems.WAFFLE, Models.GENERATED);
        itemModelGenerator.register(ModItems.CHICKEN_NUGGETS, Models.GENERATED);
        itemModelGenerator.register(ModItems.VEGGIE_SANDWICH, Models.GENERATED);
        itemModelGenerator.register(ModItems.CAULIFLOWER, Models.GENERATED);
        itemModelGenerator.register(ModItems.POWER_CORE, Models.GENERATED);
        itemModelGenerator.register(ModItems.ROOT, Models.GENERATED);

        itemModelGenerator.register(ModItems.FROSTSTONE_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.ROYAL_FROSTSTONE_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.ROYAL_TECTONITE_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.MOONSTONE_RAPIER, Models.HANDHELD);

        itemModelGenerator.register(ModItems.FROSTSTONE_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.FROSTSTONE_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.FROSTSTONE_HOE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.FROSTSTONE_PICKAXE, Models.HANDHELD);

        itemModelGenerator.registerArmor(((ArmorItem) ModItems.FROSTSTONE_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.FROSTSTONE_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.FROSTSTONE_LEGGINGS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.FROSTSTONE_BOOTS));

        itemModelGenerator.register(ModItems.MANTIS_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));

        itemModelGenerator.register(ModItems.COPPER_BULB_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));

        itemModelGenerator.register(ModItems.ICE_SPIDER_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));

        itemModelGenerator.register(ModItems.BASALT_HOWLER_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));

        itemModelGenerator.register(ModItems.HAVENICA_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
    }
}