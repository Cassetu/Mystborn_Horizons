package cassetu.mystbornhorizons.mixin;

import cassetu.mystbornhorizons.event.NetherPortalHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class FireBlockMixin {

    @Inject(method = "onBlockAdded", at = @At("HEAD"), cancellable = true)
    private void onFirePlaced(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        if (world instanceof ServerWorld serverWorld) {
            if (!NetherPortalHandler.checkPortalCreation(serverWorld, pos)) {
                ci.cancel();
            }
        }
    }
}