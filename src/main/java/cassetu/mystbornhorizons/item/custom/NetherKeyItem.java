package cassetu.mystbornhorizons.item;

import cassetu.mystbornhorizons.world.NetherAccessState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class NetherKeyItem extends Item {

    public NetherKeyItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            ServerWorld serverWorld = player.getServerWorld();
            NetherAccessState accessState = NetherAccessState.getOrCreate(serverWorld.getServer());

            if (accessState.hasNetherAccess(player.getUuid())) {
                player.sendMessage(Text.literal("You already have access to the Nether.").formatted(Formatting.YELLOW), false);
                return TypedActionResult.fail(stack);
            }

            accessState.grantNetherAccess(player.getUuid(), serverWorld.getServer());
            stack.decrement(1);

            player.sendMessage(Text.literal("The Nether Key dissolves in your hand...").formatted(Formatting.DARK_RED, Formatting.ITALIC), false);
            player.sendMessage(Text.literal("You can now access the Nether!").formatted(Formatting.GOLD), false);

            serverWorld.spawnParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    player.getX(), player.getY() + 1, player.getZ(),
                    30,
                    0.5, 0.5, 0.5,
                    0.1
            );

            serverWorld.spawnParticles(
                    ParticleTypes.PORTAL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    50,
                    0.5, 0.5, 0.5,
                    0.5
            );

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, 1.0f, 0.8f);

            return TypedActionResult.success(stack);
        }

        return TypedActionResult.consume(stack);
    }
}