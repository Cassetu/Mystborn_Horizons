package cassetu.mystbornhorizons.entity.custom;

import cassetu.mystbornhorizons.effect.ModEffects;
import cassetu.mystbornhorizons.entity.ModEntities;
import cassetu.mystbornhorizons.item.ModItems;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.ModStatus;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HavenCoreEntity extends HostileEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private boolean hasSetPlayerBasedHealth = false;

    // Configuration constants
    private static final float BASE_HEALTH = 20.0f;
    private static final float HEALTH_PER_PLAYER = 30.0f;
    private static final double DETECTION_RADIUS = 50.0; // Radius to check for players

    public HavenCoreEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, BASE_HEALTH)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20)
                .add(EntityAttributes.GENERIC_ARMOR, 13)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 100);
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    private void setHealthBasedOnPlayers() {
        if (!hasSetPlayerBasedHealth && !this.getWorld().isClient()) {
            List<PlayerEntity> nearbyPlayers = this.getWorld().getEntitiesByClass(
                    PlayerEntity.class,
                    this.getBoundingBox().expand(DETECTION_RADIUS),
                    player -> !player.isSpectator()
            );

            int playerCount = nearbyPlayers.size();
            float newMaxHealth = BASE_HEALTH + (playerCount * HEALTH_PER_PLAYER);
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(newMaxHealth);
            this
            .setHealth(newMaxHealth);

            hasSetPlayerBasedHealth = true;

            System.out.println("HavenCore spawned with " + playerCount + " players nearby. Health set to: " + newMaxHealth);
        }
    }

    @Override
    protected void dropLoot(DamageSource damageSource, boolean causedByPlayer) {
        super.dropLoot(damageSource, causedByPlayer);

        if (!this.getWorld().isClient()) {
            ItemStack discFragment = Registries.ITEM.get(Identifier.of("mystbornhorizons", "forest_haven_disc_fragment")).getDefaultStack();
            this.dropStack(discFragment);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        if (!this.getWorld().isClient()) {
            AreaEffectCloudEntity instantdamageCloud = new AreaEffectCloudEntity(this.getWorld(), this.getX(), this.getY(), this.getZ());
            instantdamageCloud.setRadius(2.0f);
            instantdamageCloud.setDuration(150);
            instantdamageCloud.addEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 20, 0));
            instantdamageCloud.setOwner(this);
            this.getWorld().spawnEntity(instantdamageCloud);

            AreaEffectCloudEntity slownessCloud = new AreaEffectCloudEntity(this.getWorld(), this.getX(), this.getY(), this.getZ());
            slownessCloud.setRadius(8.0f);
            slownessCloud.setDuration(300);
            slownessCloud.addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 1));
            slownessCloud.setOwner(this);
            this.getWorld().spawnEntity(slownessCloud);

            AreaEffectCloudEntity sporeCloud = new AreaEffectCloudEntity(this.getWorld(), this.getX(), this.getY(), this.getZ());
            sporeCloud.setRadius(5.0f);
            sporeCloud.setDuration(230);
            sporeCloud.addEffect(new StatusEffectInstance(ModEffects.SPORE_VISION_EFFECT, 120, 1));
            sporeCloud.setOwner(this);
            this.getWorld().spawnEntity(sporeCloud);
        }
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

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            this.setupAnimationStates();
        } else {
            setHealthBasedOnPlayers();
        }
    }

    //sounds//

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ALLAY_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_CHORUS_FLOWER_DEATH;
    }
}