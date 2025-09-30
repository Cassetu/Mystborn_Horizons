package cassetu.mystbornhorizons.world;

import cassetu.mystbornhorizons.sound.ModSounds;
import cassetu.mystbornhorizons.event.LoreHandler;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.world.PersistentState;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

public class CurseState extends PersistentState {
    private boolean curseActive = false;
    private boolean cursePaused = false;
    private int mobsKilled = 0;
    private static final int MOBS_NEEDED = 10;
    private ServerBossBar curseBossBar;
    private long musicStartTime = 0;
    private static final long MUSIC_DURATION = 2820;

    private Map<UUID, Long> playerMusicStartTimes = new HashMap<>();
    private Map<UUID, Boolean> playerMusicPlaying = new HashMap<>();

    public static CurseState getOrCreate(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                new Type<>(
                        CurseState::new,
                        CurseState::fromNbt,
                        null
                ),
                "forests_curse_state"
        );
    }

    public static CurseState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        CurseState state = new CurseState();
        state.curseActive = nbt.getBoolean("curse_active");
        state.cursePaused = nbt.getBoolean("curse_paused");
        state.mobsKilled = nbt.getInt("mobs_killed");
        state.musicStartTime = nbt.getLong("music_start_time");

        if (state.curseActive) {
            state.createBossBar();
        }

        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putBoolean("curse_active", curseActive);
        nbt.putBoolean("curse_paused", cursePaused);
        nbt.putInt("mobs_killed", mobsKilled);
        nbt.putLong("music_start_time", musicStartTime);
        return nbt;
    }

    public void activateCurse(ServerWorld world) {
        if (!curseActive) {
            curseActive = true;
            cursePaused = false;
            mobsKilled = 0;
            musicStartTime = world.getTime();
            createBossBar();
            this.markDirty();

            stopBackgroundMusic(world);
            startCurseMusicForAllPlayers(world);

            for (ServerPlayerEntity player : world.getPlayers()) {
                if (curseBossBar != null) {
                    curseBossBar.addPlayer(player);
                }
                player.sendMessage(Text.literal("§4§lThe Curse has begun!"), false);
                player.sendMessage(Text.literal("§6Kill cursed mobs to lift the curse (" + mobsKilled + "/" + MOBS_NEEDED + ")"), false);
            }
        }
    }

    private void startCurseMusicForAllPlayers(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            startCurseMusicForPlayer(world, player);
        }
    }

    private void startCurseMusicForPlayer(ServerWorld world, ServerPlayerEntity player) {
        UUID playerId = player.getUuid();

        stopBackgroundMusicForPlayer(player);
        player.playSoundToPlayer(ModSounds.NIGHT_SHACKLES_BG, SoundCategory.MUSIC, 0.8f, 1.0f);

        playerMusicStartTimes.put(playerId, world.getTime());
        playerMusicPlaying.put(playerId, true);
    }

    private void stopBackgroundMusic(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            stopBackgroundMusicForPlayer(player);
        }
    }

    private void stopBackgroundMusicForPlayer(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(new StopSoundS2CPacket(null, SoundCategory.AMBIENT));
        player.networkHandler.sendPacket(new StopSoundS2CPacket(null, SoundCategory.RECORDS));
    }

    private void stopCurseMusic(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            stopCurseMusicForPlayer(player);
        }
    }

    private void stopCurseMusicForPlayer(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();

        player.networkHandler.sendPacket(new StopSoundS2CPacket(
                null,
                SoundCategory.MUSIC
        ));

        playerMusicPlaying.put(playerId, false);
        playerMusicStartTimes.remove(playerId);
    }

    private static final String[] CREEPY_MESSAGES = {
            "<???> Something ancient stirs beneath our feet",
            "<???> We've poisoned this place just by being here",
            "<???> The earth itself rejects us now",
            "<???> The world grows darker with each breath...",
            "<???> Can you feel it spreading? The taint seeps into everything...",
            "<???> The curse feeds on our fear",
            "<???> The forest remembers what we've done",
            "<???> It knows we're here",
            "<???> Do you hear them too? The voices in the wind?"
    };

    public void addMobKill(ServerWorld world) {
        if (curseActive) {
            mobsKilled++;
            updateBossBar();
            this.markDirty();

            for (ServerPlayerEntity player : world.getPlayers()) {
                player.sendMessage(Text.literal("§6Curse Progress: " + mobsKilled + "/" + MOBS_NEEDED + " cursed mobs killed"), true);
            }

            if (world.getRandom().nextFloat() < 0.7f) {
                String creepyMessage = CREEPY_MESSAGES[world.getRandom().nextInt(CREEPY_MESSAGES.length)];
                for (ServerPlayerEntity player : world.getPlayers()) {
                    player.sendMessage(Text.literal(creepyMessage), false);
                    //player.addStatusEffect(new StatusEffectInstance(ModEffects.DIRT_OVERLAY_EFFECT, 160, 0, false, false));
                }
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

            stopCurseMusic(world);
            playerMusicStartTimes.clear();
            playerMusicPlaying.clear();

            if (curseBossBar != null) {
                curseBossBar.clearPlayers();
                curseBossBar = null;
            }

            world.setTimeOfDay(1000);
            world.setWeather(0, 0, false, false);

            world.iterateEntities().forEach(entity -> {
                if (entity instanceof HostileEntity && entity.hasCustomName() &&
                        entity.getCustomName() != null &&
                        entity.getCustomName().getString().contains("Cursed")) {
                    entity.discard();
                }
            });

            this.markDirty();

            for (ServerPlayerEntity player : world.getPlayers()) {
                clearHostileEffects(player);

                player.sendMessage(Text.literal("§2§lThe Curse has been lifted!"), false);
                player.sendMessage(Text.literal("§aThe world returns to its natural state."), false);

                LoreHandler.showChapter2Title(player);
            }
        }
    }

    private void clearHostileEffects(ServerPlayerEntity player) {
        List<StatusEffectInstance> effectsToRemove = new ArrayList<>();

        for (StatusEffectInstance effect : player.getStatusEffects()) {
            if (!effect.getEffectType().value().isBeneficial()) {
                effectsToRemove.add(effect);
            }
        }

        for (StatusEffectInstance effect : effectsToRemove) {
            player.removeStatusEffect(effect.getEffectType());
        }
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        if (curseActive) {
            if (curseBossBar != null) {
                curseBossBar.addPlayer(player);
            }

            ServerWorld world = (ServerWorld) player.getWorld();
            startCurseMusicForPlayer(world, player);

            cursePaused = false;
            this.markDirty();
        }
    }

    public void onPlayerLeave(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();

        if (curseBossBar != null) {
            curseBossBar.removePlayer(player);
        }

        playerMusicStartTimes.remove(playerId);
        playerMusicPlaying.remove(playerId);
    }

    public void checkPauseState(ServerWorld world) {
        if (curseActive && world.getPlayers().size() == 0) {
            cursePaused = true;
            this.markDirty();
        }
    }

    private void createBossBar() {
        curseBossBar = new ServerBossBar(
                Text.literal("§4 Curse"),
                BossBar.Color.RED,
                BossBar.Style.NOTCHED_10
        );
        updateBossBar();
    }

    private void updateBossBar() {
        if (curseBossBar != null) {
            float progress = (float) mobsKilled / MOBS_NEEDED;
            curseBossBar.setPercent(progress);
            curseBossBar.setName(Text.literal("§4Curse §r- Kill Cursed Mobs: " + mobsKilled + "/" + MOBS_NEEDED));
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

    public boolean isCurseMusicPlayingForPlayer(UUID playerId) {
        return curseActive && playerMusicPlaying.getOrDefault(playerId, false);
    }

    public void tick(ServerWorld world) {
        if (curseActive && !cursePaused) {
            world.setTimeOfDay(18000);
            world.setWeather(6000, 0, false, true);

            for (ServerPlayerEntity player : world.getPlayers()) {
                UUID playerId = player.getUuid();
                Long playerMusicStart = playerMusicStartTimes.get(playerId);

                if (playerMusicStart != null) {
                    long timeSinceStart = world.getTime() - playerMusicStart;

                    if (timeSinceStart >= MUSIC_DURATION - 90) {
                        startCurseMusicForPlayer(world, player);
                    }
                }
            }

            if (world.getTime() % 40 == 0) {
                for (ServerPlayerEntity player : world.getPlayers()) {
                    stopBackgroundMusicForPlayer(player);
                }
            }

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
        cloud.setRadius(3.5f);
        cloud.setDuration(300);
        cloud.setWaitTime(10);
        cloud.setRadiusGrowth(-0.005f);

        StatusEffectInstance effect = new StatusEffectInstance(ModEffects.SPORE_VISION_EFFECT, 280, 0, false, true);
        cloud.addEffect(effect);

        cloud.setParticleType(ParticleTypes.SCRAPE);

        world.spawnEntity(cloud);
    }
}