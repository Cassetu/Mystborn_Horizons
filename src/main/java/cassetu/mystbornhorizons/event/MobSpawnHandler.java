package cassetu.mystbornhorizons.event;

import cassetu.mystbornhorizons.item.ModItems;
import cassetu.mystbornhorizons.util.EnhancedMobEquipment;
import cassetu.mystbornhorizons.world.CurseState;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class MobSpawnHandler {

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(world instanceof ServerWorld serverWorld)) return;

            if (entity instanceof HostileEntity mob && shouldEnhanceMob(mob)) {
                CurseState curseState = CurseState.getOrCreate(serverWorld);
                if (curseState.isCurseActive()) {
                    return;
                }

                if (mob.hasCustomName() && mob.getCustomName() != null) {
                    String name = mob.getCustomName().getString();
                    if (name.contains("Cursed")) {
                        return;
                    }
                }

                serverWorld.getServer().execute(() -> {
                    EnhancedMobEquipment.equipPostHavenicaMob(mob, serverWorld);

                    if (serverWorld.getRandom().nextFloat() < 0.3f) {
                        EnhancedMobEquipment.applyCorruptionEffects(mob, serverWorld);
                    }
                });
            }
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof HostileEntity hostileEntity && entity.getWorld() instanceof ServerWorld serverWorld) {
                if (entity.hasCustomName() && entity.getCustomName() != null) {
                    String name = entity.getCustomName().getString();
                    if (name.contains("Infected")) {
                        dropInfectedEssence(hostileEntity, serverWorld);
                    }
                }
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world instanceof ServerWorld serverWorld) {
                CurseState curseState = CurseState.getOrCreate(serverWorld);

                if (curseState.isCurseActive() && serverWorld.getTime() % 40 == 0) {
                    spawnCursedMobs(serverWorld);
                }
            }
        });
    }

    private static void dropInfectedEssence(HostileEntity mob, ServerWorld world) {
        var scoreboard = world.getScoreboard();
        String teamName = "infectedEssenceGlow";
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.addTeam(teamName);
            team.setColor(Formatting.GREEN);
            team.setShowFriendlyInvisibles(false);
        }

        ItemStack essence = new ItemStack(ModItems.INFECTED_ESSENCE);
        ItemEntity itemEntity = new ItemEntity(world, mob.getX(), mob.getY() + 0.5, mob.getZ(), essence);

        double velocityX = (world.getRandom().nextDouble() - 0.5) * 0.3;
        double velocityY = world.getRandom().nextDouble() * 0.2 + 0.1;
        double velocityZ = (world.getRandom().nextDouble() - 0.5) * 0.3;
        itemEntity.setVelocity(velocityX, velocityY, velocityZ);

        world.spawnEntity(itemEntity);
        scoreboard.addScoreHolderToTeam(itemEntity.getUuidAsString(), team);
        itemEntity.setGlowing(true);

        world.spawnParticles(
                ParticleTypes.HAPPY_VILLAGER,
                mob.getX(), mob.getY() + 1, mob.getZ(),
                8,
                0.5, 0.5, 0.5,
                0.05
        );
    }

    private static boolean isValidSpawnLocation(ServerWorld world, BlockPos pos) {
        if (!world.getBlockState(pos).isAir() || !world.getBlockState(pos.up()).isAir()) {
            return false;
        }

        if (world.getBlockState(pos.down()).isAir()) {
            return false;
        }

        return world.getLightLevel(pos) < 6;
    }

    private static void spawnCursedMobs(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (world.getRandom().nextFloat() < 0.4f) {
                double angle = world.getRandom().nextDouble() * Math.PI * 2;
                double distance = 18 + world.getRandom().nextDouble() * 16;

                double x = player.getX() + Math.cos(angle) * distance;
                double z = player.getZ() + Math.sin(angle) * distance;

                BlockPos spawnPos = new BlockPos((int)x, world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z), (int)z);

                if (isValidSpawnLocation(world, spawnPos)) {
                    EntityType<?>[] mobTypes = {
                            EntityType.ZOMBIE, EntityType.BOGGED,
                            EntityType.SPIDER, EntityType.STRAY,
                            EntityType.SKELETON
                    };

                    EntityType<?> chosenType = mobTypes[world.getRandom().nextInt(mobTypes.length)];

                    if (chosenType.create(world) instanceof HostileEntity mob) {
                        mob.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                        mob.initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.EVENT, null);

                        if (shouldEnhanceMob(mob)) {
                            EnhancedMobEquipment.equipCursedMob(mob, world);
                            EnhancedMobEquipment.applyCurseEffects(mob, world);

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
                mob instanceof VindicatorEntity ||
                mob instanceof PillagerEntity ||
                mob instanceof WitchEntity ||
                mob instanceof StrayEntity ||
                mob instanceof BlazeEntity ||
                mob instanceof BoggedEntity ||
                mob.getClass().getSimpleName().contains("Entity");
    }
}