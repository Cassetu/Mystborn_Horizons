package cassetu.mystbornhorizons.world.gen;


import cassetu.mystbornhorizons.MystbornHorizons;

import static com.mojang.text2speech.Narrator.LOGGER;

public class ModWorldGeneration {
    public static void generateModWorldGen() {
        MystbornHorizons.LOGGER.info("Generating ores...");
        ModOreGeneration.generateOres();
        MystbornHorizons.LOGGER.info("Adding entity spawns...");
        ModEntitySpawns.addSpawns();
        MystbornHorizons.LOGGER.info("Generating bushes...");
        ModBushGeneration.generateBushes();
        MystbornHorizons.LOGGER.info("Starting biome generation...");
        ModBiomeGeneration.generateBiomes();
        MystbornHorizons.LOGGER.info("Biome generation completed");
    }
}
