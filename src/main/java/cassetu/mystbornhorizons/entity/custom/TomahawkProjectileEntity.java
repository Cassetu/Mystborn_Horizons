package cassetu.mystbornhorizons.entity.custom;

import cassetu.mystbornhorizons.entity.ModEntities;
import cassetu.mystbornhorizons.item.ModItems;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TomahawkProjectileEntity extends PersistentProjectileEntity {
    private float rotation;
    public Vector2f groundedOffset;
    private ItemStack tomahawkStack = ItemStack.EMPTY;

    public TomahawkProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.groundedOffset = new Vector2f(215f, 180f); // Initialize here
    }

    public TomahawkProjectileEntity(World world, PlayerEntity player) {
        super(ModEntities.TOMAHAWK, player, world, new ItemStack(ModItems.TOMAHAWK), null);
        this.groundedOffset = new Vector2f(215f, 180f); // Initialize here
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

    @Override
    public boolean canHit(Entity entity) {
        return true;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 12);
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
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("TomahawkStack")) {
            this.tomahawkStack = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("TomahawkStack")).orElse(ItemStack.EMPTY);
        }
    }
}