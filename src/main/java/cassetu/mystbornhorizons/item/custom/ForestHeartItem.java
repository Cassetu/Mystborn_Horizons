package cassetu.mystbornhorizons.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
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

                // Send a message indicating direction and distance
                Vec3d direction = Vec3d.of(mossArenaPos).subtract(user.getPos()).normalize();
                String directionText = getDirectionText(direction);
                int distance = (int) Math.sqrt(user.getBlockPos().getSquaredDistance(mossArenaPos));

                user.sendMessage(Text.translatable("item.mystbornhorizons.forest_heart.direction", directionText, distance)
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
        // Try multiple approaches to find the structure

        // Method 1: Try with your mod's namespace first
        Identifier mossId1 = Identifier.of("mystbornhorizons", "moss_arena");
        TagKey<Structure> mossArenaTag1 = TagKey.of(RegistryKeys.STRUCTURE, mossId1);

        int searchRadius = 200; // Increased search radius
        BlockPos found = world.locateStructure(mossArenaTag1, playerPos, searchRadius, false);

        if (found != null) {
            System.out.println("[ForestHeart] Found Moss Arena (mod namespace) at " + found);
            return found;
        }

        // Method 2: Try with minecraft namespace
        Identifier mossId2 = Identifier.of("minecraft", "moss_arena");
        TagKey<Structure> mossArenaTag2 = TagKey.of(RegistryKeys.STRUCTURE, mossId2);

        found = world.locateStructure(mossArenaTag2, playerPos, searchRadius, false);

        if (found != null) {
            System.out.println("[ForestHeart] Found Moss Arena (minecraft namespace) at " + found);
            return found;
        }

        // Method 3: Try to find by iterating through all structures (debugging)
        System.out.println("[ForestHeart] Attempting to find structure by iteration...");
        var structureRegistry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);

        for (var entry : structureRegistry.getEntrySet()) {
            String structureName = entry.getKey().getValue().toString();
            if (structureName.contains("moss") || structureName.contains("arena")) {
                System.out.println("[ForestHeart] Found potential structure: " + structureName);

                // Try to locate this structure
                TagKey<Structure> tag = TagKey.of(RegistryKeys.STRUCTURE, entry.getKey().getValue());
                BlockPos pos = world.locateStructure(tag, playerPos, searchRadius, false);
                if (pos != null) {
                    System.out.println("[ForestHeart] Successfully located: " + structureName + " at " + pos);
                    return pos;
                }
            }
        }

        System.out.println("[ForestHeart] Moss Arena not found within " + searchRadius + " chunks.");
        System.out.println("[ForestHeart] Available structures:");
        for (var entry : structureRegistry.getEntrySet()) {
            System.out.println("  - " + entry.getKey().getValue().toString());
        }

        return null;
    }

    private void createParticleTrail(ServerWorld world, Vec3d playerPos, BlockPos targetPos) {
        Vec3d target = Vec3d.of(targetPos);
        Vec3d direction = target.subtract(playerPos).normalize();

        // Create a trail of particles extending outward from the player
        double maxDistance = Math.min(30.0, playerPos.distanceTo(target) * 0.3); // Shorter, more visible trail
        int particleCount = 20; // Fixed number of particles for consistency

        for (int i = 0; i < particleCount; i++) {
            double progress = (double) i / (particleCount - 1);
            double distance = progress * maxDistance;

            // Calculate position along the direction vector
            Vec3d particlePos = playerPos.add(direction.multiply(distance));

            // Elevate particles slightly for better visibility
            particlePos = particlePos.add(0, 1.0 + progress * 2.0, 0);

            // Add some randomness to make it look more organic, but less chaotic
            double randomX = (world.getRandom().nextDouble() - 0.5) * 0.3;
            double randomY = (world.getRandom().nextDouble() - 0.5) * 0.3;
            double randomZ = (world.getRandom().nextDouble() - 0.5) * 0.3;

            particlePos = particlePos.add(randomX, randomY, randomZ);

            // Use more visible particles
            if (i % 2 == 0) {
                // Happy villager particles (green) - very visible
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                        particlePos.x, particlePos.y, particlePos.z,
                        3, 0.1, 0.1, 0.1, 0.02);
            } else {
                // Composter particles (also green)
                world.spawnParticles(ParticleTypes.COMPOSTER,
                        particlePos.x, particlePos.y, particlePos.z,
                        2, 0.1, 0.1, 0.1, 0.0);
            }
        }

        // Add a burst of particles around the player for emphasis
        for (int i = 0; i < 15; i++) {
            double angle = (2 * Math.PI * i) / 15;
            double radius = 2.0;
            double x = playerPos.x + Math.cos(angle) * radius;
            double z = playerPos.z + Math.sin(angle) * radius;
            double y = playerPos.y + 1.5;

            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                    x, y, z, 2, 0.0, 0.3, 0.0, 0.1);
        }

        // Add some upward-floating particles for extra effect
        for (int i = 0; i < 8; i++) {
            double randomX = playerPos.x + (world.getRandom().nextDouble() - 0.5) * 4.0;
            double randomZ = playerPos.z + (world.getRandom().nextDouble() - 0.5) * 4.0;

            world.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR,
                    randomX, playerPos.y + 2.0, randomZ,
                    1, 0.0, 0.1, 0.0, 0.05);
        }
    }

    private String getDirectionText(Vec3d direction) {
        double x = direction.x;
        double z = direction.z;

        // Determine primary direction with better precision
        double angle = Math.atan2(z, x) * 180.0 / Math.PI;
        if (angle < 0) angle += 360;

        if (angle >= 337.5 || angle < 22.5) return "East";
        else if (angle >= 22.5 && angle < 67.5) return "Southeast";
        else if (angle >= 67.5 && angle < 112.5) return "South";
        else if (angle >= 112.5 && angle < 157.5) return "Southwest";
        else if (angle >= 157.5 && angle < 202.5) return "West";
        else if (angle >= 202.5 && angle < 247.5) return "Northwest";
        else if (angle >= 247.5 && angle < 292.5) return "North";
        else return "Northeast";
    }
}