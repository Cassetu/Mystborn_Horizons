package cassetu.mystbornhorizons.entity.custom;

import cassetu.mystbornhorizons.entity.ModEntities;
import cassetu.mystbornhorizons.item.ModItems;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TomahawkProjectileEntity extends PersistentProjectileEntity {
    private float rotation;
    public Vector2f groundedOffset;

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
}
