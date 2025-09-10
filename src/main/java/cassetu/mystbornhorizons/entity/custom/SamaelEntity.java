package cassetu.mystbornhorizons.entity.custom;

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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SamaelEntity extends HostileEntity {
    private ServerBossBar bossBar;
    private int venomousBloomCooldown = 0;
    private int rootNetworkCooldown = 0;
    private int pollenStormCooldown = 0;
    private boolean isGardensWrathActive = false;
    private static final int BLOOM_COOLDOWN = 200;
    private static final int ROOT_COOLDOWN = 160;
    private static final int POLLEN_COOLDOWN = 300;

    // Healing variables
    private int healingTick = 0;
    private static final int HEALING_INTERVAL = 20; // Heal every second (20 ticks)
    private static final float HEALING_AMOUNT = 4.5f; // Amount to heal each interval

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public SamaelEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setCustomNameVisible(true);
        if (!world.isClient()) {
            this.bossBar = new ServerBossBar(
                    this.getDisplayName(),
                    BossBar.Color.GREEN,
                    BossBar.Style.PROGRESS
            );
        }
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
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
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 3000)
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
            // Check if entity is on fire and apply glowing effect
            if (this.isOnFire() && !this.hasStatusEffect(StatusEffects.GLOWING)) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 600, 0)); // 30 seconds (600 ticks)
            }

            if (venomousBloomCooldown > 0) venomousBloomCooldown--;
            if (rootNetworkCooldown > 0) rootNetworkCooldown--;
            if (pollenStormCooldown > 0) pollenStormCooldown--;

            // Healing logic - only heal if not glowing
            if (!this.hasStatusEffect(StatusEffects.GLOWING)) {
                healingTick++;
                if (healingTick >= HEALING_INTERVAL) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(HEALING_AMOUNT);

                        // Spawn healing particles
                        ((ServerWorld)this.getWorld()).spawnParticles(
                                ParticleTypes.HEART,
                                this.getX(), this.getY() + 1.5, this.getZ(),
                                3,
                                0.3, 0.3, 0.3,
                                0.1
                        );

                        // Additional nature-themed healing particles
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
            } else {
                // Reset healing tick when glowing (so healing doesn't resume immediately)
                healingTick = 0;
            }

            if (!isGardensWrathActive && this.getHealth() <= this.getMaxHealth() * 0.5f) {
                activateGardensWrath();
            }

            PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 20.0);
            if (nearestPlayer != null) {
                if (venomousBloomCooldown <= 0) useVenomousBloom(nearestPlayer);
                if (rootNetworkCooldown <= 0) useRootNetwork(nearestPlayer);
                if (pollenStormCooldown <= 0) usePollenStorm();
            }
        }

        if (this.getWorld().isClient()) {
            this.setupAnimationStates();
        }
    }

    private void useVenomousBloom(PlayerEntity target) {
        for (double t = 0; t < Math.PI * 4; t += 0.1) {
            double radius = t / 2;
            double x = target.getX() + radius * Math.cos(t);
            double z = target.getZ() + radius * Math.sin(t);

            if (!this.getWorld().isClient()) {
                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.SPORE_BLOSSOM_AIR,
                        x, target.getY() + t/3, z,
                        2,
                        0.1, 0.1, 0.1,
                        0.05
                );
                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.SNEEZE,
                        x, target.getY() + t/3, z,
                        1,
                        0.1, 0.1, 0.1,
                        0.05
                );
            }
        }

        Box searchBox = new Box(target.getX() - 2, target.getY() - 1, target.getZ() - 2,
                target.getX() + 2, target.getY() + 2, target.getZ() + 2);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, searchBox).forEach(
                p -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 1))
        );

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.samael.venomous_bloom"), true)
        );
        venomousBloomCooldown = BLOOM_COOLDOWN;
    }

    private void useRootNetwork(PlayerEntity target) {
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI/8) {
            for (double r = 0; r < 5; r += 0.5) {
                double x = target.getX() + Math.cos(angle) * r;
                double z = target.getZ() + Math.sin(angle) * r;
                double yOffset = Math.sin(r * 2) * 0.5;

                if (!this.getWorld().isClient()) {
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
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.samael.root_network"), true)
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
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.samael.pollen_storm"), true)
        );
        pollenStormCooldown = POLLEN_COOLDOWN;
    }

    private void activateGardensWrath() {
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)
                .setBaseValue(this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 1.5);

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
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.samael.gardens_wrath"), true)
        );
        isGardensWrathActive = true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        float cappedAmount = Math.min(amount, 2.5f);

        if (source.getAttacker() instanceof PlayerEntity player) {
            float thornsDamage = isGardensWrathActive ? 6.0f : 3.0f;
            player.damage(this.getDamageSources().thorns(this), thornsDamage);

            // Calculate knockback direction (from Samael to player)
            double deltaX = player.getX() - this.getX();
            double deltaZ = player.getZ() - this.getZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            // Normalize the direction and apply strong knockback
            if (distance > 0) {
                double knockbackStrength = isGardensWrathActive ? 0.6 : 0.3; // Stronger when enraged
                double normalizedX = (deltaX / distance) * knockbackStrength;
                double normalizedZ = (deltaZ / distance) * knockbackStrength;
                double upwardForce = isGardensWrathActive ? 0.4 : 0.2; // Launch them up too

                player.setVelocity(normalizedX, upwardForce, normalizedZ);
                player.velocityModified = true; // Important for syncing with client
            }

            if (!this.getWorld().isClient()) {
                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.CRIT,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        10,
                        0.5, 0.5, 0.5,
                        0.1
                );

                // Add explosion-like particles at the point of contact for visual impact
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
                            ParticleTypes.CRIMSON_SPORE,
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

    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return !effect.equals(StatusEffects.POISON) && super.canHaveStatusEffect(effect);
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