package cassetu.mystbornhorizons.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

/**
 * BasaltHowlerAttackGoal - A custom AI goal for the Basalt Howler entity
 * This goal provides a slow but powerful golem with special attack behaviors:
 * - Tremor attack that damages nearby entities when it stomps
 * - Charged howl that slows and weakens enemies within range
 */
public class BasaltHowlerAttackGoal extends Goal {
    private final PathAwareEntity basaltHowler;
    private final double speed;
    private final float attackDistanceSq;
    private final int cooldownTicks;
    private final int chargeTime;
    private final int trembleDuration;
    private final float tremorRadius;
    private final float howlRadius;
    private final float attackDamage;

    private int attackCooldown = 0;
    private int chargeTicksLeft = 0;
    private boolean isCharging = false;
    private int trembleTicksLeft = 0;
    private AttackPhase currentPhase = AttackPhase.APPROACH;

    private enum AttackPhase {
        APPROACH,    // Moving toward target
        CHARGING,    // Charging up attack
        HOWL,        // Performing howl attack
        TREMOR,      // Performing tremor attack
        COOLDOWN     // Post-attack cooldown
    }

    public BasaltHowlerAttackGoal(PathAwareEntity entity, double speed, float attackDistance, int cooldownTicks) {
        this.basaltHowler = entity;
        this.speed = speed;
        this.attackDistanceSq = attackDistance * attackDistance;
        this.cooldownTicks = cooldownTicks;
        this.chargeTime = 40; // 2 seconds at 20 ticks per second
        this.trembleDuration = 15; // 0.75 seconds of trembling
        this.tremorRadius = 3.5f; // Block radius for tremor attack
        this.howlRadius = 6.0f; // Block radius for howl attack
        this.attackDamage = 6.0f; // Base damage for attacks

        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.basaltHowler.getTarget();
        return target != null && target.isAlive() && attackCooldown <= 0;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.basaltHowler.getTarget();
        return (target != null && target.isAlive()) || isCharging || trembleTicksLeft > 0;
    }

    @Override
    public void start() {
        this.currentPhase = AttackPhase.APPROACH;
        this.attackCooldown = 0;
    }

    @Override
    public void stop() {
        this.attackCooldown = this.cooldownTicks;
        this.currentPhase = AttackPhase.APPROACH;
        this.isCharging = false;
        this.chargeTicksLeft = 0;
        this.trembleTicksLeft = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.basaltHowler.getTarget();
        if (target == null) return;

        // Handle different attack phases
        switch (currentPhase) {
            case APPROACH:
                handleApproachPhase(target);
                break;
            case CHARGING:
                handleChargingPhase(target);
                break;
            case HOWL:
                performHowlAttack(target);
                break;
            case TREMOR:
                handleTremorPhase();
                break;
            case COOLDOWN:
                attackCooldown--;
                if (attackCooldown <= 0) {
                    currentPhase = AttackPhase.APPROACH;
                }
                break;
        }
    }

    private void handleApproachPhase(LivingEntity target) {
        double distanceSq = this.basaltHowler.squaredDistanceTo(target);

        // Move toward the target
        this.basaltHowler.getNavigation().startMovingTo(target, this.speed);
        this.basaltHowler.getLookControl().lookAt(target, 30.0F, 30.0F);

        // When close enough, decide attack type and start charging
        if (distanceSq <= this.attackDistanceSq) {
            // 30% chance of howl attack, 70% chance of tremor attack when in range
            boolean useHowlAttack = this.basaltHowler.getRandom().nextFloat() < 0.3f;

            currentPhase = AttackPhase.CHARGING;
            isCharging = true;
            chargeTicksLeft = chargeTime;

            // Stop movement during charge
            this.basaltHowler.getNavigation().stop();

            // Play pre-attack sound
            this.basaltHowler.getWorld().playSound(
                    null,
                    this.basaltHowler.getX(),
                    this.basaltHowler.getY(),
                    this.basaltHowler.getZ(),
                    useHowlAttack ? SoundEvents.ENTITY_WARDEN_SONIC_CHARGE : SoundEvents.BLOCK_BASALT_BREAK,
                    SoundCategory.HOSTILE,
                    1.0F,
                    useHowlAttack ? 1.5F : 0.8F
            );
        }
    }

