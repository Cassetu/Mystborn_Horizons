package cassetu.mystbornhorizons.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.structure.StructureStart;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;

public class ForestHeartItem extends Item {
    public ForestHeartItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.mystbornhorizons.forest_heart.tooltip1").formatted(Formatting.GREEN));
        tooltip.add(Text.translatable("tooltip.mystbornhorizons.forest_heart.tooltip2").formatted(Formatting.DARK_GREEN));
        tooltip.add(Text.translatable("tooltip.mystbornhorizons.forest_heart.tooltip3").formatted(Formatting.RED));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true; // Makes the item have an enchanted glint
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;

            // Find the nearest moss arena structure
            BlockPos mossArenaPos = findNearestMossArena(serverWorld, user.getBlockPos());

            if (mossArenaPos != null) {
                // Create a trail of green particles pointing toward the structure
                createParticleTrail(serverWorld, user.getPos(), mossArenaPos);

                // Play a mystical sound effect
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0f,
                        1.2f + world.getRandom().nextFloat() * 0.4f);

                // Increment usage stat
                user.incrementStat(Stats.USED.getOrCreateStat(this));

                // Consume the item if not in creative mode
                if (!user.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                // Send a message indicating direction
                Vec3d direction = new Vec3d(mossArenaPos.getX() + 0.5, mossArenaPos.getY() + 0.5, mossArenaPos.getZ() + 0.5)
                        .subtract(user.getPos()).normalize();
                String directionText = getDirectionText(direction);
                user.sendMessage(Text.translatable("item.mystbornhorizons.forest_heart.direction", directionText)
                        .formatted(Formatting.GREEN), true);

                return TypedActionResult.success(itemStack);
            } else {
                // No structure found - send a message to the player
                user.sendMessage(Text.translatable("item.mystbornhorizons.forest_heart.no_structure")
                        .formatted(Formatting.RED), true);
                return TypedActionResult.fail(itemStack);
            }
        }

        return TypedActionResult.success(itemStack);
    }

    private BlockPos findNearestMossArena(ServerWorld world, BlockPos playerPos) {
        try {
            return findNearestMossArenaFallback(world, playerPos);
        } catch (Exception e) {
            // swallow and return null if anything goes wrong (caller already handles null)
            return null;
        }
    }

    /**
     * Fallback search that scans chunks in an expanding square around the player.
     * Uses the correct StructureAccessor.getStructureStarts(ChunkSectionPos, Structure) API.
     */
    private BlockPos findNearestMossArenaFallback(ServerWorld world, BlockPos playerPos) {
        try {
            // Get the structure registry and resolve our structure by id
            Registry<Structure> structureRegistry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
            Identifier mossId = Identifier.of("mystbornhorizons", "moss_arena");
            Structure mossArenaStructure = structureRegistry.get(mossId);

            if (mossArenaStructure == null) {
                // structure not registered
                return null;
            }

            // Search in expanding squares around the player
            int maxRadius = 300; // chunks
            int playerChunkX = playerPos.getX() >> 4;
            int playerChunkZ = playerPos.getZ() >> 4;

            for (int radius = 0; radius <= maxRadius; radius++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        // Only check the perimeter of the current radius
                        if (Math.abs(dx) != radius && Math.abs(dz) != radius) {
                            continue;
                        }

                        ChunkPos chunkPos = new ChunkPos(playerChunkX + dx, playerChunkZ + dz);

                        try {
                            // Convert chunk coordinates to a ChunkSectionPos (y=0) and query StructureAccessor properly
                            ChunkSectionPos sectionPos = ChunkSectionPos.from(chunkPos, 0);
                            List<StructureStart> structureStarts = world.getStructureAccessor().getStructureStarts(sectionPos, mossArenaStructure);

                            if (structureStarts != null && !structureStarts.isEmpty()) {
                                // return the first valid start's bounding-box center
                                for (StructureStart start : structureStarts) {
                                    if (start != null) {
                                        // StructureStart bounding box center -> BlockPos
                                        BlockPos center = start.getBoundingBox().getCenter();
                                        if (center != null) {
                                            return center;
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                            // if getting structure info for a chunk fails, continue searching other chunks
                        }
                    }
                }
            }
        } catch (Exception e) {
            // give up and return null
        }

        return null;
    }

    private void createParticleTrail(ServerWorld world, Vec3d playerPos, BlockPos targetPos) {
        Vec3d target = new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
        Vec3d direction = target.subtract(playerPos).normalize();

        // Create a trail of particles extending outward from the player
        double maxDistance = Math.min(50.0, playerPos.distanceTo(target)); // Maximum trail length
        int particleCount = (int) Math.min(30, Math.max(1, maxDistance)); // Number of particles

        for (int i = 0; i < particleCount; i++) {
            double progress = (double) i / particleCount;
            double distance = progress * maxDistance;

            // Calculate position along the direction vector
            Vec3d particlePos = playerPos.add(direction.multiply(distance));

            // Add some randomness to make it look more organic
            double randomX = (world.getRandom().nextDouble() - 0.5) * 0.5;
            double randomY = (world.getRandom().nextDouble() - 0.5) * 0.5;
            double randomZ = (world.getRandom().nextDouble() - 0.5) * 0.5;

            particlePos = particlePos.add(randomX, randomY, randomZ);

            // Spawn different green particles for variety
            if (i % 3 == 0) {
                // Happy villager particles (green)
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                        particlePos.x, particlePos.y, particlePos.z,
                        1, 0.0, 0.0, 0.0, 0.0);
            } else if (i % 3 == 1) {
                // Composter particles (also green)
                world.spawnParticles(ParticleTypes.COMPOSTER,
                        particlePos.x, particlePos.y, particlePos.z,
                        2, 0.1, 0.1, 0.1, 0.0);
            } else {
                // Spore blossom particles for a mystical effect
                world.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR,
                        particlePos.x, particlePos.y, particlePos.z,
                        1, 0.0, 0.0, 0.0, 0.0);
            }
        }

        // Add some extra particles around the player for emphasis
        for (int i = 0; i < 10; i++) {
            double angle = (2 * Math.PI * i) / 10;
            double radius = 1.5;
            double x = playerPos.x + Math.cos(angle) * radius;
            double z = playerPos.z + Math.sin(angle) * radius;
            double y = playerPos.y + 1.0;

            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                    x, y, z, 1, 0.0, 0.2, 0.0, 0.1);
        }
    }

    private String getDirectionText(Vec3d direction) {
        double x = direction.x;
        double z = direction.z;

        // Determine primary direction
        if (Math.abs(x) > Math.abs(z)) {
            if (x > 0) {
                return z > 0.3 ? "Southeast" : z < -0.3 ? "Northeast" : "East";
            } else {
                return z > 0.3 ? "Southwest" : z < -0.3 ? "Northwest" : "West";
            }
        } else {
            if (z > 0) {
                return x > 0.3 ? "Southeast" : x < -0.3 ? "Southwest" : "South";
            } else {
                return x > 0.3 ? "Northeast" : x < -0.3 ? "Northwest" : "North";
            }
        }
    }
}
