package cassetu.mystbornhorizons.entity.custom;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import cassetu.mystbornhorizons.entity.ModEntities;
import cassetu.mystbornhorizons.sound.ModSounds;
import cassetu.mystbornhorizons.network.ModPackets;
import cassetu.mystbornhorizons.network.BossMusicPacket;

import java.util.ArrayList;
import java.util.List;

public class HavenicaEntity extends HostileEntity {
    private ServerBossBar bossBar;

    private int shockwaveCooldown = 0;
    private static final int SHOCKWAVE_COOLDOWN = 300;
    private boolean shockwaveActive = false;
    private int shockwaveTimer = 0;
    private List<Double> waveDistances = new ArrayList<>();
    private List<Integer> waveDelays = new ArrayList<>();
    private static final double WAVE_SPEED = 0.8;
    private static final double MAX_WAVE_DISTANCE = 15.0;

    private int toxicLaserCooldown = 0;
    private int rootNetworkCooldown = 0;
    private int pollenStormCooldown = 0;
    private boolean isGardensWrathActive = false;
    private static final int LASER_COOLDOWN = 200;
    private static final int ROOT_COOLDOWN = 160;

    private Vec3d[] lockedLaserTargets = new Vec3d[3];

    private int teleportCooldown = 0;
    private static final int TELEPORT_COOLDOWN = 240;
    private int teleportDelayTicks = 0;
    private static final int TELEPORT_DELAY = 40;

    private boolean toxicLaserCharging = false;
    private int toxicLaserChargeTicks = 0;
    private PlayerEntity toxicLaserTarget = null;
    private static final int LASER_CHARGE_TIME = 75;

    private int healingTick = 0;
    private static final int HEALING_INTERVAL = 20;
    private static final float HEALING_AMOUNT = 28f;

    private boolean rootNetworkActive = false;
    private int rootNetworkTimer = 0;
    private static final int ROOT_PILLAR_PHASE = 60;
    private static final int ROOT_LINE_PHASE = 220;
    private List<Vec3d> rootPillars = new ArrayList<>();

