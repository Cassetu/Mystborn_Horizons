package cassetu.mystbornhorizons.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.BoggedEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import java.util.EnumSet;

public class CursedMobTrackerGoal extends Goal {
    private final MobEntity mob;
    private final double speed;
    private final double detectionRange;
    private final double meleeAttackRange;
    private final double rangedAttackRange;
    private PlayerEntity targetPlayer;
    private int updateCountdown;
    private int attackCooldown;
    private int rangedAttackTime = -1;
    private final int maxRangedAttackTime = 20;
    private int pathRecalculationCooldown;
    private int stuckTimer;
    private double lastX, lastY, lastZ;

    public CursedMobTrackerGoal(MobEntity mob, double speed, double detectionRange) {
        this.mob = mob;
        this.speed = speed;
        this.detectionRange = detectionRange;
        this.meleeAttackRange = mob.getWidth() * 2.0 + 1.0;
        this.rangedAttackRange = 15.0;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.updateCountdown > 0) {
            --this.updateCountdown;
            return false;
        }

        this.updateCountdown = 10;

        if (!isCursedMob()) {
            return false;
        }

        this.targetPlayer = findNearestPlayer();
        return this.targetPlayer != null;
    }

    @Override
    public boolean shouldContinue() {
        if (!isCursedMob()) {
            return false;
        }

        if (this.targetPlayer == null || !this.targetPlayer.isAlive()) {
            return false;
        }

        double distanceSq = this.mob.squaredDistanceTo(this.targetPlayer);
        return distanceSq < this.detectionRange * this.detectionRange * 1.5;
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetPlayer, this.speed);
        this.mob.setTarget(this.targetPlayer);
        this.attackCooldown = 0;
        this.rangedAttackTime = -1;
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.mob.getNavigation().stop();
        this.mob.setTarget(null);
        this.rangedAttackTime = -1;
    }

    @Override
    public void tick() {
        if (this.targetPlayer == null) {
            return;
        }

        double distanceSq = this.mob.squaredDistanceTo(this.targetPlayer);
        double distance = Math.sqrt(distanceSq);

        this.mob.getLookControl().lookAt(this.targetPlayer, 30.0F, 30.0F);

        boolean isRangedMob = isRangedMob();

        if (isRangedMob) {
            handleRangedCombat(distance);
        } else {
            handleMeleeCombat(distance);
        }

        if (this.mob.age % 10 == 0) {
            this.mob.getNavigation().startMovingTo(this.targetPlayer, this.speed);
        }

        this.mob.setTarget(this.targetPlayer);

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
    }

    private void handleMeleeCombat(double distance) {
        if (distance > this.meleeAttackRange) {
            this.mob.getNavigation().startMovingTo(this.targetPlayer, this.speed);
        } else {
            this.mob.getNavigation().stop();

            if (this.attackCooldown <= 0) {
                performMeleeAttack();
                this.attackCooldown = 20;
            }
        }
    }

    private void handleRangedCombat(double distance) {
        if (distance < 8.0) {
            this.mob.getNavigation().stop();
        } else if (distance > this.rangedAttackRange) {
            this.mob.getNavigation().startMovingTo(this.targetPlayer, this.speed);
        } else {
            this.mob.getNavigation().stop();

            if (this.attackCooldown <= 0) {
                performRangedAttack(distance);
            }
        }
    }

    private void performMeleeAttack() {
        if (this.targetPlayer == null) return;

        this.mob.swingHand(Hand.MAIN_HAND);

        this.mob.tryAttack(this.targetPlayer);
    }

    private void performRangedAttack(double distance) {
        if (this.targetPlayer == null) return;

        ItemStack mainHandStack = this.mob.getMainHandStack();

        if (mainHandStack.getItem() instanceof BowItem) {
            if (this.rangedAttackTime == -1) {
                this.rangedAttackTime = 0;
            }

            this.rangedAttackTime++;

            if (this.rangedAttackTime >= maxRangedAttackTime) {
                shootArrow(distance);
                this.rangedAttackTime = -1;
                this.attackCooldown = 30;
            }
        } else if (mainHandStack.getItem() instanceof CrossbowItem) {
            shootCrossbow();
            this.attackCooldown = 40;
        } else if (this.mob instanceof AbstractSkeletonEntity) {
            shootArrow(distance);
            this.attackCooldown = 30;
        }
    }

    private void shootArrow(double distance) {
        ItemStack weaponStack = this.mob.getMainHandStack();
        ItemStack arrowStack = this.mob.getProjectileType(weaponStack);
        PersistentProjectileEntity arrow = ProjectileUtil.createArrowProjectile(this.mob, arrowStack, 1.0F, weaponStack);

        if (arrow != null) {
            double dx = this.targetPlayer.getX() - this.mob.getX();
            double dy = this.targetPlayer.getBodyY(0.3333333333333333) - arrow.getY();
            double dz = this.targetPlayer.getZ() - this.mob.getZ();
            double distanceXZ = Math.sqrt(dx * dx + dz * dz);

            arrow.setVelocity(dx, dy + distanceXZ * 0.2, dz, 1.6F, (float)(14 - this.mob.getWorld().getDifficulty().getId() * 4));

            this.mob.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.mob.getRandom().nextFloat() * 0.4F + 0.8F));
            this.mob.getWorld().spawnEntity(arrow);

            this.mob.swingHand(Hand.MAIN_HAND);
        }
    }

    private void shootCrossbow() {
        if (this.mob instanceof PillagerEntity) {
            this.mob.swingHand(Hand.MAIN_HAND);
        } else {
            shootArrow(Math.sqrt(this.mob.squaredDistanceTo(this.targetPlayer)));
        }
    }

    private boolean isRangedMob() {
        if (this.mob instanceof AbstractSkeletonEntity) {
            return true;
        }

        if (this.mob instanceof PillagerEntity) {
            return true;
        }

        ItemStack mainHand = this.mob.getMainHandStack();
        return mainHand.getItem() instanceof RangedWeaponItem;
    }

    private boolean isCursedMob() {
        if (!this.mob.hasCustomName() || this.mob.getCustomName() == null) {
            return false;
        }
        return this.mob.getCustomName().getString().contains("Cursed");
    }

    private PlayerEntity findNearestPlayer() {
        if (!(this.mob.getWorld() instanceof ServerWorld serverWorld)) {
            return null;
        }

        PlayerEntity nearest = null;
        double nearestDistance = this.detectionRange * this.detectionRange;

        for (PlayerEntity player : serverWorld.getPlayers()) {
            if (!player.isAlive() || player.isSpectator() || player.isCreative()) {
                continue;
            }

            double distance = this.mob.squaredDistanceTo(player);
            if (distance < nearestDistance) {
                nearest = player;
                nearestDistance = distance;
            }
        }

        return nearest;
    }
}