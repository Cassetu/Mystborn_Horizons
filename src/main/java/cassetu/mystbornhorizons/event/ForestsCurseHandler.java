package cassetu.mystbornhorizons.event;

import cassetu.mystbornhorizons.world.ForestsCurseState;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class ForestsCurseHandler {

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof HostileEntity hostileEntity && entity.getWorld() instanceof ServerWorld serverWorld) {
                if (entity.hasCustomName() && entity.getCustomName() != null &&
                        entity.getCustomName().getString().contains("Infected")) {

                    ForestsCurseState curseState = ForestsCurseState.getOrCreate(serverWorld);
                    if (curseState.isCurseActive()) {
                        curseState.addMobKill(serverWorld);
                    }
                }
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world instanceof ServerWorld serverWorld) {
                ForestsCurseState curseState = ForestsCurseState.getOrCreate(serverWorld);
                curseState.tick(serverWorld);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            ServerWorld world = player.getServerWorld();
            ForestsCurseState curseState = ForestsCurseState.getOrCreate(world);
            curseState.onPlayerJoin(player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            ServerWorld world = player.getServerWorld();
            ForestsCurseState curseState = ForestsCurseState.getOrCreate(world);
            curseState.onPlayerLeave(player);
        });
    }
}