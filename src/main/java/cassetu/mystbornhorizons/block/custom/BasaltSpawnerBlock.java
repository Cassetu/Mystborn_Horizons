package cassetu.mystbornhorizons.block.custom;

import cassetu.mystbornhorizons.block.entity.BasaltSpawnerBlockEntity;
import cassetu.mystbornhorizons.block.entity.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BasaltSpawnerBlock extends BlockWithEntity {
    public static final MapCodec<BasaltSpawnerBlock> CODEC = createCodec(BasaltSpawnerBlock::new);

    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
    public static final IntProperty WAVE = IntProperty.of("wave", 1, 3);

    public BasaltSpawnerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(ACTIVE, false)
                .with(WAVE, 1));
    }

    @Override
    public MapCodec<BasaltSpawnerBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE, WAVE);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BasaltSpawnerBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BasaltSpawnerBlockEntity spawnerEntity) {
                if (spawnerEntity.isOnCooldown()) {
                    player.sendMessage(Text.literal("Spawner on cooldown"), true);
                } else if (!state.get(ACTIVE)) {
                    spawnerEntity.startTrial(player);
                    world.setBlockState(pos, state.with(ACTIVE, true).with(WAVE, 1));
                    world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_SPAWN_MOB,
                            SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, ModBlockEntities.BASALT_SPAWNER,
                BasaltSpawnerBlockEntity::serverTick);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(ACTIVE)) {
            for (int i = 0; i < 5; i++) {
                double x = pos.getX() + random.nextDouble();
                double y = pos.getY() + random.nextDouble();
                double z = pos.getZ() + random.nextDouble();

                world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.1, 0.0);
                world.addParticle(ParticleTypes.LAVA, x, y, z, 0.0, 0.05, 0.0);
            }

            int wave = state.get(WAVE);
            if (wave >= 3) {
                world.addParticle(ParticleTypes.LARGE_SMOKE,
                        pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                        0.0, 0.1, 0.0);
            }
        }
    }

    public void completeTrial(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            world.setBlockState(pos, state.with(ACTIVE, false).with(WAVE, 1));
            world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_CLOSE_SHUTTER,
                    SoundCategory.BLOCKS, 1.0F, 1.0F);
            spawnLootChest(world, pos);
        }
    }

    private void spawnLootChest(World world, BlockPos pos) {
        for (int radius = 2; radius <= 4; radius++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius || Math.abs(z) == radius) {
                        BlockPos chestPos = pos.add(x, 0, z);
                        BlockPos groundPos = chestPos.down();

                        if (world.getBlockState(chestPos).isAir() &&
                                world.getBlockState(groundPos).isSolidBlock(world, groundPos)) {

                            world.setBlockState(chestPos, Blocks.CHEST.getDefaultState());
                            return;
                        }
                    }
                }
            }
        }
    }

    public void updateWave(World world, BlockPos pos, int newWave) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == this && state.get(ACTIVE)) {
            world.setBlockState(pos, state.with(WAVE, Math.min(3, newWave)));
            world.playSound(null, pos, SoundEvents.BLOCK_TRIAL_SPAWNER_SPAWN_MOB,
                    SoundCategory.BLOCKS, 1.0F, 0.8F + (newWave * 0.1F));
        }
    }
}