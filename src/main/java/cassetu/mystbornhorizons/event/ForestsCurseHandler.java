package cassetu.mystbornhorizons.event;

import cassetu.mystbornhorizons.world.ForestsCurseState;
import cassetu.mystbornhorizons.item.ModItems;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;

public class ForestsCurseHandler {

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof HostileEntity hostileEntity && entity.getWorld() instanceof ServerWorld serverWorld) {
                if (entity.hasCustomName() && entity.getCustomName() != null) {
                    String name = entity.getCustomName().getString();
                    if (name.contains("Cursed")) {
                        ForestsCurseState curseState = ForestsCurseState.getOrCreate(serverWorld);
                        if (curseState.isCurseActive()) {
                            curseState.addMobKill(serverWorld);
                            dropCursedEssence(hostileEntity, serverWorld);
                        }
                    }
                }
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world instanceof ServerWorld serverWorld) {
                ForestsCurseState curseState = ForestsCurseState.getOrCreate(serverWorld);
                curseState.tick(serverWorld);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            ServerWorld world = player.getServerWorld();
            ForestsCurseState curseState = ForestsCurseState.getOrCreate(world);
            curseState.onPlayerJoin(player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            ServerWorld world = player.getServerWorld();
            ForestsCurseState curseState = ForestsCurseState.getOrCreate(world);
            curseState.onPlayerLeave(player);
        });
    }

    private static void dropCursedEssence(HostileEntity mob, ServerWorld world) {
        int dropCount = 1 + world.getRandom().nextInt(3);
        if (world.getRandom().nextFloat() < 0.15f) {
            dropCount += 1 + world.getRandom().nextInt(2);
        }

        var scoreboard = world.getScoreboard();
        String teamName = "cursedEssenceGlow";
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.addTeam(teamName);
            team.setColor(Formatting.BLUE);
            team.setShowFriendlyInvisibles(false);
        }

        for (int i = 0; i < dropCount; i++) {
            ItemStack essence = new ItemStack(ModItems.CURSE_ESSENCE);
            ItemEntity itemEntity = new ItemEntity(world, mob.getX(), mob.getY() + 0.5, mob.getZ(), essence);

            double velocityX = (world.getRandom().nextDouble() - 0.5) * 0.3;
            double velocityY = world.getRandom().nextDouble() * 0.2 + 0.1;
            double velocityZ = (world.getRandom().nextDouble() - 0.5) * 0.3;
            itemEntity.setVelocity(velocityX, velocityY, velocityZ);

            world.spawnEntity(itemEntity);
            scoreboard.addScoreHolderToTeam(itemEntity.getUuidAsString(), team);
            itemEntity.setGlowing(true);
        }

        world.spawnParticles(
                ParticleTypes.END_ROD,
                mob.getX(), mob.getY() + 1, mob.getZ(),
                8,
                0.5, 0.5, 0.5,
                0.05
        );
    }


}