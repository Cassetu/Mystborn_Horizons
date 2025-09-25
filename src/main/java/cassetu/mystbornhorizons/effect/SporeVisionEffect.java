package cassetu.mystbornhorizons.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class SporeVisionEffect extends StatusEffect {
    public SporeVisionEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld().isClient() || !(entity instanceof PlayerEntity)) {
            return super.applyUpdateEffect(entity, amplifier);
        }

        PlayerEntity player = (PlayerEntity) entity;
        ServerWorld serverWorld = (ServerWorld) entity.getWorld();
        int effectLevel = amplifier + 1;

        createDenseSporeCloud(player, serverWorld, effectLevel);

        if (entity.age % 10 == 0) {
            createObscuringParticles(player, serverWorld, effectLevel);
        }

        if (entity.age % 20 == 0) {
            applyVisionImpairment(player, effectLevel);
        }

        if (entity.age % 40 == 0) {
            createPhantomHallucinations(player, serverWorld, effectLevel);
        }

        return super.applyUpdateEffect(entity, amplifier);
    }

    private void createDenseSporeCloud(PlayerEntity player, ServerWorld world, int level) {
        Vec3d playerPos = player.getPos();
        Vec3d eyePos = player.getEyePos();

        int particleIntensity = 15 + level * 10;
        double cloudRadius = 2.0 + level * 0.8;

        for (int i = 0; i < particleIntensity; i++) {
            double offsetX = (player.getRandom().nextGaussian()) * cloudRadius;
            double offsetY = (player.getRandom().nextGaussian()) * (cloudRadius * 0.7) + 0.5;
            double offsetZ = (player.getRandom().nextGaussian()) * cloudRadius;

            world.spawnParticles(
                    ParticleTypes.SPORE_BLOSSOM_AIR,
                    eyePos.x + offsetX, eyePos.y + offsetY, eyePos.z + offsetZ,
                    1, 0.0, 0.0, 0.0, 0.01
            );

            if (i % 3 == 0) {
                world.spawnParticles(
                        ParticleTypes.MYCELIUM,
                        eyePos.x + offsetX, eyePos.y + offsetY, eyePos.z + offsetZ,
                        1, 0.0, 0.0, 0.0, 0.008
                );
            }
        }

        Vec3d lookDirection = player.getRotationVec(1.0f);
        for (int i = 0; i < 8 + level * 3; i++) {
            Vec3d sporePos = eyePos.add(lookDirection.multiply(0.5 + i * 0.3));

            world.spawnParticles(
                    ParticleTypes.SPORE_BLOSSOM_AIR,
                    sporePos.x + (player.getRandom().nextGaussian() * 0.8),
                    sporePos.y + (player.getRandom().nextGaussian() * 0.8),
                    sporePos.z + (player.getRandom().nextGaussian() * 0.8),
                    3 + level, 0.0, 0.0, 0.0, 0.015
            );
        }
    }

    private void createObscuringParticles(PlayerEntity player, ServerWorld world, int level) {
        Vec3d eyePos = player.getEyePos();

        for (int i = 0; i < 25 + level * 15; i++) {
            double angle = player.getRandom().nextDouble() * Math.PI * 2;
            double distance = 0.8 + player.getRandom().nextDouble() * (1.2 + level * 0.4);
            double height = (player.getRandom().nextDouble() - 0.5) * 1.8;

            double x = eyePos.x + Math.cos(angle) * distance;
            double y = eyePos.y + height;
            double z = eyePos.z + Math.sin(angle) * distance;

            world.spawnParticles(
                    ParticleTypes.LARGE_SMOKE,
                    x, y, z,
                    3, 0.0, 0.0, 0.0, 0.005
            );

            if (i % 4 == 0) {
                world.spawnParticles(
                        ParticleTypes.SQUID_INK,
                        x, y, z,
                        3, 0.0, 0.0, 0.0, 0.003
                );
            }

            if (level >= 3 && i % 6 == 0) {
                world.spawnParticles(
                        ParticleTypes.ASH,
                        x, y, z,
                        2, 0.1, 0.1, 0.1, 0.008
                );
            }
        }
    }

    private void applyVisionImpairment(PlayerEntity player, int level) {
        if (level >= 1) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 60 + level * 20, 0, false, false, false));
        }

        if (level >= 2) {
            if (player.getRandom().nextFloat() < 0.6f) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 30 + level * 10, 0, false, false, false));
            }
        }

        if (level >= 3) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 1, false, false, false));
            if (player.getRandom().nextFloat() < 0.4f) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 40, 0, false, false, false));
            }
        }
    }

    private void createPhantomHallucinations(PlayerEntity player, ServerWorld world, int level) {
        if (player.getRandom().nextFloat() > 0.3f + level * 0.15f) return;

        Vec3d playerPos = player.getEyePos();
        Vec3d lookDir = player.getRotationVec(1.0f);

        int hallucinationCount = player.getRandom().nextInt(1 + level) + 1;

        for (int i = 0; i < hallucinationCount; i++) {
            double distance = 2.0 + player.getRandom().nextDouble() * (6.0 + level * 2.0);
            double angleOffset = (player.getRandom().nextDouble() - 0.5) * Math.PI * 0.9;
            double heightOffset = (player.getRandom().nextDouble() - 0.5) * 3.0;

            double yaw = Math.atan2(lookDir.z, lookDir.x) + angleOffset;
            Vec3d phantomPos = new Vec3d(
                    playerPos.x + Math.cos(yaw) * distance,
                    playerPos.y + heightOffset,
                    playerPos.z + Math.sin(yaw) * distance
            );

            createPhantomMob(world, phantomPos, level);
        }
    }

    private void createPhantomMob(ServerWorld world, Vec3d pos, int level) {
        int mobType = world.getRandom().nextInt(4);

        switch (mobType) {
            case 0:
                createPhantomSkeleton(world, pos, level);
                break;
            case 1:
                createPhantomZombie(world, pos, level);
                break;
            case 2:
                createPhantomSpider(world, pos, level);
                break;
            case 3:
                createPhantomEnderman(world, pos, level);
                break;
        }
    }

    private void createPhantomSkeleton(ServerWorld world, Vec3d pos, int level) {
        world.spawnParticles(ParticleTypes.SOUL, pos.x, pos.y + 1.8, pos.z, 4, 0.15, 0.1, 0.15, 0.02);
        world.spawnParticles(ParticleTypes.SOUL, pos.x, pos.y + 1.4, pos.z, 3, 0.2, 0.1, 0.2, 0.02);
        world.spawnParticles(ParticleTypes.SOUL, pos.x, pos.y + 1.0, pos.z, 3, 0.2, 0.1, 0.2, 0.02);
        world.spawnParticles(ParticleTypes.SOUL, pos.x, pos.y + 0.6, pos.z, 3, 0.15, 0.1, 0.15, 0.02);
        world.spawnParticles(ParticleTypes.SOUL, pos.x, pos.y + 0.2, pos.z, 2, 0.1, 0.05, 0.1, 0.02);

        world.spawnParticles(ParticleTypes.WHITE_ASH, pos.x + 0.3, pos.y + 1.6, pos.z, 2, 0.05, 0.05, 0.05, 0.01);
        world.spawnParticles(ParticleTypes.WHITE_ASH, pos.x - 0.3, pos.y + 1.6, pos.z, 2, 0.05, 0.05, 0.05, 0.01);

        if (level >= 2) {
            world.spawnParticles(ParticleTypes.SMOKE, pos.x, pos.y + 1.5, pos.z, 6, 0.3, 0.2, 0.3, 0.02);
        }
    }

    private void createPhantomZombie(ServerWorld world, Vec3d pos, int level) {
        world.spawnParticles(ParticleTypes.ITEM_SLIME, pos.x, pos.y + 1.7, pos.z, 5, 0.25, 0.1, 0.25, 0.03);
        world.spawnParticles(ParticleTypes.ITEM_SLIME, pos.x, pos.y + 1.3, pos.z, 6, 0.3, 0.2, 0.3, 0.03);
        world.spawnParticles(ParticleTypes.ITEM_SLIME, pos.x, pos.y + 0.9, pos.z, 5, 0.25, 0.1, 0.25, 0.03);
        world.spawnParticles(ParticleTypes.ITEM_SLIME, pos.x, pos.y + 0.5, pos.z, 4, 0.2, 0.1, 0.2, 0.03);
        world.spawnParticles(ParticleTypes.ITEM_SLIME, pos.x, pos.y + 0.1, pos.z, 3, 0.15, 0.05, 0.15, 0.03);

        world.spawnParticles(ParticleTypes.FALLING_NECTAR, pos.x, pos.y + 1.8, pos.z, 3, 0.1, 0.0, 0.1, 0.01);

        if (level >= 2) {
            world.spawnParticles(ParticleTypes.CRIMSON_SPORE, pos.x, pos.y + 1.0, pos.z, 8, 0.4, 0.4, 0.4, 0.04);
        }
    }

    private void createPhantomSpider(ServerWorld world, Vec3d pos, int level) {
        world.spawnParticles(ParticleTypes.LARGE_SMOKE, pos.x, pos.y + 0.5, pos.z, 8, 0.4, 0.1, 0.4, 0.02);

        for (int i = 0; i < 8; i++) {
            double angle = (i / 8.0) * Math.PI * 2;
            double legX = pos.x + Math.cos(angle) * 0.9;
            double legZ = pos.z + Math.sin(angle) * 0.9;
            world.spawnParticles(ParticleTypes.LARGE_SMOKE, legX, pos.y + 0.1, legZ, 2, 0.05, 0.05, 0.05, 0.01);
        }

        world.spawnParticles(ParticleTypes.WITCH, pos.x, pos.y + 0.3, pos.z, 4, 0.2, 0.1, 0.2, 0.02);

        if (level >= 2) {
            world.spawnParticles(ParticleTypes.PORTAL, pos.x, pos.y + 0.7, pos.z, 6, 0.3, 0.2, 0.3, 0.03);
        }
    }

    private void createPhantomEnderman(ServerWorld world, Vec3d pos, int level) {
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y + 2.8, pos.z, 3, 0.1, 0.1, 0.1, 0.02);
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y + 2.4, pos.z, 4, 0.15, 0.1, 0.15, 0.02);
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y + 2.0, pos.z, 4, 0.15, 0.1, 0.15, 0.02);
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y + 1.6, pos.z, 4, 0.15, 0.1, 0.15, 0.02);
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y + 1.2, pos.z, 4, 0.15, 0.1, 0.15, 0.02);
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y + 0.8, pos.z, 3, 0.1, 0.1, 0.1, 0.02);
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y + 0.4, pos.z, 3, 0.1, 0.05, 0.1, 0.02);

        world.spawnParticles(ParticleTypes.PORTAL, pos.x + 0.3, pos.y + 2.6, pos.z, 2, 0.05, 0.05, 0.05, 0.01);
        world.spawnParticles(ParticleTypes.PORTAL, pos.x - 0.3, pos.y + 2.6, pos.z, 2, 0.05, 0.05, 0.05, 0.01);

        if (level >= 2) {
            world.spawnParticles(ParticleTypes.DRAGON_BREATH, pos.x, pos.y + 1.5, pos.z, 6, 0.3, 0.3, 0.3, 0.03);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}