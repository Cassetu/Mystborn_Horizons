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

    // NOTICE: THE SYSTEM PRINTS A LOT OF DEBUG INFO TO THE SERVER CONSOLE.
    // THIS IS INTENTIONAL TO HELP WITH TROUBLESHOOTING STRUCTURE LOCATING
    // AND WAS MADE WITH THE HELP OF CLAUDE.AI

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
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;

            BlockPos mossArenaPos = findNearestMossArena(serverWorld, user.getBlockPos());

            if (mossArenaPos != null) {
                createParticleTrail(serverWorld, user.getPos(), mossArenaPos);

                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0f,
                        1.2f + world.getRandom().nextFloat() * 0.4f);

                user.incrementStat(Stats.USED.getOrCreateStat(this));

                Vec3d direction = Vec3d.of(mossArenaPos).subtract(user.getPos()).normalize();
                String directionText = getDirectionText(direction);
                int distance = (int) Math.sqrt(user.getBlockPos().getSquaredDistance(mossArenaPos));

                user.sendMessage(Text.translatable("item.mystbornhorizons.forest_heart.direction", directionText, distance)
                        .formatted(Formatting.GREEN), true);

                return TypedActionResult.success(itemStack);
            } else {
                user.sendMessage(Text.translatable("item.mystbornhorizons.forest_heart.no_structure")
                        .formatted(Formatting.RED), true);
                return TypedActionResult.fail(itemStack);
            }
        }
        return TypedActionResult.success(itemStack);
    }

    private BlockPos findNearestMossArena(ServerWorld world, BlockPos playerPos) {
        System.out.println("[ForestHeart] Starting structure search from position: " + playerPos);

        Identifier mossId = Identifier.of("minecraft", "moss_arena");
        System.out.println("[ForestHeart] Looking for structure: " + mossId);

        try {
            var structureRegistry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
            System.out.println("[ForestHeart] Structure registry size: " + structureRegistry.size());

            var structureEntry = structureRegistry.getEntry(mossId);
            if (structureEntry.isPresent()) {
                System.out.println("[ForestHeart] Found structure entry in registry");

                var entryList = structureRegistry.getEntryList(TagKey.of(RegistryKeys.STRUCTURE, mossId));

                if (entryList.isEmpty()) {
                    System.out.println("[ForestHeart] No entry list found, trying individual entry...");
                    var singleEntryList = net.minecraft.registry.entry.RegistryEntryList.of(structureEntry.get());

                    var result = world.getChunkManager().getChunkGenerator()
                            .locateStructure(world, singleEntryList, playerPos, 100, false);

                    if (result != null) {
                        BlockPos structurePos = result.getFirst();
                        double distance = Math.sqrt(playerPos.getSquaredDistance(structurePos));
                        System.out.println("[ForestHeart] ✓ Found via single entry method: " + structurePos + " (distance: " + (int)distance + " blocks)");
                        return structurePos;
                    }
                } else {
                    System.out.println("[ForestHeart] Using entry list...");
                    var result = world.getChunkManager().getChunkGenerator()
                            .locateStructure(world, entryList.get(), playerPos, 100, false);

                    if (result != null) {
                        BlockPos structurePos = result.getFirst();
                        double distance = Math.sqrt(playerPos.getSquaredDistance(structurePos));
                        System.out.println("[ForestHeart] ✓ Found via entry list: " + structurePos + " (distance: " + (int)distance + " blocks)");
                        return structurePos;
                    }
                }
            } else {
                System.out.println("[ForestHeart] Structure entry not found in registry!");
            }
        } catch (Exception e) {
            System.out.println("[ForestHeart] Error in ChunkGenerator approach: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("[ForestHeart] Trying structure sets approach...");
        try {
            var structureSetRegistry = world.getRegistryManager().get(RegistryKeys.STRUCTURE_SET);
            System.out.println("[ForestHeart] Available structure sets:");
            for (var entry : structureSetRegistry.getEntrySet()) {
                String setName = entry.getKey().getValue().toString();
                System.out.println("  - " + setName);

                if (setName.contains("moss") || setName.contains("arena")) {
                    System.out.println("[ForestHeart] Found potential structure set: " + setName);
                }
            }
        } catch (Exception e) {
            System.out.println("[ForestHeart] Error checking structure sets: " + e.getMessage());
        }

        System.out.println("[ForestHeart] Trying command-style locate...");
        try {
            var registry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
            var holder = registry.getEntry(mossId);

            if (holder.isPresent()) {
                System.out.println("[ForestHeart] Structure holder found, attempting locate...");

                int[] radii = {50, 100, 200, 500};
                for (int radius : radii) {
                    System.out.println("[ForestHeart] Searching with radius: " + radius + " chunks");

                    var pair = world.getChunkManager().getChunkGenerator().locateStructure(
                            world,
                            net.minecraft.registry.entry.RegistryEntryList.of(holder.get()),
                            playerPos,
                            radius,
                            false
                    );

                    if (pair != null) {
                        BlockPos foundPos = pair.getFirst();
                        double distance = Math.sqrt(playerPos.getSquaredDistance(foundPos));
                        System.out.println("[ForestHeart] ✓ Found with command-style locate: " + foundPos + " (distance: " + (int)distance + " blocks)");
                        return foundPos;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[ForestHeart] Error in command-style locate: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("[ForestHeart] Falling back to manual chunk scanning...");
        try {
            int chunkRadius = 100;
            int checked = 0;

            for (int x = -chunkRadius; x <= chunkRadius; x += 2) {
                for (int z = -chunkRadius; z <= chunkRadius; z += 2) {
                    checked++;
                    if (checked % 1000 == 0) {
                        System.out.println("[ForestHeart] Checked " + checked + " chunks...");
                    }

                    int chunkX = (playerPos.getX() >> 4) + x;
                    int chunkZ = (playerPos.getZ() >> 4) + z;

                    var chunk = world.getChunk(chunkX, chunkZ);
                    var structureStarts = chunk.getStructureStarts();

                    for (var entry : structureStarts.entrySet()) {
                        var structureKey = entry.getKey();
                        var start = entry.getValue();

                        var structureRegistry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
                        var structureId = structureRegistry.getId(structureKey);

                        if (structureId != null && structureId.toString().equals("minecraft:moss_arena") && start.hasChildren()) {
                            BlockPos structurePos = new BlockPos(chunkX * 16 + 8, 64, chunkZ * 16 + 8);
                            System.out.println("[ForestHeart] ✓ Found via manual scanning: " + structurePos);
                            return structurePos;
                        }
                    }
                }
            }

            System.out.println("[ForestHeart] Manual scan completed, checked " + checked + " chunks");
        } catch (Exception e) {
            System.out.println("[ForestHeart] Error in manual scanning: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("[ForestHeart] All methods exhausted. Structure may be further than 100 chunks away.");
        return null;
    }

    private void createParticleTrail(ServerWorld world, Vec3d playerPos, BlockPos targetPos) {
        Vec3d target = Vec3d.of(targetPos);
        Vec3d direction = target.subtract(playerPos).normalize();

        double maxDistance = Math.min(30.0, playerPos.distanceTo(target) * 0.3);
        int particleCount = 20;

        for (int i = 0; i < particleCount; i++) {
            double progress = (double) i / (particleCount - 1);
            double distance = progress * maxDistance;

            Vec3d particlePos = playerPos.add(direction.multiply(distance));

            particlePos = particlePos.add(0, 1.0 + progress * 2.0, 0);

            double randomX = (world.getRandom().nextDouble() - 0.5) * 0.3;
            double randomY = (world.getRandom().nextDouble() - 0.5) * 0.3;
            double randomZ = (world.getRandom().nextDouble() - 0.5) * 0.3;

            particlePos = particlePos.add(randomX, randomY, randomZ);

            if (i % 2 == 0) {
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                        particlePos.x, particlePos.y, particlePos.z,
                        3, 0.1, 0.1, 0.1, 0.02);
            } else {
                world.spawnParticles(ParticleTypes.COMPOSTER,
                        particlePos.x, particlePos.y, particlePos.z,
                        2, 0.1, 0.1, 0.1, 0.0);
            }
        }

        for (int i = 0; i < 15; i++) {
            double angle = (2 * Math.PI * i) / 15;
            double radius = 2.0;
            double x = playerPos.x + Math.cos(angle) * radius;
            double z = playerPos.z + Math.sin(angle) * radius;
            double y = playerPos.y + 1.5;

            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                    x, y, z, 2, 0.0, 0.3, 0.0, 0.1);
        }

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