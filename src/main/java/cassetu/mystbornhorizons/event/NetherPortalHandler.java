package cassetu.mystbornhorizons.event;

import cassetu.mystbornhorizons.world.NetherAccessState;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class NetherPortalHandler {

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) {
                return ActionResult.PASS;
            }

            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (player.getStackInHand(hand).getItem() == Items.FLINT_AND_STEEL ||
                        player.getStackInHand(hand).getItem() == Items.FIRE_CHARGE) {

                    var blockState = world.getBlockState(hitResult.getBlockPos());
                    var adjacentState = world.getBlockState(hitResult.getBlockPos().offset(hitResult.getSide()));

                    if (blockState.getBlock() == Blocks.OBSIDIAN || adjacentState.getBlock() == Blocks.OBSIDIAN) {
                        NetherAccessState accessState = NetherAccessState.getOrCreate(serverPlayer.getServer());

                        if (!accessState.hasNetherAccess(serverPlayer.getUuid())) {
                            serverPlayer.sendMessage(
                                    Text.literal("You cannot create a Nether portal without a Nether Key!")
                                            .formatted(Formatting.RED),
                                    true
                            );
                            return ActionResult.FAIL;
                        }
                    }
                }
            }

            return ActionResult.PASS;
        });
    }

    public static boolean canCreatePortal(World world, BlockPos pos) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) {
            return true;
        }

        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (isValidPortalFrame(world, pos, direction)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidPortalFrame(World world, BlockPos pos, Direction direction) {
        Direction.Axis axis = direction.getAxis();
        Direction perpendicular = direction.rotateYClockwise();

        for (int height = -1; height <= 3; height++) {
            for (int width = -1; width <= 3; width++) {
                BlockPos checkPos = pos.offset(Direction.UP, height).offset(perpendicular, width);
                BlockState state = world.getBlockState(checkPos);

                if (state.getBlock() == Blocks.OBSIDIAN) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkPortalCreation(ServerWorld world, BlockPos firePos) {
        if (canCreatePortal(world, firePos)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            for (int x = -2; x <= 2; x++) {
                for (int y = -2; y <= 3; y++) {
                    for (int z = -2; z <= 2; z++) {
                        mutable.set(firePos.getX() + x, firePos.getY() + y, firePos.getZ() + z);

                        if (world.getBlockState(mutable).getBlock() == Blocks.OBSIDIAN) {
                            for (ServerPlayerEntity player : world.getPlayers()) {
                                if (player.squaredDistanceTo(firePos.getX(), firePos.getY(), firePos.getZ()) < 64) {
                                    NetherAccessState accessState = NetherAccessState.getOrCreate(world.getServer());

                                    if (!accessState.hasNetherAccess(player.getUuid())) {
                                        world.setBlockState(firePos, Blocks.AIR.getDefaultState());
                                        player.sendMessage(
                                                Text.literal("The fire extinguishes mysteriously... You need a Nether Key!")
                                                        .formatted(Formatting.RED),
                                                true
                                        );
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}