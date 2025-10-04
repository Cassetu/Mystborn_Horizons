package cassetu.mystbornhorizons.block.entity;

import cassetu.mystbornhorizons.block.ModBlocks;
import cassetu.mystbornhorizons.block.custom.BasaltSpawnerBlock;
import cassetu.mystbornhorizons.entity.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;

public class BasaltSpawnerBlockEntity extends BlockEntity {
    private int currentWave = 1;
    private int maxWaves = 3;
    private int mobsToSpawn = 0;
    private int mobsSpawned = 0;
    private int ticksSinceLastSpawn = 0;
    private int spawnDelay = 40;
    private boolean trialActive = false;
    private boolean onCooldown = false;
    private long cooldownEndTime = 0;
    private UUID trialStarterId = null;
    private Set<UUID> participatingPlayers = new HashSet<>();
    private Set<UUID> spawnedMobs = new HashSet<>();

    private static final int SPAWN_RADIUS = 10;
    private static final int DETECTION_RADIUS = 15;
    private static final long COOLDOWN_DURATION = 72000;

    public boolean isOnCooldown() {
        return onCooldown;
    }

    public long getCooldownTicksRemaining(long currentTime) {
        if (!onCooldown) return 0;
        return Math.max(0, cooldownEndTime - currentTime);
    }

