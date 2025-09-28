package cassetu.mystbornhorizons.mixin;

import cassetu.mystbornhorizons.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LandPathNodeMaker.class)
public class ShardBlockPathfindingMixin {

    @Inject(method = "getCommonNodeType", at = @At("HEAD"), cancellable = true)
    private static void makeShardBlockAvoidable(BlockView world, BlockPos pos, CallbackInfoReturnable<PathNodeType> cir) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isOf(ModBlocks.SHARD_BLOCK) || blockState.isOf(ModBlocks.HONEY_BERRY_BUSH)) {
            cir.setReturnValue(PathNodeType.DAMAGE_OTHER);
        }
    }
}