    private void handleChargingPhase(LivingEntity target) {
        // Visual charging effects - 1.21.1 approach using ServerWorld
        if (chargeTicksLeft % 5 == 0 && basaltHowler.getWorld() instanceof ServerWorld serverWorld) {
            double offsetX, offsetY, offsetZ;
            for (int i = 0; i < 3; i++) {
                offsetX = this.basaltHowler.getRandom().nextGaussian() * 0.2;
                offsetY = this.basaltHowler.getRandom().nextGaussian() * 0.2;
                offsetZ = this.basaltHowler.getRandom().nextGaussian() * 0.2;

                serverWorld.spawnParticles(
                        ParticleTypes.LAVA,
                        this.basaltHowler.getX() + offsetX,
                        this.basaltHowler.getY() + 0.5 + offsetY,
                        this.basaltHowler.getZ() + offsetZ,
                        1, // particle count
                        0.0, 0.05, 0.0, // velocity
                        0.0 // speed
                );

                serverWorld.spawnParticles(
                        ParticleTypes.ASH,
                        this.basaltHowler.getX() + offsetX,
                        this.basaltHowler.getY() + 0.5 + offsetY,
                        this.basaltHowler.getZ() + offsetZ,
                        2, // particle count
                        0.0, 0.2, 0.0, // velocity
                        0.02 // speed
                );
            }
        }

        // Decrease charge timer
        chargeTicksLeft--;

        // When charge complete, execute attack
        if (chargeTicksLeft <= 0) {
            isCharging = false;

            // Decide attack type (same chance as when starting charging)
            boolean useHowlAttack = this.basaltHowler.getRandom().nextFloat() < 0.3f;

            if (useHowlAttack) {
                currentPhase = AttackPhase.HOWL;
            } else {
                currentPhase = AttackPhase.TREMOR;
                trembleTicksLeft = trembleDuration;
            }
        }
    }

    private void handleTremorPhase() {
        trembleTicksLeft--;

        // Visual effects for trembling - updated for 1.21.1
        if (basaltHowler.getWorld() instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 4; i++) {
                double offsetX = this.basaltHowler.getRandom().nextFloat() * 2.0 - 1.0;
                double offsetZ = this.basaltHowler.getRandom().nextFloat() * 2.0 - 1.0;

                serverWorld.spawnParticles(
                        ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        this.basaltHowler.getX() + offsetX,
                        this.basaltHowler.getY(),
                        this.basaltHowler.getZ() + offsetZ,
                        1, // particle count
                        0.0, 0.2, 0.0, // velocity
                        0.02 // speed
                );
            }
        }

