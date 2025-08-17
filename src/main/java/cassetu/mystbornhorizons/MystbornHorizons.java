package cassetu.mystbornhorizons;

import cassetu.mystbornhorizons.block.ModBlocks;
import cassetu.mystbornhorizons.effect.ModEffects;
import cassetu.mystbornhorizons.enchantment.ModEnchantmentEffects;
import cassetu.mystbornhorizons.entity.ModEntities;
import cassetu.mystbornhorizons.entity.custom.BasaltHowlerEntity;
import cassetu.mystbornhorizons.entity.custom.CopperBulbEntity;
import cassetu.mystbornhorizons.entity.custom.IceSpiderEntity;
import cassetu.mystbornhorizons.entity.custom.MantisEntity;
import cassetu.mystbornhorizons.item.ModItemGroups;
import cassetu.mystbornhorizons.item.ModItems;
import cassetu.mystbornhorizons.sound.ModSounds;
import cassetu.mystbornhorizons.util.ModLootTablesModifiers;
import cassetu.mystbornhorizons.world.biome.ModBiomes;
import cassetu.mystbornhorizons.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MystbornHorizons implements ModInitializer {
	public static final String MOD_ID = "mystbornhorizons";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void generateBiomes() {
		MystbornHorizons.LOGGER.info("Starting biome placement for " + MystbornHorizons.MOD_ID);

		try {
			// Your existing code for Alpine Meadows
			MystbornHorizons.LOGGER.info("Registered Alpine Meadows placement");

			// Craggy Peaks code
			MystbornHorizons.LOGGER.info("Registered Craggy Peaks placement");

			// Rocky Tundra code
			MystbornHorizons.LOGGER.info("Registered Rocky Tundra placement");

		} catch (Exception e) {
			MystbornHorizons.LOGGER.error("Error during biome placement: " + e.getMessage());
			e.printStackTrace();
		}

		MystbornHorizons.LOGGER.info("Completed biome placement for " + MystbornHorizons.MOD_ID);
	}


	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModEffects.registerEffects();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModSounds.registerSounds();
		ModEnchantmentEffects.registerEnchantmentEffects();
		ModBiomes.registerBiomes();

		ModWorldGeneration.generateModWorldGen();
		ModLootTablesModifiers.modifyLootTables();

		FabricDefaultAttributeRegistry.register(ModEntities.MANTIS, MantisEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.COPPERBULB, CopperBulbEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ICESPIDER, IceSpiderEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BASALTHOWLER, BasaltHowlerEntity.createAttributes());

		CompostingChanceRegistry.INSTANCE.add(ModItems.HONEY_BERRIES, 0.35f);
	}
}