    private boolean hasSpawnedCores = false;
    private boolean hasSentMusicStart = false;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public HavenicaEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setCustomNameVisible(true);
        if (!world.isClient()) {
            this.bossBar = new ServerBossBar(
                    this.getDisplayName(),
                    BossBar.Color.GREEN,
                    BossBar.Style.PROGRESS
            );
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        if (this.bossBar != null) {
            this.bossBar.addPlayer(player);
        }
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        if (this.bossBar != null) {
            this.bossBar.removePlayer(player);
        }
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        if (this.bossBar != null) {
            this.bossBar.clearPlayers();
        }

        if (!this.getWorld().isClient()) {
            stopBossMusicForNearbyPlayers();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.getWorld().isClient()) {
            stopBossMusicForNearbyPlayers();
        }
        super.remove(reason);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1220)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 20)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.5)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 100.0)
                .add(EntityAttributes.GENERIC_ARMOR, 11.0);

    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    private void spawnHavenCores() {
        if (this.getWorld().isClient() || hasSpawnedCores) return;

        List<BlockPos> validFroglights = new ArrayList<>();
        BlockPos currentPos = this.getBlockPos();

        for (int x = -25; x <= 25; x++) {
            for (int y = -25; y <= 25; y++) {
                for (int z = -25; z <= 25; z++) {
                    BlockPos checkPos = currentPos.add(x, y, z);
                    Block block = this.getWorld().getBlockState(checkPos).getBlock();

                    if (block == Blocks.OCHRE_FROGLIGHT ||
                            block == Blocks.VERDANT_FROGLIGHT ||
                            block == Blocks.PEARLESCENT_FROGLIGHT) {

                        BlockPos above1 = checkPos.up();
                        BlockPos above2 = checkPos.up(2);
                        BlockPos spawnPos = checkPos.up(2);

                        if (this.getWorld().getBlockState(above1).isAir() &&
                                this.getWorld().getBlockState(above2).isAir()) {
                            validFroglights.add(spawnPos);
                        }
                    }
                }
            }
        }

        for (BlockPos spawnPos : validFroglights) {
            HavenCoreEntity havenCore = ModEntities.HAVEN_CORE.create(this.getWorld());
            if (havenCore != null) {
                havenCore.refreshPositionAndAngles(
                        spawnPos.getX() + 0.5,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.5,
                        0,
                        0
                );

                this.getWorld().spawnEntity(havenCore);

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.PORTAL,
                        spawnPos.getX() + 0.5, spawnPos.getY() + 1.0, spawnPos.getZ() + 0.5,
                        15,
                        0.5, 1.0, 0.5,
                        0.1
                );
            }
        }

        hasSpawnedCores = true;

        if (!validFroglights.isEmpty()) {
            Box messageRange = this.getBoundingBox().expand(25.0);
            this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                    player -> player.sendMessage(Text.literal("Haven Cores emerge from the froglights!"), true)
            );

            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_BEACON_ACTIVATE,
                    this.getSoundCategory(), 1.0f, 0.7f);
        }
    }

    private void startBossMusicForNearbyPlayers() {
        Box musicRange = this.getBoundingBox().expand(35.0);
        List<ServerPlayerEntity> playersInRange = this.getWorld().getNonSpectatingEntities(ServerPlayerEntity.class, musicRange);

        for (ServerPlayerEntity player : playersInRange) {
            ModPackets.sendToPlayer(new BossMusicPacket(true, ModSounds.HAVENICA_BOSS_MUSIC), player);
        }
    }

    private void stopBossMusicForNearbyPlayers() {
        Box musicRange = this.getBoundingBox().expand(50.0);
        List<ServerPlayerEntity> playersInRange = this.getWorld().getNonSpectatingEntities(ServerPlayerEntity.class, musicRange);

        for (ServerPlayerEntity player : playersInRange) {
            ModPackets.sendToPlayer(new BossMusicPacket(false, null), player);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.bossBar != null) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        }

        if (!this.getWorld().isClient()) {
            if (!hasSpawnedCores) {
                spawnHavenCores();
            }

            if (!hasSentMusicStart) {
                startBossMusicForNearbyPlayers();
                hasSentMusicStart = true;
            }

            if (this.isOnFire() && !this.hasStatusEffect(StatusEffects.GLOWING)) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 600, 0));
            }

            if (toxicLaserCooldown > 0) toxicLaserCooldown--;
            if (rootNetworkCooldown > 0) rootNetworkCooldown--;
            if (pollenStormCooldown > 0) pollenStormCooldown--;
            if (teleportCooldown > 0) teleportCooldown--;
            if (shockwaveCooldown > 0) shockwaveCooldown--;

            if (shockwaveActive) { tickShockwaveBlast(); }

            if (rootNetworkActive) {
                rootNetworkTimer++;

                if (rootNetworkTimer <= ROOT_PILLAR_PHASE) {
                    spawnRootPillarParticles();
                } else if (rootNetworkTimer <= ROOT_LINE_PHASE) {
                    spawnRootLineParticles();
                    damagePlayersInLines();
                } else {
                    rootNetworkActive = false;
                    rootNetworkTimer = 0;
                    rootPillars.clear();
                    rootNetworkCooldown = ROOT_COOLDOWN;
                }
            }

            if (toxicLaserCharging) {
                toxicLaserChargeTicks++;

                if (hasValidLaserTargets()) {
                    createTripleLaserChargingEffect();

                    if (toxicLaserChargeTicks >= LASER_CHARGE_TIME) {
                        fireTripleToxicLaser();
                        toxicLaserCharging = false;
                        toxicLaserChargeTicks = 0;
                        toxicLaserTarget = null;
                        clearLaserTargets();
                    }
                } else {
                    toxicLaserCharging = false;
                    toxicLaserChargeTicks = 0;
                    toxicLaserTarget = null;
                    clearLaserTargets();
                }
            }

            if (teleportDelayTicks > 0) {
                teleportDelayTicks--;
                if (teleportDelayTicks == 0 && teleportCooldown <= 0) {
                    tryTeleportToFroglight();
                }
            }

            healingTick++;
            int healingInterval = this.hasStatusEffect(StatusEffects.GLOWING) ? HEALING_INTERVAL * 5 : HEALING_INTERVAL;

            if (healingTick >= healingInterval) {
                if (this.getHealth() < this.getMaxHealth()) {
                    this.heal(HEALING_AMOUNT);

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.HEART,
                            this.getX(), this.getY() + 1.5, this.getZ(),
                            3,
                            0.3, 0.3, 0.3,
                            0.1
                    );

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            this.getX(), this.getY() + 1.0, this.getZ(),
                            5,
                            0.5, 0.5, 0.5,
                            0.05
                    );
                }
                healingTick = 0;
            }

            if (!isGardensWrathActive && this.getHealth() <= this.getMaxHealth() * 0.5f) {
                activateGardensWrath();
            }

            PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 20.0);
            if (nearestPlayer != null) {
                if (toxicLaserCooldown <= 0 && !toxicLaserCharging) useToxicLaser(nearestPlayer);
                if (rootNetworkCooldown <= 0 && !rootNetworkActive) useRootNetwork(nearestPlayer);
                if (shockwaveCooldown <= 0) useShockwaveBlast();
            }
        }

        if (this.getWorld().isClient()) {
            this.setupAnimationStates();
        }
    }

    private void useToxicLaser(PlayerEntity target) {
        if (!hasLineOfSight(target)) {
            return;
        }

        toxicLaserCharging = true;
        toxicLaserChargeTicks = 0;
        toxicLaserTarget = target;

        Vec3d playerPos = new Vec3d(target.getX(), target.getY() + 1.0, target.getZ());
        Vec3d directionToPlayer = playerPos.subtract(this.getX(), this.getY() + 1.5, this.getZ()).normalize();

        Vec3d extendedTarget = playerPos.add(directionToPlayer.multiply(12.0));

        Vec3d perpendicular = new Vec3d(-directionToPlayer.z, 0, directionToPlayer.x).normalize();

        lockedLaserTargets[0] = extendedTarget;
        lockedLaserTargets[1] = extendedTarget.add(perpendicular.multiply(1.2));
        lockedLaserTargets[2] = extendedTarget.subtract(perpendicular.multiply(1.2));

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.toxic_laser_charging"), true)
        );

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT,
                this.getSoundCategory(), 1.0f, 0.5f);
    }

    private boolean hasLineOfSight(PlayerEntity target) {
        Vec3d start = new Vec3d(this.getX(), this.getY() + 1.5, this.getZ());
        Vec3d end = new Vec3d(target.getX(), target.getY() + 1.0, target.getZ());

        return this.getWorld().raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                this
        )).getType() == net.minecraft.util.hit.HitResult.Type.MISS;
    }

    private boolean hasValidLaserTargets() {
        return lockedLaserTargets[0] != null && lockedLaserTargets[1] != null && lockedLaserTargets[2] != null;
    }

    private void clearLaserTargets() {
        lockedLaserTargets[0] = null;
        lockedLaserTargets[1] = null;
        lockedLaserTargets[2] = null;
    }

    private void createTripleLaserChargingEffect() {
        if (!hasValidLaserTargets() || this.getWorld().isClient()) return;

        Vec3d start = new Vec3d(this.getX(), this.getY() + 1.5, this.getZ());
        double intensity = (double) toxicLaserChargeTicks / LASER_CHARGE_TIME;
        int particleCount = (int)(3 + intensity * 7);

        for (int laserIndex = 0; laserIndex < 3; laserIndex++) {
            Vec3d end = lockedLaserTargets[laserIndex];
            Vec3d direction = end.subtract(start);
            double distance = direction.length();
            direction = direction.normalize();

            for (double d = 0; d < distance; d += 0.4) {
                Vec3d particlePos = start.add(direction.multiply(d));

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.COMPOSTER,
                        particlePos.x, particlePos.y + 0.1, particlePos.z,
                        2,
                        0.1, 0.1, 0.1,
                        0.0
                );

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        particlePos.x, particlePos.y + 0.5, particlePos.z,
                        1,
                        0.05, 0.05, 0.05,
                        0.0
                );
            }

            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI/8) {
                double radius = 2.0 - (intensity * 0.5);
                double x = end.x + Math.cos(angle) * radius;
                double z = end.z + Math.sin(angle) * radius;

                ((ServerWorld)this.getWorld()).spawnParticles(
                        intensity > 0.7 ? ParticleTypes.FLAME : ParticleTypes.SMOKE,
                        x, end.y + 0.1, z,
                        1,
                        0.1, 0.1, 0.1,
                        0.0
                );
            }
        }

        if (toxicLaserChargeTicks % 10 == 0) {
            float pitch = 0.5f + (float)intensity * 1.0f;
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_WEEPING_VINES_HIT,
                    this.getSoundCategory(), 0.3f, pitch);
        }
    }

    private void fireTripleToxicLaser() {
        if (!hasValidLaserTargets() || this.getWorld().isClient()) return;

        Vec3d start = new Vec3d(this.getX(), this.getY() + 1.5, this.getZ());

        for (int laserIndex = 0; laserIndex < 3; laserIndex++) {
            Vec3d end = lockedLaserTargets[laserIndex];
            Vec3d direction = end.subtract(start);
            double distance = direction.length();
            direction = direction.normalize();

            for (double d = 0; d < distance; d += 0.2) {
                Vec3d particlePos = start.add(direction.multiply(d));

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.EXPLOSION,
                        particlePos.x, particlePos.y, particlePos.z,
                        3,
                        0.2, 0.2, 0.2,
                        0.1
                );

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.GLOW_SQUID_INK,
                        particlePos.x, particlePos.y, particlePos.z,
                        5,
                        0.3, 0.3, 0.3,
                        0.1
                );

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.COMPOSTER,
                        particlePos.x, particlePos.y, particlePos.z,
                        3,
                        0.2, 0.2, 0.2,
                        0.05
                );
            }

            Box damageBox = new Box(end.x - 2.5, end.y - 1, end.z - 2.5,
                    end.x + 2.5, end.y + 2, end.z + 2.5);

            this.getWorld().getNonSpectatingEntities(PlayerEntity.class, damageBox).forEach(player -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 160, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 1));
                player.damage(this.getDamageSources().magic(), 7.0f);

                for (double angle = 0; angle < Math.PI * 2; angle += Math.PI/6) {
                    double radius = 1.5;
                    double x = player.getX() + Math.cos(angle) * radius;
                    double z = player.getZ() + Math.sin(angle) * radius;

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.FLAME,
                            x, player.getY() + 1.0, z,
                            2,
                            0.1, 0.2, 0.1,
                            0.05
                    );
                }
            });
        }

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                this.getSoundCategory(), 1.0f, 1.5f);
        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE,
                this.getSoundCategory(), 1.0f, 1.5f);

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.toxic_laser_fire"), true)
        );

        toxicLaserCooldown = LASER_COOLDOWN;
        clearLaserTargets();

        if (this.random.nextFloat() < 0.30f && teleportCooldown <= 0) {
            teleportDelayTicks = TELEPORT_DELAY;

            Box teleportMessageRange = this.getBoundingBox().expand(20.0);
            this.getWorld().getNonSpectatingEntities(PlayerEntity.class, teleportMessageRange).forEach(
                    player -> player.sendMessage(Text.literal("Havenica prepares to teleport..."), true)
            );
        }
    }

    private void damagePlayersInLines() {
        if (this.getWorld().isClient()) return;

        for (int i = 0; i < rootPillars.size(); i++) {
            Vec3d start = rootPillars.get(i);
            Vec3d end = rootPillars.get((i + 1) % rootPillars.size());

            Vec3d direction = end.subtract(start);
            double distance = direction.length();
            direction = direction.normalize();

            for (double d = 0; d < distance; d += 0.5) {
                Vec3d checkPos = start.add(direction.multiply(d));

                Box damageBox = new Box(
                        checkPos.x - 1.0, checkPos.y - 0.5, checkPos.z - 1.0,
                        checkPos.x + 1.0, checkPos.y + 2.5, checkPos.z + 1.0
                );

                this.getWorld().getNonSpectatingEntities(PlayerEntity.class, damageBox).forEach(player -> {
                    if (!player.hasStatusEffect(StatusEffects.SLOWNESS) || player.getStatusEffect(StatusEffects.SLOWNESS).getDuration() < 20) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 2));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 80, 1));
                        player.damage(this.getDamageSources().magic(), 6.0f);

                        player.getInventory().armor.forEach(itemStack -> {
                            if (!itemStack.isEmpty() && itemStack.isDamageable()) {
                                int currentDamage = itemStack.getDamage();
                                int maxDamage = itemStack.getMaxDamage();
                                int newDamage = Math.min(maxDamage - 1, currentDamage + 15);
                                itemStack.setDamage(newDamage);
                            }
                        });

                        if (!player.getMainHandStack().isEmpty() && player.getMainHandStack().isDamageable()) {
                            player.getMainHandStack().setDamage(player.getMainHandStack().getDamage() + 5);
                        }

                        for (int p = 0; p < 8; p++) {
                            double angle = (p / 8.0) * Math.PI * 2;
                            double radius = 1.2;
                            double x = player.getX() + Math.cos(angle) * radius;
                            double z = player.getZ() + Math.sin(angle) * radius;

                            ((ServerWorld)this.getWorld()).spawnParticles(
                                    ParticleTypes.CRIT,
                                    x, player.getY() + 1.0, z,
                                    2,
                                    0.1, 0.1, 0.1,
                                    0.05
                            );
                        }
                    }
                });
            }
        }
    }

    private void useShockwaveBlast() {
        shockwaveActive = true;
        shockwaveTimer = 0;
        waveDistances.clear();
        waveDelays.clear();

        waveDistances.add(0.0);
        waveDistances.add(0.0);
        waveDistances.add(0.0);

        waveDelays.add(0);
        waveDelays.add(20);
        waveDelays.add(40);

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.shockwave_blast"), true)
        );

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM,
                this.getSoundCategory(), 1.0f, 0.8f);

        shockwaveCooldown = SHOCKWAVE_COOLDOWN;
    }

    private void tickShockwaveBlast() {
        if (!shockwaveActive || this.getWorld().isClient()) return;

        shockwaveTimer++;

        for (int i = 0; i < waveDistances.size(); i++) {
            if (shockwaveTimer >= waveDelays.get(i)) {
                double currentDistance = waveDistances.get(i);

                if (currentDistance < MAX_WAVE_DISTANCE) {
                    createShockwaveRing(currentDistance);
                    damagePlayersInWave(currentDistance);
                    waveDistances.set(i, currentDistance + WAVE_SPEED);
                }
            }
        }

        boolean allWavesFinished = true;
        for (double distance : waveDistances) {
            if (distance < MAX_WAVE_DISTANCE) {
                allWavesFinished = false;
                break;
            }
        }

        if (allWavesFinished) {
            shockwaveActive = false;
            shockwaveTimer = 0;
            waveDistances.clear();
            waveDelays.clear();
        }
    }

    private void createShockwaveRing(double radius) {
        if (radius <= 0) return;

        double circumference = 2 * Math.PI * radius;
        int particleCount = Math.max(8, (int)(circumference / 0.5));

        for (int i = 0; i < particleCount; i++) {
            double angle = (i / (double)particleCount) * Math.PI * 2;
            double x = this.getX() + Math.cos(angle) * radius;
            double z = this.getZ() + Math.sin(angle) * radius;

            ((ServerWorld)this.getWorld()).spawnParticles(
                    ParticleTypes.BUBBLE_POP,
                    x, this.getY() + 0.1, z,
                    3,
                    0.1, 0.1, 0.1,
                    0.0
            );

            ((ServerWorld)this.getWorld()).spawnParticles(
                    ParticleTypes.CRIT,
                    x, this.getY() + 0.3, z,
                    1,
                    0.05, 0.05, 0.05,
                    0.0
            );
        }

        if (radius % 3.0 < WAVE_SPEED) {
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_STONE_BREAK,
                    this.getSoundCategory(), 0.6f, 0.9f);
        }
    }

    private void damagePlayersInWave(double waveRadius) {
        double waveThickness = 0.8;

        Box searchBox = new Box(
                this.getX() - waveRadius - waveThickness, this.getY() - 0.5, this.getZ() - waveRadius - waveThickness,
                this.getX() + waveRadius + waveThickness, this.getY() + 1.5, this.getZ() + waveRadius + waveThickness
        );

        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, searchBox).forEach(player -> {
            double distanceToPlayer = Math.sqrt(
                    Math.pow(player.getX() - this.getX(), 2) +
                            Math.pow(player.getZ() - this.getZ(), 2)
            );

            if (Math.abs(distanceToPlayer - waveRadius) <= waveThickness &&
                    player.getY() <= this.getY() + 1.2) {

                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
                player.damage(this.getDamageSources().magic(), 6.0f);

                Vec3d knockbackDir = new Vec3d(
                        player.getX() - this.getX(),
                        0.3,
                        player.getZ() - this.getZ()
                ).normalize().multiply(1.2);

                player.setVelocity(player.getVelocity().add(knockbackDir));

                for (int p = 0; p < 8; p++) {
                    double angle = (p / 8.0) * Math.PI * 2;
                    double particleRadius = 1.0;
                    double x = player.getX() + Math.cos(angle) * particleRadius;
                    double z = player.getZ() + Math.sin(angle) * particleRadius;

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.DAMAGE_INDICATOR,
                            x, player.getY() + 1.0, z,
                            1,
                            0.1, 0.1, 0.1,
                            0.05
                    );
                }
            }
        });
    }

    private void activateGardensWrath() {
        if (!this.getWorld().isClient()) {
            for (double y = 0; y < 3; y += 0.2) {
                for (int root = 0; root < 4; root++) {
                    double baseAngle = (root * Math.PI / 2) + (y * 2);
                    double radius = 0.8 - (y * 0.1);

                    double x = this.getX() + Math.cos(baseAngle) * radius;
                    double z = this.getZ() + Math.sin(baseAngle) * radius;

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.MYCELIUM,
                            x, this.getY() + y, z,
                            1,
                            0.05, 0.05, 0.05,
                            0.0
                    );

                    for (int branch = 0; branch < 2; branch++) {
                        double branchAngle = baseAngle + (Math.sin(y * 3) * 0.5);
                        double branchRadius = radius * 0.7;
                        double branchX = x + Math.cos(branchAngle) * 0.3;
                        double branchZ = z + Math.sin(branchAngle) * 0.3;

                        ((ServerWorld)this.getWorld()).spawnParticles(
                                ParticleTypes.ASH,
                                branchX, this.getY() + y, branchZ,
                                1,
                                0.05, 0.05, 0.05,
                                0.0
                        );
                    }
                }
            }

            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI/8) {
                for (double r = 0.5; r < 2; r += 0.3) {
                    double x = this.getX() + Math.cos(angle) * r;
                    double z = this.getZ() + Math.sin(angle) * r;

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.MYCELIUM,
                            x, this.getY() + 0.1, z,
                            1,
                            0.05, 0, 0.05,
                            0.0
                    );
                }
            }
        }

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.gardens_wrath"), true)
        );
        isGardensWrathActive = true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        float cappedAmount = Math.min(amount, 50f);
        return super.damage(source, cappedAmount);
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return !effect.getEffectType().equals(StatusEffects.POISON) && super.canHaveStatusEffect(effect);
    }

    private void tryTeleportToFroglight() {
        if (this.getWorld().isClient()) return;

        List<BlockPos> validFroglights = new ArrayList<>();
        BlockPos currentPos = this.getBlockPos();

        for (int x = -15; x <= 15; x++) {
            for (int y = -15; y <= 15; y++) {
                for (int z = -15; z <= 15; z++) {
                    BlockPos checkPos = currentPos.add(x, y, z);
                    Block block = this.getWorld().getBlockState(checkPos).getBlock();

                    if (block == Blocks.OCHRE_FROGLIGHT ||
                            block == Blocks.VERDANT_FROGLIGHT ||
                            block == Blocks.PEARLESCENT_FROGLIGHT) {

                        BlockPos above1 = checkPos.up();
                        BlockPos above2 = checkPos.up(2);

                        if (this.getWorld().getBlockState(above1).isAir() &&
                                this.getWorld().getBlockState(above2).isAir()) {
                            validFroglights.add(above1);
                        }
                    }
                }
            }
        }

        if (!validFroglights.isEmpty()) {
            BlockPos teleportPos = validFroglights.get(this.random.nextInt(validFroglights.size()));

            ((ServerWorld)this.getWorld()).spawnParticles(
                    ParticleTypes.PORTAL,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    20,
                    0.5, 1.0, 0.5,
                    0.1
            );

            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                    this.getSoundCategory(), 1.0f, 1.0f);

            this.refreshPositionAndAngles(teleportPos.getX() + 0.5, teleportPos.getY(), teleportPos.getZ() + 0.5, this.getYaw(), this.getPitch());

            ((ServerWorld)this.getWorld()).spawnParticles(
                    ParticleTypes.PORTAL,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    20,
                    0.5, 1.0, 0.5,
                    0.1
            );

            Box messageRange = this.getBoundingBox().expand(20.0);
            this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                    player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.teleport"), true)
            );

            teleportCooldown = TELEPORT_COOLDOWN;
        } else {
            Box messageRange = this.getBoundingBox().expand(20.0);
            this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                    player -> player.sendMessage(Text.literal("Havenica searches for a froglight to teleport to..."), true)
            );
        }
    }

    private void useRootNetwork(PlayerEntity target) {
        rootNetworkActive = true;
        rootNetworkTimer = 0;
        rootPillars.clear();

        double centerX = target.getX();
        double centerZ = target.getZ();

        rootPillars.add(new Vec3d(centerX, target.getY(), centerZ));

        int pillarCount = isGardensWrathActive ? 8 : 6;
        for (int i = 0; i < pillarCount; i++) {
            double angle = (i / (double)pillarCount) * Math.PI * 2;
            double radius = 8.0 + this.random.nextDouble() * 4.0;
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            rootPillars.add(new Vec3d(x, target.getY(), z));
        }

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.root_network"), true)
        );

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_ROOTED_DIRT_BREAK,
                this.getSoundCategory(), 1.0f, 0.7f);
    }

    private void spawnRootPillarParticles() {
        if (this.getWorld().isClient()) return;

        for (Vec3d pillar : rootPillars) {
            for (double y = 0; y < 5; y += 0.3) {
                double radius = 0.8;
                int particlesPerRing = 8;

                for (int p = 0; p < particlesPerRing; p++) {
                    double angle = (p / (double)particlesPerRing) * Math.PI * 2;
                    double x = pillar.x + Math.cos(angle) * radius;
                    double z = pillar.z + Math.sin(angle) * radius;

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.ITEM_COBWEB,
                            x, pillar.y + y, z,
                            1,
                            0.1, 0.1, 0.1,
                            0.0
                    );

                    if (y > 3) {
                        ((ServerWorld)this.getWorld()).spawnParticles(
                                ParticleTypes.END_ROD,
                                x, pillar.y + y, z,
                                1,
                                0.05, 0.05, 0.05,
                                0.0
                        );
                    }
                }
            }
        }

        if (rootNetworkTimer == ROOT_PILLAR_PHASE - 10) {
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_ROOTED_DIRT_PLACE,
                    this.getSoundCategory(), 1.0f, 1.0f);
        }
    }

    private void spawnRootLineParticles() {
        if (this.getWorld().isClient()) return;

        for (int i = 0; i < rootPillars.size(); i++) {
            Vec3d start = rootPillars.get(i);
            Vec3d end = rootPillars.get((i + 1) % rootPillars.size());

            Vec3d direction = end.subtract(start);
            double distance = direction.length();
            direction = direction.normalize();

            for (double d = 0; d < distance; d += 0.3) {
                Vec3d particlePos = start.add(direction.multiply(d));

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.CRIT,
                        particlePos.x, particlePos.y, particlePos.z,
                        2,
                        0.1, 0.1, 0.1,
                        0.0
                );

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.SCRAPE,
                        particlePos.x, particlePos.y + 0.5, particlePos.z,
                        1,
                        0.05, 0.05, 0.05,
                        0.0
                );
            }
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.BLOCK_MOSS_STEP;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ITEM_BONE_MEAL_USE;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_MOSS_BREAK;
    }
}