        // When trembling is complete, perform the tremor attack
        if (trembleTicksLeft <= 0) {
            performTremorAttack();
            currentPhase = AttackPhase.COOLDOWN;
            attackCooldown = cooldownTicks;
        }
    }

    private void performTremorAttack() {
        // Play tremor sound
        this.basaltHowler.getWorld().playSound(
                null,
                this.basaltHowler.getX(),
                this.basaltHowler.getY(),
                this.basaltHowler.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.HOSTILE,
                1.0F,
                0.5F
        );

        // Create a visual effect of dust and rocks - updated for 1.21.1
        if (basaltHowler.getWorld() instanceof ServerWorld serverWorld) {
            // Spawn particles in a circular pattern for tremor effect
            for (int i = 0; i < 80; i++) {
                double angle = i * (Math.PI * 2) / 80;
                double distance = tremorRadius * (0.3 + 0.7 * this.basaltHowler.getRandom().nextFloat());
                double offsetX = Math.cos(angle) * distance;
                double offsetZ = Math.sin(angle) * distance;

                serverWorld.spawnParticles(
                        ParticleTypes.LARGE_SMOKE,
                        this.basaltHowler.getX() + offsetX,
                        this.basaltHowler.getY() + 0.1,
                        this.basaltHowler.getZ() + offsetZ,
                        1, // particle count
                        0.0, 0.05, 0.0, // velocity
                        0.0 // speed
                );

                // Add falling dust particles for better effect
                if (i % 4 == 0) {
                    serverWorld.spawnParticles(
                            ParticleTypes.WHITE_ASH,
                            this.basaltHowler.getX() + offsetX,
                            this.basaltHowler.getY() + 0.3,
                            this.basaltHowler.getZ() + offsetZ,
                            1, // particle count
                            0.0, 0.0, 0.0, // velocity
                            0.0 // speed
                    );
                }
            }
        }

        // Damage and knock back entities caught in tremor
        Box attackBox = new Box(
                this.basaltHowler.getX() - tremorRadius, this.basaltHowler.getY(), this.basaltHowler.getZ() - tremorRadius,
                this.basaltHowler.getX() + tremorRadius, this.basaltHowler.getY() + 2.0, this.basaltHowler.getZ() + tremorRadius
        );

        List<LivingEntity> affectedEntities = this.basaltHowler.getWorld().getEntitiesByClass(
                LivingEntity.class, attackBox, entity -> entity != this.basaltHowler && !entity.isSpectator()
        );

        for (LivingEntity entity : affectedEntities) {
            float damage = attackDamage;

            // Deal reduced damage based on distance
            double distSq = entity.squaredDistanceTo(this.basaltHowler);
            float distanceFactor = (float) (1.0 - Math.sqrt(distSq) / (tremorRadius + 0.5));
            damage *= Math.max(0.5, distanceFactor);

            entity.damage(this.basaltHowler.getWorld().getDamageSources().mobAttack(this.basaltHowler), damage);

            // Calculate knockback direction and power
            Vec3d knockbackVec = entity.getPos().subtract(this.basaltHowler.getPos()).normalize();
            double knockbackStrength = 0.7 * (1.0 - Math.sqrt(distSq) / tremorRadius);

            // Apply knockback - 1.21.1 method with velocity
            entity.addVelocity(
                    knockbackVec.x * knockbackStrength,
                    0.2 * knockbackStrength,
                    knockbackVec.z * knockbackStrength
            );
            entity.velocityModified = true; // Important flag to update velocity
        }
    }

    private void performHowlAttack(LivingEntity target) {
        // Play howl sound
        this.basaltHowler.getWorld().playSound(
                null,
                this.basaltHowler.getX(),
                this.basaltHowler.getY(),
                this.basaltHowler.getZ(),
                SoundEvents.ENTITY_WARDEN_SONIC_BOOM,
                SoundCategory.HOSTILE,
                1.5F,
                0.8F
        );

        // Visual effects for the howl - updated for 1.21.1
        if (basaltHowler.getWorld() instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 60; i++) {
                double angle = i * (Math.PI * 2) / 60;
                double distance = howlRadius * (0.3 + 0.7 * this.basaltHowler.getRandom().nextFloat());
                double offsetX = Math.cos(angle) * distance;
                double offsetZ = Math.sin(angle) * distance;

                serverWorld.spawnParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        this.basaltHowler.getX() + offsetX,
                        this.basaltHowler.getY() + 0.5,
                        this.basaltHowler.getZ() + offsetZ,
                        1, // particle count
                        0.0, 0.05, 0.0, // velocity
                        0.01 // speed
                );

                // Add additional wave effect particles
                if (i % 6 == 0) {
                    serverWorld.spawnParticles(
                            ParticleTypes.SCULK_CHARGE_POP,
                            this.basaltHowler.getX() + offsetX * 0.8,
                            this.basaltHowler.getY() + 0.7,
                            this.basaltHowler.getZ() + offsetZ * 0.8,
                            1, // particle count
                            0.0, 0.02, 0.0, // velocity
                            0.01 // speed
                    );
                }
            }
        }

        // Affect entities caught in howl radius
        Box attackBox = new Box(
                this.basaltHowler.getX() - howlRadius, this.basaltHowler.getY(), this.basaltHowler.getZ() - howlRadius,
                this.basaltHowler.getX() + howlRadius, this.basaltHowler.getY() + 3.0, this.basaltHowler.getZ() + howlRadius
        );

        List<LivingEntity> affectedEntities = this.basaltHowler.getWorld().getEntitiesByClass(
                LivingEntity.class, attackBox, entity -> entity != this.basaltHowler && !entity.isSpectator()
        );

        for (LivingEntity entity : affectedEntities) {
            // Deal sonic damage
            float damage = attackDamage * 0.7f;

            // Deal reduced damage based on distance
            double distSq = entity.squaredDistanceTo(this.basaltHowler);
            float distanceFactor = (float) (1.0 - Math.sqrt(distSq) / howlRadius);
            damage *= Math.max(0.5, distanceFactor);

            entity.damage(this.basaltHowler.getWorld().getDamageSources().sonicBoom(this.basaltHowler), damage);

            // Apply slowness and weakness effects (longer duration if closer)
            int baseDuration = 100; // 5 seconds
            int scaledDuration = (int) (baseDuration * Math.max(0.5, distanceFactor));

            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, scaledDuration, 1));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, scaledDuration, 0));
        }

        // Move to cooldown phase
        currentPhase = AttackPhase.COOLDOWN;
        attackCooldown = cooldownTicks;
    }
}