package cassetu.mystbornhorizons.world;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import cassetu.mystbornhorizons.effect.ModEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.PersistentState;

public class ForestsCurseState extends PersistentState {
    private boolean curseActive = false;
    private int mobsKilled = 0;
    private static final int MOBS_NEEDED = 10;
    private ServerBossBar curseBossBar;

    public static ForestsCurseState getOrCreate(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                new Type<>(
                        ForestsCurseState::new,
                        ForestsCurseState::fromNbt,
                        null
                ),
                "forests_curse_state"
        );
    }

    public static ForestsCurseState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        ForestsCurseState state = new ForestsCurseState();
        state.curseActive = nbt.getBoolean("curse_active");
        state.mobsKilled = nbt.getInt("mobs_killed");
        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putBoolean("curse_active", curseActive);
        nbt.putInt("mobs_killed", mobsKilled);
        return nbt;
    }

    public void activateCurse(ServerWorld world) {
        if (!curseActive) {
            curseActive = true;
            mobsKilled = 0;
            createBossBar();
            this.markDirty();

            for (ServerPlayerEntity player : world.getPlayers()) {
                if (curseBossBar != null) {
                    curseBossBar.addPlayer(player);
                }
                player.sendMessage(Text.literal("Â§4Â§lThe Forest's Curse has begun!"), false);
                player.sendMessage(Text.literal("Â§6Kill infected mobs to lift the curse (" + mobsKilled + "/" + MOBS_NEEDED + ")"), false);
            }
        }
    }

    public void addMobKill(ServerWorld world) {
        if (curseActive) {
            mobsKilled++;
            updateBossBar();
            this.markDirty();

            for (ServerPlayerEntity player : world.getPlayers()) {
                player.sendMessage(Text.literal("Â§6Curse Progress: " + mobsKilled + "/" + MOBS_NEEDED + " infected mobs killed"), true);
            }

            if (mobsKilled >= MOBS_NEEDED) {
                endCurse(world);
            }
        }
    }

    public void endCurse(ServerWorld world) {
        if (curseActive) {
            curseActive = false;
            mobsKilled = 0;

            if (curseBossBar != null) {
                curseBossBar.clearPlayers();
                curseBossBar = null;
            }

            world.setTimeOfDay(1000);
            world.setWeather(0, 0, false, false);

            world.iterateEntities().forEach(entity -> {
                if (entity instanceof HostileEntity && entity.hasCustomName() &&
                        entity.getCustomName() != null &&
                        entity.getCustomName().getString().contains("Infected")) {
                    entity.discard();
                }
            });

            this.markDirty();

            for (ServerPlayerEntity player : world.getPlayers()) {
                player.sendMessage(Text.literal("Â§2Â§lThe Forest's Curse has been lifted!"), false);
                player.sendMessage(Text.literal("Â§aThe forest returns to its natural state."), false);
            }
        }
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        if (curseActive && curseBossBar != null) {
            curseBossBar.addPlayer(player);
        }
    }

    public void onPlayerLeave(ServerPlayerEntity player) {
        if (curseBossBar != null) {
            curseBossBar.removePlayer(player);
        }
    }

    private void createBossBar() {
        curseBossBar = new ServerBossBar(
                Text.literal("Â§4Forest's Curse"),
                BossBar.Color.RED,
                BossBar.Style.NOTCHED_10
        );
        updateBossBar();
    }

    private void updateBossBar() {
        if (curseBossBar != null) {
            float progress = (float) mobsKilled / MOBS_NEEDED;
            curseBossBar.setPercent(progress);
            curseBossBar.setName(Text.literal("Â§4Forest's Curse Â§r- Kill Infected Mobs: " + mobsKilled + "/" + MOBS_NEEDED));
        }
    }

    public boolean isCurseActive() {
        return curseActive;
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public void tick(ServerWorld world) {
        if (curseActive) {
            world.setTimeOfDay(18000);
            world.setWeather(0, 6000, true, false);

            if (world.getTime() % 20 == 0) {
                for (ServerPlayerEntity player : world.getPlayers()) {
                    double playerX = player.getX();
                    double playerZ = player.getZ();

                    for (int i = 0; i < 15; i++) {
                        double x = playerX + (world.getRandom().nextDouble() - 0.5) * 40;
                        double z = playerZ + (world.getRandom().nextDouble() - 0.5) * 40;
                        double y = world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z);

                        world.spawnParticles(
                                net.minecraft.particle.ParticleTypes.MYCELIUM,
                                x, y + 20, z,
                                1,
                                0.5, 0.0, 0.5,
                                0.0
                        );

                        world.spawnParticles(
                                net.minecraft.particle.ParticleTypes.SMOKE,
                                x, y + 1, z,
                                2,
                                1.0, 0.5, 1.0,
                                0.05
                        );

                        world.spawnParticles(
                                net.minecraft.particle.ParticleTypes.SOUL,
                                x, y + 1, z,
                                1,
                                2.0, 1.0, 2.0,
                                0.02
                        );
                    }

                    world.spawnParticles(
                            net.minecraft.particle.ParticleTypes.CRIMSON_SPORE,
                            playerX, player.getY() + 2, playerZ,
                            8,
                            3.0, 1.0, 3.0,
                            0.1
                    );
                }
            }

            if (world.getTime() % 80 == 0) {
                for (ServerPlayerEntity player : world.getPlayers()) {
                    if (world.getRandom().nextFloat() < 0.4f) {
                        spawnSporeVisionCloud(world, player);
                    }
                }
            }
        }
    }

    private void spawnSporeVisionCloud(ServerWorld world, ServerPlayerEntity player) {
        double offsetX = (world.getRandom().nextDouble() - 0.5) * 6;
        double offsetZ = (world.getRandom().nextDouble() - 0.5) * 6;
        double x = player.getX() + offsetX;
        double z = player.getZ() + offsetZ;
        double y = player.getY() + world.getRandom().nextDouble() * 2;

        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(world, x, y, z);
        cloud.setRadius(3.0f);
        cloud.setDuration(200);
        cloud.setWaitTime(20);
        cloud.setRadiusGrowth(-0.005f);

        StatusEffectInstance effect = new StatusEffectInstance(ModEffects.SPORE_VISION_EFFECT, 300, 0, false, true);
        cloud.addEffect(effect);
        cloud.setParticleType(ParticleTypes.ITEM_COBWEB);

        world.spawnEntity(cloud);
    }
}