package cassetu.mystbornhorizons.world;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
    private boolean cursePaused = false;
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
        state.cursePaused = nbt.getBoolean("curse_paused");
        state.mobsKilled = nbt.getInt("mobs_killed");
        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putBoolean("curse_active", curseActive);
        nbt.putBoolean("curse_paused", cursePaused);
        nbt.putInt("mobs_killed", mobsKilled);
        return nbt;
    }

    public void activateCurse(ServerWorld world) {
        if (!curseActive) {
            curseActive = true;
            cursePaused = false;
            mobsKilled = 0;
            createBossBar();
            this.markDirty();

            for (ServerPlayerEntity player : world.getPlayers()) {
                if (curseBossBar != null) {
                    curseBossBar.addPlayer(player);
                }
                player.sendMessage(Text.literal("§4§lThe Forest's Curse has begun!"), false);
                player.sendMessage(Text.literal("§6Kill infected mobs to lift the curse (" + mobsKilled + "/" + MOBS_NEEDED + ")"), false);
            }
        }
    }

    public void addMobKill(ServerWorld world) {
        if (curseActive) {
            mobsKilled++;
            updateBossBar();
            this.markDirty();

            for (ServerPlayerEntity player : world.getPlayers()) {
                player.sendMessage(Text.literal("§6Curse Progress: " + mobsKilled + "/" + MOBS_NEEDED + " infected mobs killed"), true);
            }

            if (mobsKilled >= MOBS_NEEDED) {
                endCurse(world);
            }
        }
    }

    public void endCurse(ServerWorld world) {
        if (curseActive) {
            curseActive = false;
            cursePaused = false;
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
                player.sendMessage(Text.literal("§2§lThe Forest's Curse has been lifted!"), false);
                player.sendMessage(Text.literal("§aThe forest returns to its natural state."), false);
            }
        }
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        if (curseActive) {
            if (curseBossBar != null) {
                curseBossBar.addPlayer(player);
            }

            cursePaused = false;
            this.markDirty();
        }
    }

    public void onPlayerLeave(ServerPlayerEntity player) {
        if (curseBossBar != null) {
            curseBossBar.removePlayer(player);
        }
    }

    public void checkPauseState(ServerWorld world) {
        if (curseActive && world.getPlayers().size() == 0) {
            cursePaused = true;
            this.markDirty();
        }
    }

    private void createBossBar() {
        curseBossBar = new ServerBossBar(
                Text.literal("§4 Forest Curse"),
                BossBar.Color.RED,
                BossBar.Style.NOTCHED_10
        );
        updateBossBar();
    }

    private void updateBossBar() {
        if (curseBossBar != null) {
            float progress = (float) mobsKilled / MOBS_NEEDED;
            curseBossBar.setPercent(progress);
            curseBossBar.setName(Text.literal("§4Forest's Curse §r- Kill Infected Mobs: " + mobsKilled + "/" + MOBS_NEEDED));
        }
    }

    public boolean isCurseActive() {
        return curseActive;
    }

    public boolean isCursePaused() {
        return cursePaused;
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public void tick(ServerWorld world) {
        if (curseActive && !cursePaused) {
            world.setTimeOfDay(18000);
            world.setWeather(6000, 0, false, true);

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
                                12,
                                0.5, 0.0, 0.5,
                                0.0
                        );

                        world.spawnParticles(
                                net.minecraft.particle.ParticleTypes.SMOKE,
                                x, y + 1, z,
                                8,
                                1.0, 0.5, 1.0,
                                0.05
                        );

                        world.spawnParticles(
                                net.minecraft.particle.ParticleTypes.SOUL,
                                x, y + 1, z,
                                4,
                                2.0, 1.0, 2.0,
                                0.02
                        );
                    }

                    world.spawnParticles(
                            net.minecraft.particle.ParticleTypes.CRIMSON_SPORE,
                            playerX, player.getY() + 2, playerZ,
                            9,
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
        double distance = 6.0 + world.getRandom().nextDouble() * 2.0;
        double angle = world.getRandom().nextDouble() * 2 * Math.PI;

        double offsetX = Math.cos(angle) * distance;
        double offsetZ = Math.sin(angle) * distance;

        double x = player.getX() + offsetX;
        double z = player.getZ() + offsetZ;
        double y = player.getY() + world.getRandom().nextDouble() * 2;

        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(world, x, y, z);
        cloud.setRadius(4.0f);
        cloud.setDuration(300);
        cloud.setWaitTime(10);
        cloud.setRadiusGrowth(-0.005f);

        StatusEffectInstance effect = new StatusEffectInstance(ModEffects.SPORE_VISION_EFFECT, 280, 0, false, true);
        cloud.addEffect(effect);

        cloud.setParticleType(ParticleTypes.SCRAPE);

        world.spawnEntity(cloud);
    }
}