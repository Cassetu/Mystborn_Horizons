package cassetu.mystbornhorizons.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CutsceneManager {
    private static final Map<String, CutsceneData> activeCutscenes = new HashMap<>();

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(CutsceneManager::onServerTick);
        System.out.println("DEBUG: CutsceneManager initialized");
    }

    public static void startCutscene(ServerWorld world, BlockPos pos, CutsceneTickHandler handler) {
        String key = generateKey(world, pos);
        activeCutscenes.put(key, new CutsceneData(world, pos, handler, 0));
        System.out.println("DEBUG: Started cutscene with key: " + key);
    }

    public static void stopCutscene(ServerWorld world, BlockPos pos) {
        String key = generateKey(world, pos);
        boolean removed = activeCutscenes.remove(key) != null;
        System.out.println("DEBUG: Stopped cutscene with key: " + key + ", existed: " + removed);
    }

    public static boolean isCutsceneActive(ServerWorld world, BlockPos pos) {
        String key = generateKey(world, pos);
        boolean active = activeCutscenes.containsKey(key);
        System.out.println("DEBUG: Checking cutscene active for key: " + key + ", result: " + active);
        return active;
    }

    private static String generateKey(ServerWorld world, BlockPos pos) {
        return world.getRegistryKey().getValue().toString() + "_" + pos.toShortString();
    }

    private static void onServerTick(MinecraftServer server) {
        if (activeCutscenes.isEmpty()) {
            return; // No cutscenes to process
        }

        Iterator<Map.Entry<String, CutsceneData>> iterator = activeCutscenes.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, CutsceneData> entry = iterator.next();
            CutsceneData data = entry.getValue();

            // Validate the world still exists and is on the correct server
            if (data.world.getServer() != server) {
                System.out.println("DEBUG: Removing cutscene due to server mismatch: " + entry.getKey());
                iterator.remove();
                continue;
            }

            data.tick++;

            try {
                boolean shouldContinue = data.handler.tick(data.world, data.pos, data.tick);

                if (!shouldContinue) {
                    System.out.println("DEBUG: Cutscene completed naturally: " + entry.getKey());
                    iterator.remove();
                }
            } catch (Exception e) {
                System.out.println("ERROR: Exception in cutscene tick for " + entry.getKey() + ": " + e.getMessage());
                e.printStackTrace();
                iterator.remove(); // Remove problematic cutscenes
            }
        }
    }

    // Debug method to check active cutscenes
    public static void debugPrintActiveCutscenes() {
        System.out.println("DEBUG: Active cutscenes (" + activeCutscenes.size() + "):");
        for (Map.Entry<String, CutsceneData> entry : activeCutscenes.entrySet()) {
            CutsceneData data = entry.getValue();
            System.out.println("  " + entry.getKey() + " - Tick: " + data.tick);
        }
    }

    @FunctionalInterface
    public interface CutsceneTickHandler {
        boolean tick(ServerWorld world, BlockPos pos, int currentTick);
    }

    private static class CutsceneData {
        final ServerWorld world;
        final BlockPos pos;
        final CutsceneTickHandler handler;
        int tick;

        CutsceneData(ServerWorld world, BlockPos pos, CutsceneTickHandler handler, int tick) {
            this.world = world;
            this.pos = pos;
            this.handler = handler;
            this.tick = tick;
        }
    }
}