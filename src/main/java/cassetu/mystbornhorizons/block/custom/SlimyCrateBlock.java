package cassetu.mystbornhorizons.block.custom;

import cassetu.mystbornhorizons.effect.ModEffects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class SlimyCrateBlock extends Block {
    public SlimyCrateBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockState result = super.onBreak(world, pos, state, player);

        if (!world.isClient) {
            spawnSlimyCloud((ServerWorld) world, pos);
        }

        return result;
    }


    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        super.onDestroyedByExplosion(world, pos, explosion);

        if (!world.isClient) {
            spawnSlimyCloud((ServerWorld) world, pos);
        }
    }


    private void spawnSlimyCloud(ServerWorld world, BlockPos pos) {
        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(
                world,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
        );

        cloud.setRadius(3.0F);
        cloud.setDuration(200); // 10 seconds
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(0);
        cloud.setRadiusGrowth(0);

        cloud.addEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
        cloud.addEffect(new StatusEffectInstance(ModEffects.SLIMEY, 200, 0));

        world.spawnEntity(cloud);
    }
}
