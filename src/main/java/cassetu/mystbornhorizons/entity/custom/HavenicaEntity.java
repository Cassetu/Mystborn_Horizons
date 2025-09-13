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

import java.util.ArrayList;
import java.util.List;

public class HavenicaEntity extends HostileEntity {

    private ServerBossBar bossBar;
    private int toxicLaserCooldown = 0;
    private int rootNetworkCooldown = 0;
    private int pollenStormCooldown = 0;
    private int tomahawkBarrageCooldown = 0;
    private boolean isGardensWrathActive = false;
    private static final int LASER_COOLDOWN = 200;
    private static final int ROOT_COOLDOWN = 160;
    private static final int POLLEN_COOLDOWN = 300;
    private static final int TOMAHAWK_COOLDOWN = 180;

    private Vec3d[] lockedLaserTargets = new Vec3d[3];

    private int teleportCooldown = 0;
    private static final int TELEPORT_COOLDOWN = 240;
    private int teleportDelayTicks = 0;
    private static final int TELEPORT_DELAY = 40;

    private boolean toxicLaserCharging = false;
    private int toxicLaserChargeTicks = 0;
    private PlayerEntity toxicLaserTarget = null;
    private static final int LASER_CHARGE_TIME = 60;

    private int healingTick = 0;
    private static final int HEALING_INTERVAL = 20;
    private static final float HEALING_AMOUNT = 4.5f;

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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 5000)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 20)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.5)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 100.0)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0);
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

    @Override
    public void tick() {
        super.tick();

        if (this.bossBar != null) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        }

        if (!this.getWorld().isClient()) {
            if (this.isOnFire() && !this.hasStatusEffect(StatusEffects.GLOWING)) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 600, 0));
            }

            if (toxicLaserCooldown > 0) toxicLaserCooldown--;
            if (rootNetworkCooldown > 0) rootNetworkCooldown--;
            if (pollenStormCooldown > 0) pollenStormCooldown--;
            if (teleportCooldown > 0) teleportCooldown--;
            if (tomahawkBarrageCooldown > 0) tomahawkBarrageCooldown--;

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
            int healingInterval = this.hasStatusEffect(StatusEffects.GLOWING) ? HEALING_INTERVAL * 6 : HEALING_INTERVAL;

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
                if (rootNetworkCooldown <= 0) useRootNetwork(nearestPlayer);
                if (pollenStormCooldown <= 0) usePollenStorm();
                if (tomahawkBarrageCooldown <= 0) useTomahawkBarrage(nearestPlayer);
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

        Vec3d perpendicular = new Vec3d(-directionToPlayer.z, 0, directionToPlayer.x).normalize();

        lockedLaserTargets[0] = playerPos;
        lockedLaserTargets[1] = playerPos.add(perpendicular.multiply(1.2));
        lockedLaserTargets[2] = playerPos.subtract(perpendicular.multiply(1.2));

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

            for (double d = 0; d < distance; d += 0.3) {
                Vec3d particlePos = start.add(direction.multiply(d));

                if (laserIndex == 0) {
                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.COMPOSTER,
                            particlePos.x, particlePos.y, particlePos.z,
                            particleCount,
                            0.1, 0.1, 0.1,
                            0.0
                    );
                } else {
                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.SNEEZE,
                            particlePos.x, particlePos.y, particlePos.z,
                            particleCount,
                            0.1, 0.1, 0.1,
                            0.0
                    );
                }

                if (intensity > 0.5) {
                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.SMOKE,
                            particlePos.x, particlePos.y, particlePos.z,
                            (int)(intensity * 3),
                            0.2, 0.2, 0.2,
                            0.05
                    );
                }
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
                        ParticleTypes.SNEEZE,
                        particlePos.x, particlePos.y, particlePos.z,
                        5,
                        0.3, 0.3, 0.3,
                        0.1
                );

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.SPORE_BLOSSOM_AIR,
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
    }

    private void useTomahawkBarrage(PlayerEntity target) {
        if (!this.getWorld().isClient()) {
            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI/16) {
                double radius = 2.0;
                double x = this.getX() + Math.cos(angle) * radius;
                double z = this.getZ() + Math.sin(angle) * radius;

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.FLAME,
                        x, this.getY() + 0.5, z,
                        2,
                        0.1, 0.1, 0.1,
                        0.05
                );

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.ENCHANT,
                        x, this.getY() + 1.0, z,
                        1,
                        0.1, 0.1, 0.1,
                        0.1
                );
            }

            int tomahawkCount = isGardensWrathActive ? 5 : 3;

            for (int i = 0; i < tomahawkCount; i++) {
                double spreadAngle = (i - (tomahawkCount - 1) / 2.0) * 0.3;

                Vec3d directionToTarget = new Vec3d(
                        target.getX() - this.getX(),
                        target.getY() + 1.0 - (this.getY() + 2.0),
                        target.getZ() - this.getZ()
                ).normalize();

                double cos = Math.cos(spreadAngle);
                double sin = Math.sin(spreadAngle);
                Vec3d spreadDirection = new Vec3d(
                        directionToTarget.x * cos - directionToTarget.z * sin,
                        directionToTarget.y,
                        directionToTarget.x * sin + directionToTarget.z * cos
                );

                Vec3d spawnPos = new Vec3d(
                        this.getX(),
                        this.getY() + 2.5,
                        this.getZ()
                );

                float damage = isGardensWrathActive ? 15.0f : 12.0f;
                Vec3d velocity = spreadDirection.multiply(1.8);

                TomahawkProjectileEntity tomahawk = new TomahawkProjectileEntity(
                        this.getWorld(), this, velocity, damage);

                tomahawk.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                tomahawk.setBossSummoned(true);
                this.getWorld().spawnEntity(tomahawk);

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.CRIT,
                        spawnPos.x, spawnPos.y, spawnPos.z,
                        5,
                        0.2, 0.2, 0.2,
                        0.1
                );
            }

            for (int i = 0; i < 10; i++) {
                double angle = (i / 10.0) * Math.PI * 2;
                double radius = 1.5;
                double x = this.getX() + Math.cos(angle) * radius;
                double z = this.getZ() + Math.sin(angle) * radius;

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.SMOKE,
                        x, this.getY() + 1.5, z,
                        3,
                        0.1, 0.3, 0.1,
                        0.05
                );
            }
        }

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.tomahawk_barrage"), true)
        );

        tomahawkBarrageCooldown = TOMAHAWK_COOLDOWN;

        teleportDelayTicks = TELEPORT_DELAY;
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
        if (!this.getWorld().isClient()) {
            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI/8) {
                for (double r = 0; r < 5; r += 0.5) {
                    double x = target.getX() + Math.cos(angle) * r;
                    double z = target.getZ() + Math.sin(angle) * r;
                    double yOffset = Math.sin(r * 2) * 0.5;

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.MYCELIUM,
                            x, target.getY() + yOffset, z,
                            1,
                            0.1, 0, 0.1,
                            0
                    );
                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.ASH,
                            x, target.getY() + 0.1, z,
                            1,
                            0.1, 0.1, 0.1,
                            0
                    );
                }
            }
        }

        target.setVelocity(0, 0, 0);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 4));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 1));
        target.damage(this.getDamageSources().magic(), 4.0f);

        target.getInventory().armor.forEach(itemStack -> {
            if (!itemStack.isEmpty() && itemStack.isDamageable()) {
                int currentDamage = itemStack.getDamage();
                int maxDamage = itemStack.getMaxDamage();
                int newDamage = (int)(maxDamage * 0.25);
                if (currentDamage < newDamage) {
                    itemStack.setDamage(newDamage);
                    if (!this.getWorld().isClient()) {
                        double x = target.getX();
                        double y = target.getY() + 1.0;
                        double z = target.getZ();

                        for (int i = 0; i < 8; i++) {
                            double angle = (i / 8.0) * Math.PI * 2;
                            double offsetX = Math.cos(angle) * 0.5;
                            double offsetZ = Math.sin(angle) * 0.5;

                            ((ServerWorld)this.getWorld()).spawnParticles(
                                    ParticleTypes.CRIT,
                                    x + offsetX, y, z + offsetZ,
                                    1,
                                    0.0, 0.0, 0.0,
                                    0.1
                            );

                            ((ServerWorld)this.getWorld()).spawnParticles(
                                    ParticleTypes.SMOKE,
                                    x + offsetX, y, z + offsetZ,
                                    1,
                                    0.0, 0.0, 0.0,
                                    0.05
                            );
                        }
                    }
                }
            }
        });

        if (!target.getMainHandStack().isEmpty() && target.getMainHandStack().isDamageable()) {
            target.getMainHandStack().setDamage(target.getMainHandStack().getDamage() + 2);
        }

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.root_network"), true)
        );
        rootNetworkCooldown = ROOT_COOLDOWN;
    }

    private void usePollenStorm() {
        if (!this.getWorld().isClient()) {
            for (double y = 0; y < 8; y += 0.5) {
                double radius = 4.0 - (y / 2.0);
                double particlesPerCircle = 12;
                double angleStep = (Math.PI * 2) / particlesPerCircle;

                for (double angle = 0; angle < Math.PI * 2; angle += angleStep) {
                    double x = this.getX() + Math.cos(angle + y) * radius;
                    double z = this.getZ() + Math.sin(angle + y) * radius;

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.SPORE_BLOSSOM_AIR,
                            x, this.getY() + y, z,
                            1,
                            0.1, 0.1, 0.1,
                            0.05
                    );

                    if (this.random.nextFloat() < 0.3) {
                        ((ServerWorld)this.getWorld()).spawnParticles(
                                ParticleTypes.END_ROD,
                                x, this.getY() + y, z,
                                1,
                                0.1, 0.1, 0.1,
                                0.05
                        );
                    }
                }
            }
        }

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.pollen_storm"), true)
        );
        pollenStormCooldown = POLLEN_COOLDOWN;
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
        float cappedAmount = Math.min(amount, 2.5f);

        if (source.getAttacker() instanceof PlayerEntity player) {
            float thornsDamage = isGardensWrathActive ? 6.0f : 3.0f;
            player.damage(this.getDamageSources().thorns(this), thornsDamage);

            double deltaX = player.getX() - this.getX();
            double deltaZ = player.getZ() - this.getZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            if (distance > 0) {
                double knockbackStrength = isGardensWrathActive ? 0.6 : 0.3;
                double normalizedX = (deltaX / distance) * knockbackStrength;
                double normalizedZ = (deltaZ / distance) * knockbackStrength;
                double upwardForce = isGardensWrathActive ? 0.4 : 0.2;

                player.setVelocity(normalizedX, upwardForce, normalizedZ);
                player.velocityModified = true;
            }

            if (!this.getWorld().isClient()) {
                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.CRIT,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        10,
                        0.5, 0.5, 0.5,
                        0.1
                );

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.EXPLOSION,
                        this.getX(), this.getY() + 1.0, this.getZ(),
                        3,
                        0.2, 0.2, 0.2,
                        0.1
                );

                for (double y = 0; y < 2; y += 0.2) {
                    double radius = 0.8;
                    double angle = y * Math.PI * 4;
                    double x = player.getX() + Math.cos(angle) * radius;
                    double z = player.getZ() + Math.sin(angle) * radius;

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.SMOKE,
                            x, player.getY() + y, z,
                            1,
                            0.0, 0.0, 0.0,
                            0.0
                    );
                }
            }
        }

        return super.damage(source, cappedAmount);
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return !effect.getEffectType().equals(StatusEffects.POISON) && super.canHaveStatusEffect(effect);
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