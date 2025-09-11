package cassetu.mystbornhorizons.entity.custom;

import cassetu.mystbornhorizons.entity.ModEntities;
import cassetu.mystbornhorizons.item.ModItems;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TomahawkProjectileEntity extends PersistentProjectileEntity {
    private float rotation;
    public Vector2f groundedOffset;
    private ItemStack tomahawkStack = ItemStack.EMPTY;
    private float customDamage = 12.0f; // Default damage
    private boolean isSummonedByBoss = false; // Track if summoned by boss

    public TomahawkProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.groundedOffset = new Vector2f(215f, 180f); // Initialize here
    }

    public TomahawkProjectileEntity(World world, PlayerEntity player) {
        super(ModEntities.TOMAHAWK, player, world, new ItemStack(ModItems.TOMAHAWK), null);
        this.groundedOffset = new Vector2f(215f, 180f); // Initialize here
    }

    // Constructor for boss summoning
    public TomahawkProjectileEntity(World world, LivingEntity owner, Vec3d velocity, float damage) {
        super(ModEntities.TOMAHAWK, owner, world, new ItemStack(ModItems.TOMAHAWK), null);
        this.groundedOffset = new Vector2f(215f, 180f);
        this.customDamage = damage;
        this.isSummonedByBoss = true;
        this.setVelocity(velocity);
        this.setNoClip(false); // Make sure it can hit things
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.TOMAHAWK);
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    public boolean isGrounded() {
        return inGround;
    }

    // Method to set custom damage (for boss summoning)
    public void setCustomDamage(float damage) {
        this.customDamage = damage;
    }

    public float getCustomDamage() {
        return this.customDamage;
    }

    public void setBossSummoned(boolean bossSummoned) {
        this.isSummonedByBoss = bossSummoned;
    }

    public boolean isBossSummoned() {
        return this.isSummonedByBoss;
    }

    @Override
    public void tick() {
        super.tick();

        // Add particle trail for boss-summoned tomahawks
        if (isSummonedByBoss && !this.getWorld().isClient() && !this.inGround) {
            ((ServerWorld)this.getWorld()).spawnParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    this.getX(), this.getY(), this.getZ(),
                    1,
                    0.1, 0.1, 0.1,
                    0.0
            );

            // Occasional enchantment particles
            if (this.random.nextFloat() < 0.3) {
                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.ENCHANT,
                        this.getX(), this.getY(), this.getZ(),
                        1,
                        0.05, 0.05, 0.05,
                        0.1
                );
            }
        }

        // Auto-despawn boss-summoned tomahawks after some time to prevent world clutter
        if (isSummonedByBoss && this.age > 1200) { // 60 seconds
            this.discard();
        }
    }

    @Override
    public boolean canHit(Entity entity) {
        // Boss-summoned tomahawks should not hit the summoner
        if (isSummonedByBoss && entity == this.getOwner()) {
            return false;
        }
        return super.canHit(entity);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();

        // Use custom damage for boss-summoned tomahawks
        float damage = isSummonedByBoss ? customDamage : 12.0f;
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), damage);

        // Special effects for boss-summoned tomahawks
        if (isSummonedByBoss && !this.getWorld().isClient()) {
            // Explosion particles on hit
            ((ServerWorld)this.getWorld()).spawnParticles(
                    ParticleTypes.CRIT,
                    entity.getX(), entity.getY() + 1.0, entity.getZ(),
                    8,
                    0.3, 0.3, 0.3,
                    0.1
            );

            // Soul fire particles
            ((ServerWorld)this.getWorld()).spawnParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    entity.getX(), entity.getY() + 1.0, entity.getZ(),
                    5,
                    0.2, 0.2, 0.2,
                    0.05
            );

            // Sound removed as requested
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        super.onBlockHit(result);

        groundedOffset = new Vector2f(215f, 180f);

        if(result.getSide() == Direction.SOUTH) {
            groundedOffset = new Vector2f(215f,180f);
        }
        if(result.getSide() == Direction.NORTH) {
            groundedOffset = new Vector2f(215f, 0f);
        }
        if(result.getSide() == Direction.EAST) {
            groundedOffset = new Vector2f(215f,-90f);
        }
        if(result.getSide() == Direction.WEST) {
            groundedOffset = new Vector2f(215f,90f);
        }

        if(result.getSide() == Direction.DOWN) {
            groundedOffset = new Vector2f(115f,180f);
        }
        if(result.getSide() == Direction.UP) {
            groundedOffset = new Vector2f(285f,180f);
        }

        // Special effects when boss-summoned tomahawk hits blocks
        if (isSummonedByBoss && !this.getWorld().isClient()) {
            ((ServerWorld)this.getWorld()).spawnParticles(
                    ParticleTypes.SMOKE,
                    this.getX(), this.getY(), this.getZ(),
                    5,
                    0.2, 0.2, 0.2,
                    0.1
            );
        }
    }

    // Tomahawk stack methods
    public void setTomahawkStack(ItemStack stack) {
        this.tomahawkStack = stack.copy();
    }

    public ItemStack getTomahawkStack() {
        return this.tomahawkStack.copy();
    }

    // Override the pickup behavior to return the tomahawk
    @Override
    public boolean tryPickup(PlayerEntity player) {
        // Boss-summoned tomahawks cannot be picked up by players
        if (isSummonedByBoss) {
            return false;
        }

        if (this.isNoClip()) {
            return false;
        } else if (!this.getWorld().isClient && this.inGround) {
            // Give back the tomahawk item
            if (!this.tomahawkStack.isEmpty()) {
                if (player.getInventory().insertStack(this.tomahawkStack.copy())) {
                    this.discard();
                    return true;
                }
            } else {
                // Fallback to default item if no stored stack
                if (player.getInventory().insertStack(this.getDefaultItemStack())) {
                    this.discard();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (!this.tomahawkStack.isEmpty()) {
            nbt.put("TomahawkStack", this.tomahawkStack.encode(this.getRegistryManager()));
        }
        nbt.putFloat("CustomDamage", this.customDamage);
        nbt.putBoolean("BossSummoned", this.isSummonedByBoss);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("TomahawkStack")) {
            this.tomahawkStack = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("TomahawkStack")).orElse(ItemStack.EMPTY);
        }
        if (nbt.contains("CustomDamage")) {
            this.customDamage = nbt.getFloat("CustomDamage");
        }
        if (nbt.contains("BossSummoned")) {
            this.isSummonedByBoss = nbt.getBoolean("BossSummoned");
        }
    }
}