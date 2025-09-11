package cassetu.mystbornhorizons.item.custom;

import cassetu.mystbornhorizons.entity.custom.TomahawkProjectileEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TomahawkItem extends Item {
    public TomahawkItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        // Play sound on both client and server for consistency
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL,
                0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

        // The following logic should only run on the server
        if (!world.isClient) {
            TomahawkProjectileEntity tomahawk = new TomahawkProjectileEntity(world, user);
            tomahawk.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 0f);
            tomahawk.setTomahawkStack(itemStack.copy());
            world.spawnEntity(tomahawk);

            user.incrementStat(Stats.USED.getOrCreateStat(this));

            if (!user.getAbilities().creativeMode) {
                // Damage the item first
                itemStack.damage(1, user, EquipmentSlot.MAINHAND);

                // Check if item is broken after damage
                if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                    // Item is completely broken, remove it entirely
                    itemStack.decrement(1);
                } else {
                    // Item still has durability, remove it temporarily (will be returned when picked up)
                    itemStack.decrement(1);
                }
            } else {
                // In creative mode, still remove the item temporarily
                // Create a copy to store in the projectile, but don't modify the original stack
                // The projectile will return a full durability version
            }
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.IRON_INGOT);
    }
}