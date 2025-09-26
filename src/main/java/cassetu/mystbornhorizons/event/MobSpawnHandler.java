package cassetu.mystbornhorizons.event;

import cassetu.mystbornhorizons.util.EnhancedMobEquipment;
import cassetu.mystbornhorizons.world.ForestsCurseState;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.SpawnHelper;

public class MobSpawnHandler {

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(world instanceof ServerWorld serverWorld)) return;

            if (entity instanceof HostileEntity mob && shouldEnhanceMob(mob)) {
                serverWorld.getServer().execute(() -> {
                    EnhancedMobEquipment.equipPostHavenicaMob(mob, serverWorld);

                    if (serverWorld.getRandom().nextFloat() < 0.3f) {
                        EnhancedMobEquipment.applyCorruptionEffects(mob, serverWorld);
                    }
                });
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world instanceof ServerWorld serverWorld) {
                ForestsCurseState curseState = ForestsCurseState.getOrCreate(serverWorld);

                if (curseState.isCurseActive() && serverWorld.getTime() % 40 == 0) {
                    spawnCursedMobs(serverWorld);
                }
            }
        });
    }

    private static boolean isValidSpawnLocation(ServerWorld world, BlockPos pos) {
        if (!world.getBlockState(pos).isAir() || !world.getBlockState(pos.up()).isAir()) {
            return false;
        }

        if (world.getBlockState(pos.down()).isAir()) {
            return false;
        }

        return world.getLightLevel(pos) < 8;
    }

    private static void spawnCursedMobs(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (world.getRandom().nextFloat() < 0.7f) {
                double angle = world.getRandom().nextDouble() * Math.PI * 2;
                double distance = 16 + world.getRandom().nextDouble() * 16;

                double x = player.getX() + Math.cos(angle) * distance;
                double z = player.getZ() + Math.sin(angle) * distance;

                BlockPos spawnPos = new BlockPos((int)x, world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z), (int)z);

                if (isValidSpawnLocation(world, spawnPos)) {
                    EntityType<?>[] mobTypes = {
                            EntityType.ZOMBIE, EntityType.WITCH, EntityType.BOGGED,
                            EntityType.SPIDER, EntityType.ENDERMAN, EntityType.VINDICATOR,
                            EntityType.SKELETON
                    };

                    EntityType<?> chosenType = mobTypes[world.getRandom().nextInt(mobTypes.length)];

                    if (chosenType.create(world) instanceof HostileEntity mob) {
                        mob.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                        mob.initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.EVENT, null);

                        if (shouldEnhanceMob(mob)) {
                            EnhancedMobEquipment.equipPostHavenicaMob(mob, world);
                            EnhancedMobEquipment.applyCorruptionEffects(mob, world);

                            world.spawnParticles(
                                    net.minecraft.particle.ParticleTypes.SOUL_FIRE_FLAME,
                                    mob.getX(), mob.getY() + 1, mob.getZ(),
                                    10,
                                    1.0, 1.0, 1.0,
                                    0.1
                            );
                        }

                        world.spawnEntity(mob);
                    }
                }
            }
        }
    }

    private static boolean shouldEnhanceMob(HostileEntity mob) {
        if (mob instanceof WitherEntity ||
                mob.getClass().getSimpleName().contains("Boss") ||
                mob.getClass().getSimpleName().contains("Havenica") ||
                mob.getClass().getSimpleName().contains("Dragon")) {
            return false;
        }

        return mob instanceof ZombieEntity ||
                mob instanceof SkeletonEntity ||
                mob instanceof CreeperEntity ||
                mob instanceof SpiderEntity ||
                mob instanceof EndermanEntity ||
                mob instanceof VindicatorEntity ||
                mob instanceof PillagerEntity ||
                mob instanceof WitchEntity ||
                mob instanceof BlazeEntity ||
                mob instanceof BoggedEntity ||
                mob.getClass().getSimpleName().contains("Entity");
    }
}