package cassetu.mystbornhorizons;

import cassetu.mystbornhorizons.block.ModBlocks;
import cassetu.mystbornhorizons.command.MystbornCommands;
import cassetu.mystbornhorizons.effect.ModEffects;
import cassetu.mystbornhorizons.enchantment.ModEnchantmentEffects;
import cassetu.mystbornhorizons.entity.ModEntities;
import cassetu.mystbornhorizons.entity.custom.*;
import cassetu.mystbornhorizons.event.FirstSpawnBookHandler;
import cassetu.mystbornhorizons.event.MobSpawnHandler;
import cassetu.mystbornhorizons.event.ForestsCurseHandler;
import cassetu.mystbornhorizons.item.ModItemGroups;
import cassetu.mystbornhorizons.item.ModItems;
import cassetu.mystbornhorizons.network.ModPackets;
import cassetu.mystbornhorizons.sound.ModSounds;
import cassetu.mystbornhorizons.util.CutsceneManager;
import cassetu.mystbornhorizons.util.ModLootTablesModifiers;
import cassetu.mystbornhorizons.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MystbornHorizons implements ModInitializer {
	public static final String MOD_ID = "mystbornhorizons";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModEffects.registerEffects();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModSounds.registerSounds();
		ModEnchantmentEffects.registerEnchantmentEffects();
		ModWorldGeneration.generateModWorldGen();
		ModLootTablesModifiers.modifyLootTables();
		ModPackets.registerPackets();
		CutsceneManager.initialize();
		MobSpawnHandler.register();
		ForestsCurseHandler.register();
		MystbornCommands.registerCommands();
		FirstSpawnBookHandler.register();

		FabricDefaultAttributeRegistry.register(ModEntities.MANTIS, MantisEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.COPPERBULB, CopperBulbEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ICESPIDER, IceSpiderEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BASALTHOWLER, BasaltHowlerEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.HAVENICA, HavenicaEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.HAVEN_CORE, HavenCoreEntity.createAttributes());

		CompostingChanceRegistry.INSTANCE.add(ModItems.HONEY_BERRIES, 0.35f);
	}
}