package cassetu.mystbornhorizons.world.gen;


public class ModWorldGeneration {
    public static void generateModWorldGen() {
        ModOreGeneration.generateOres();
        ModEntitySpawns.addSpawns();
        ModBushGeneration.generateBushes();
    }
}