    public BasaltSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASALT_SPAWNER, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state,
                                  BasaltSpawnerBlockEntity blockEntity) {
        if (world instanceof ServerWorld serverWorld) {
            blockEntity.tick(serverWorld, pos, state);
        }
    }

    private void tick(ServerWorld world, BlockPos pos, BlockState state) {
        if (onCooldown) {
            if (world.getTime() >= cooldownEndTime) {
                onCooldown = false;
                markDirty();
            }
            return;
        }

        if (!trialActive) return;

        updateParticipatingPlayers(world, pos);

        if (mobsToSpawn <= 0 && areAllMobsDefeated(world, pos)) {
            if (currentWave >= maxWaves) {
                completeTrial(world, pos, state);
                return;
            } else {
                advanceToNextWave(world, pos, state);
            }
        }

        if (mobsToSpawn > 0) {
            ticksSinceLastSpawn++;
            if (ticksSinceLastSpawn >= spawnDelay) {
                spawnMob(world, pos);
                ticksSinceLastSpawn = 0;
            }
        }

        spawnTrialParticles(world, pos, state);
    }

    public boolean canStartTrial() {
        return !trialActive && !onCooldown;
    }

    public void startTrial(PlayerEntity player) {
        if (!canStartTrial()) return;

        trialActive = true;
        trialStarterId = player.getUuid();
        currentWave = 1;
        participatingPlayers.clear();
        participatingPlayers.add(player.getUuid());
        spawnedMobs.clear();

        setupWave(currentWave);
        markDirty();
    }

    private void setupWave(int wave) {
        mobsToSpawn = 2 + (wave * 3);
        mobsSpawned = 0;
        spawnDelay = Math.max(15, 60 - (wave * 8));
    }

    private void spawnMob(ServerWorld world, BlockPos pos) {
        if (mobsToSpawn <= 0) return;

        BlockPos spawnPos = findValidSpawnPos(world, pos);
        if (spawnPos == null) return;

        EntityType<? extends MobEntity> mobType = getMobTypeForWave(currentWave);
        if (mobType == null) return;

        MobEntity mob = mobType.create(world);
        if (mob != null) {
            mob.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
            mob.initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.SPAWNER, null);

            String mobTypeName = mob.getType().getName().getString();
            mob.setCustomName(Text.literal(mobTypeName + " Spawn"));
            mob.setCustomNameVisible(false);

            mob.setPersistent();

            NbtCompound nbt = new NbtCompound();
            mob.writeNbt(nbt);
            nbt.putBoolean("IsSpawnerMob", true);
            mob.readNbt(nbt);

            if (mob instanceof HostileEntity hostileMob) {
                PlayerEntity target = findNearestParticipatingPlayer(world, pos);
                if (target != null) {
                    hostileMob.setTarget(target);
                }
            }

            world.spawnEntity(mob);
            spawnedMobs.add(mob.getUuid());

            spawnMobParticles(world, spawnPos);

            world.playSound(null, spawnPos, SoundEvents.ENTITY_BLAZE_AMBIENT,
                    SoundCategory.HOSTILE, 0.8F, 1.0F);

            mobsToSpawn--;
            mobsSpawned++;
            markDirty();
        }
    }

    private void spawnMobParticles(ServerWorld world, BlockPos spawnPos) {
        Random random = world.getRandom();

        for (int i = 0; i < 20; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (random.nextDouble() - 0.5) * 2.0;
            double offsetY = random.nextDouble() * 2.0;

            world.addParticle(ParticleTypes.LAVA,
                    spawnPos.getX() + 0.5 + offsetX,
                    spawnPos.getY() + offsetY,
                    spawnPos.getZ() + 0.5 + offsetZ,
                    0, 0.1, 0);

            world.addParticle(ParticleTypes.LARGE_SMOKE,
                    spawnPos.getX() + 0.5 + offsetX,
                    spawnPos.getY() + 1 + offsetY,
                    spawnPos.getZ() + 0.5 + offsetZ,
                    0, 0.2, 0);

            if (i < 5) {
                world.addParticle(ParticleTypes.FLAME,
                        spawnPos.getX() + 0.5 + offsetX,
                        spawnPos.getY() + 0.5 + offsetY,
                        spawnPos.getZ() + 0.5 + offsetZ,
                        0, 0.15, 0);
            }
        }
    }

    private EntityType<? extends MobEntity> getMobTypeForWave(int wave) {
        return switch (wave) {
            case 1 -> EntityType.ZOMBIE;
            case 2 -> ModEntities.COPPERBULB;
            case 3 -> ModEntities.BASALTHOWLER;
            default -> EntityType.ZOMBIE;
        };
    }

    private BlockPos findValidSpawnPos(ServerWorld world, BlockPos center) {
        Random random = world.random;

        for (int i = 0; i < 30; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            int radius = 3 + random.nextInt(SPAWN_RADIUS - 3);

            int x = center.getX() + (int)(Math.cos(angle) * radius);
            int z = center.getZ() + (int)(Math.sin(angle) * radius);

            for (int y = center.getY() + 4; y >= center.getY() - 4; y--) {
                BlockPos testPos = new BlockPos(x, y, z);
                BlockPos groundPos = testPos.down();

                BlockState groundState = world.getBlockState(groundPos);

                if (groundState.isOf(ModBlocks.GILDED_BASALT_TILING) &&
                        world.getBlockState(testPos).isAir() &&
                        world.getBlockState(testPos.up()).isAir()) {
                    return testPos;
                }
            }
        }
        return null;
    }

    private boolean areAllMobsDefeated(ServerWorld world, BlockPos pos) {
        spawnedMobs.removeIf(uuid -> {
            MobEntity mob = (MobEntity) world.getEntity(uuid);
            return mob == null || !mob.isAlive();
        });
        return spawnedMobs.isEmpty();
    }

    private void updateParticipatingPlayers(ServerWorld world, BlockPos pos) {
        Box detectionBox = new Box(pos).expand(DETECTION_RADIUS);
        List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(PlayerEntity.class, detectionBox,
                player -> !player.isSpectator());

        for (PlayerEntity player : nearbyPlayers) {
            participatingPlayers.add(player.getUuid());
        }
    }

    private PlayerEntity findNearestParticipatingPlayer(ServerWorld world, BlockPos pos) {
        PlayerEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (UUID playerId : participatingPlayers) {
            PlayerEntity player = world.getPlayerByUuid(playerId);
            if (player != null) {
                double distance = player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ());
                if (distance < nearestDistance) {
                    nearest = player;
                    nearestDistance = distance;
                }
            }
        }
        return nearest;
    }

    private void advanceToNextWave(ServerWorld world, BlockPos pos, BlockState state) {
        currentWave++;
        setupWave(currentWave);

        if (getCachedState().getBlock() instanceof BasaltSpawnerBlock block) {
            block.updateWave(world, pos, currentWave);
        }

        world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE,
                SoundCategory.BLOCKS, 1.0F, 0.8F + currentWave * 0.1F);

        markDirty();
    }

    private void completeTrial(ServerWorld world, BlockPos pos, BlockState state) {
        trialActive = false;
        onCooldown = true;
        cooldownEndTime = world.getTime() + COOLDOWN_DURATION;

        spawnLootRewards(world, pos);

        if (getCachedState().getBlock() instanceof BasaltSpawnerBlock block) {
            block.completeTrial(world, pos, state);
        }

        participatingPlayers.clear();
        trialStarterId = null;
        currentWave = 1;
        mobsToSpawn = 0;
        spawnedMobs.clear();

        markDirty();
    }

    private void spawnLootRewards(ServerWorld world, BlockPos pos) {
    }

    private void cancelTrial(ServerWorld world, BlockPos pos, BlockState state) {
        trialActive = false;
        world.setBlockState(pos, state.with(BasaltSpawnerBlock.ACTIVE, false)
                .with(BasaltSpawnerBlock.WAVE, 1));

        participatingPlayers.clear();
        trialStarterId = null;
        currentWave = 1;
        mobsToSpawn = 0;
        spawnedMobs.clear();

        markDirty();
    }

    private void spawnTrialParticles(ServerWorld world, BlockPos pos, BlockState state) {
        if (world.getTime() % 8 == 0) {
            for (int i = 0; i < 12; i++) {
                double angle = (i / 12.0) * 2 * Math.PI;
                double x = pos.getX() + 0.5 + Math.cos(angle) * 3;
                double z = pos.getZ() + 0.5 + Math.sin(angle) * 3;
                double y = pos.getY() + 1.5;

                world.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0.05, 0);
                world.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.1, 0);
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        nbt.putInt("currentWave", currentWave);
        nbt.putInt("mobsToSpawn", mobsToSpawn);
        nbt.putInt("mobsSpawned", mobsSpawned);
        nbt.putBoolean("trialActive", trialActive);
        nbt.putBoolean("onCooldown", onCooldown);
        nbt.putLong("cooldownEndTime", cooldownEndTime);

        if (trialStarterId != null) {
            nbt.putUuid("trialStarterId", trialStarterId);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        currentWave = nbt.getInt("currentWave");
        mobsToSpawn = nbt.getInt("mobsToSpawn");
        mobsSpawned = nbt.getInt("mobsSpawned");
        trialActive = nbt.getBoolean("trialActive");
        onCooldown = nbt.getBoolean("onCooldown");
        cooldownEndTime = nbt.getLong("cooldownEndTime");

        if (nbt.containsUuid("trialStarterId")) {
            trialStarterId = nbt.getUuid("trialStarterId");
        }
    }
}