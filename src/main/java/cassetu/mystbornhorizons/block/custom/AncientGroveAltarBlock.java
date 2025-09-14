package cassetu.mystbornhorizons.block.custom;

import cassetu.mystbornhorizons.entity.ModEntities;
import cassetu.mystbornhorizons.entity.custom.HavenCoreEntity;
import cassetu.mystbornhorizons.entity.custom.HavenicaEntity;
import cassetu.mystbornhorizons.item.ModItems;
import cassetu.mystbornhorizons.util.CutsceneManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public class AncientGroveAltarBlock extends Block {
    public AncientGroveAltarBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                              BlockHitResult hit) {
            if (!world.isClient) {
                ServerWorld serverWorld = (ServerWorld) world;

                // Check if player has Forest Heart
                ItemStack heldItem = player.getMainHandStack();
                if (!heldItem.isOf(ModItems.FOREST_HEART)) {
                    player.sendMessage(Text.literal("§c This altar requires a §2Forest Heart§c to activate..."), false);
                    return ActionResult.FAIL;
                }

                // Prevent multiple bosses
                Box searchBox = new Box(pos).expand(50);
                if (!serverWorld.getEntitiesByClass(HavenicaEntity.class, searchBox, e -> true).isEmpty()) {
                    player.sendMessage(Text.literal("§cHavenica's presence is already felt in this area..."), false);
                    return ActionResult.FAIL;
                }

                // Prevent duplicate cutscenes
                if (CutsceneManager.isCutsceneActive(serverWorld, pos)) {
                    player.sendMessage(Text.literal("§6The ritual is already in progress..."), false);
                    return ActionResult.FAIL;
                }

                // Consume the Forest Heart
                if (!player.getAbilities().creativeMode) {
                    heldItem.decrement(1);
                }

                // Start the spawning cutscene
                startSpawningCutscene(serverWorld, pos);
            }

            return ActionResult.SUCCESS;
        }

    private void startSpawningCutscene(ServerWorld world, BlockPos pos) {
        System.out.println("DEBUG: Starting spawning cutscene at " + pos);

        // Initialize cutscene data
        Set<ServerPlayerEntity> cutscenePlayers = new HashSet<>();
        Map<ServerPlayerEntity, GameMode> originalGameModes = new HashMap<>();
        Map<ServerPlayerEntity, Vec3d> originalPositions = new HashMap<>();

        // Find nearby players and put them in spectator mode
        Box cutsceneArea = new Box(pos).expand(25.0);
        List<ServerPlayerEntity> nearbyPlayers = world.getNonSpectatingEntities(PlayerEntity.class, cutsceneArea)
                .stream()
                .filter(player -> player instanceof ServerPlayerEntity)
                .map(player -> (ServerPlayerEntity) player)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (ServerPlayerEntity player : nearbyPlayers) {
            try {
                cutscenePlayers.add(player);
                originalGameModes.put(player, player.interactionManager.getGameMode());
                originalPositions.put(player, player.getPos());

                player.changeGameMode(GameMode.SPECTATOR);

                // Position camera for dramatic view
                double cameraX = pos.getX() + 0.5;
                double cameraY = pos.getY() + 8.0;
                double cameraZ = pos.getZ() + 12.0;

                player.teleport(world, cameraX, cameraY, cameraZ, 0, 15);
                Vec3d lookTarget = new Vec3d(pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5);
                player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, lookTarget);

                player.sendMessage(Text.literal("§2§l⚡ §a§lTHE FOREST AWAKENS §2§l⚡")
                        .formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("§6Witness the birth of an ancient guardian...")
                        .formatted(Formatting.GOLD), false);
            } catch (Exception e) {
                System.out.println("ERROR: Failed to setup cutscene for player " + player.getName().getString());
                e.printStackTrace();
            }
        }

        // Start the cutscene using CutsceneManager
        CutsceneManager.startCutscene(world, pos, (serverWorld, altarPos, tick) -> {
            return tickSpawningCutscene(serverWorld, altarPos, tick, cutscenePlayers, originalGameModes, originalPositions);
        });
    }

    private boolean tickSpawningCutscene(ServerWorld serverWorld, BlockPos altarPos, int cutsceneTick,
                                         Set<ServerPlayerEntity> cutscenePlayers,
                                         Map<ServerPlayerEntity, GameMode> originalGameModes,
                                         Map<ServerPlayerEntity, Vec3d> originalPositions) {

        Vec3d altarCenter = new Vec3d(altarPos.getX() + 0.5, altarPos.getY() + 1, altarPos.getZ() + 0.5);

        // Phase 1: Ground awakening (ticks 1-40)
        if (cutsceneTick <= 40) {
            // Expanding ground circles
            double maxRadius = (cutsceneTick / 40.0) * 15.0;
            for (double r = 0; r <= maxRadius; r += 0.8) {
                int particlesInRing = Math.max(8, (int) (r * 4));
                for (int i = 0; i < particlesInRing; i++) {
                    double angle = (i / (double) particlesInRing) * Math.PI * 2;
                    double x = altarCenter.x + Math.cos(angle) * r;
                    double z = altarCenter.z + Math.sin(angle) * r;

                    serverWorld.spawnParticles(ParticleTypes.MYCELIUM,
                            x, altarPos.getY() + 0.1, z, 2, 0.2, 0.1, 0.2, 0.05);

                    if (r > 8) {
                        serverWorld.spawnParticles(ParticleTypes.COMPOSTER,
                                x, altarPos.getY() + 0.5, z, 1, 0.1, 0.1, 0.1, 0.02);
                    }
                }
            }

            // Sound effects
            if (cutsceneTick == 10) {
                serverWorld.playSound(null, altarPos, SoundEvents.BLOCK_ROOTED_DIRT_BREAK,
                        SoundCategory.BLOCKS, 1.5f, 0.8f);
            }
            if (cutsceneTick == 25) {
                serverWorld.playSound(null, altarPos, SoundEvents.ENTITY_ENDER_DRAGON_GROWL,
                        SoundCategory.HOSTILE, 1.2f, 0.6f);
            }
        }

        // Phase 2: Orb summoning (ticks 41-80)
        else if (cutsceneTick <= 80) {
            // Spawn haven orbs at designated positions
            if (cutsceneTick == 45) {
                spawnHavenOrbs(serverWorld, altarPos);
            }

            // Energy beams from orbs to altar
            if (cutsceneTick > 50) {
                createOrbToAltarBeams(serverWorld, altarPos);
            }

            // Rising energy pillar at altar
            double pillarHeight = Math.min(((cutsceneTick - 40) / 40.0) * 10.0, 10.0);
            for (double y = 0; y <= pillarHeight; y += 0.2) {
                serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        altarCenter.x, altarCenter.y + y, altarCenter.z,
                        3, 0.2, 0.1, 0.2, 0.02);

                if (y > 6) {
                    serverWorld.spawnParticles(ParticleTypes.ENCHANT,
                            altarCenter.x, altarCenter.y + y, altarCenter.z,
                            2, 0.3, 0.1, 0.3, 0.08);
                }
            }
        }

        // Phase 3: Boss materialization (ticks 81-119)
        else if (cutsceneTick <= 119) {
            // Intense swirling effects
            double intensity = (cutsceneTick - 80) / 40.0;
            for (int spiral = 0; spiral < 6; spiral++) {
                double spiralHeight = intensity * 8.0;
                double angle = (cutsceneTick * 0.6) + (spiral * Math.PI / 3);
                double radius = 2.0 + Math.sin(intensity * Math.PI) * 1.5;

                double x = altarCenter.x + Math.cos(angle) * radius;
                double z = altarCenter.z + Math.sin(angle) * radius;

                serverWorld.spawnParticles(ParticleTypes.WITCH,
                        x, altarCenter.y + spiralHeight, z, 3, 0.1, 0.1, 0.1, 0.0);
                serverWorld.spawnParticles(ParticleTypes.SCULK_SOUL,
                        x, altarCenter.y + spiralHeight, z, 2, 0.15, 0.15, 0.15, 0.05);
            }

            // Dramatic sound buildup
            if (cutsceneTick == 90) {
                serverWorld.playSound(null, altarPos, SoundEvents.ENTITY_WARDEN_ROAR,
                        SoundCategory.HOSTILE, 2.0f, 0.5f);
            }
            if (cutsceneTick == 110) {
                serverWorld.playSound(null, altarPos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                        SoundCategory.WEATHER, 2.5f, 0.7f);
            }
        }

        // Phase 4: Boss spawn and finale (tick 120)
        // Phase 4: Boss spawn and finale (ticks 120-125)
        else if (cutsceneTick >= 120 && cutsceneTick <= 125) {
            if (cutsceneTick == 120) {
                System.out.println("DEBUG: Starting boss spawn sequence at tick 120");

                // Massive explosion effect
                serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER,
                        altarCenter.x, altarCenter.y + 4, altarCenter.z, 5, 1.0, 1.0, 1.0, 0.0);

                // Debug entity type registration
                System.out.println("DEBUG: ModEntities.HAVENICA = " + ModEntities.HAVENICA);

                try {
                    System.out.println("DEBUG: Attempting to create Havenica entity...");
                    HavenicaEntity havenica = ModEntities.HAVENICA.create(serverWorld);

                    if (havenica == null) {
                        System.out.println("ERROR: ModEntities.HAVENICA.create() returned null!");
                        havenica = new HavenicaEntity(ModEntities.HAVENICA, serverWorld);
                        System.out.println("DEBUG: Alternative creation used: " + (havenica != null));
                    } else {
                        System.out.println("DEBUG: Havenica entity created successfully: " + havenica);
                    }

                    if (havenica != null) {
                        // Set position and properties
                        havenica.refreshPositionAndAngles(
                                altarCenter.x, altarPos.getY() + 1, altarCenter.z, 0, 0);

                        // Set custom name
                        havenica.setCustomName(Text.literal("§2§lHavenica, Guardian of the Grove"));
                        System.out.println("DEBUG: Set custom name: " + havenica.getCustomName());

                        // Try to spawn the entity properly
                        System.out.println("DEBUG: Attempting to spawn entity with spawnNewEntityAndPassengers...");
                        boolean spawned = serverWorld.spawnNewEntityAndPassengers(havenica);
                        System.out.println("DEBUG: Spawn result: " + spawned);

                        if (!spawned) {
                            System.out.println("ERROR: spawnNewEntityAndPassengers() returned false!");
                        } else {
                            System.out.println("SUCCESS: Havenica spawned successfully!");

                            // Verify nearby Havenicas
                            java.util.List<HavenicaEntity> nearbyHavenicas = serverWorld.getEntitiesByClass(
                                    HavenicaEntity.class,
                                    new Box(altarPos).expand(10),
                                    entity -> true
                            );
                            System.out.println("DEBUG: Found " + nearbyHavenicas.size() + " Havenica entities nearby after spawn");
                        }

                        // Spawn effect rings
                        for (int ring = 0; ring < 5; ring++) {
                            double ringRadius = (ring + 1) * 2.0;
                            int particleCount = (int) (ringRadius * 8);

                            for (int i = 0; i < particleCount; i++) {
                                double angle = (i / (double) particleCount) * Math.PI * 2;
                                double x = altarCenter.x + Math.cos(angle) * ringRadius;
                                double z = altarCenter.z + Math.sin(angle) * ringRadius;

                                serverWorld.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                                        x, altarPos.getY() + 1, z, 5, 0.3, 0.5, 0.3, 0.1);
                                serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                                        x, altarPos.getY() + 2, z, 3, 0.2, 0.3, 0.2, 0.05);
                            }
                        }
                    } else {
                        System.out.println("CRITICAL ERROR: Could not create Havenica entity at all!");
                    }

                } catch (Exception e) {
                    System.out.println("EXCEPTION during Havenica spawn: " + e.getMessage());
                    e.printStackTrace();
                }

                serverWorld.playSound(null, altarPos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE,
                        SoundCategory.HOSTILE, 3.0f, 0.4f);
            }

            // Continue with finale effects for ticks 121-124
            if (cutsceneTick > 120 && cutsceneTick < 125) {
                // Add some lingering particle effects after boss spawn
                for (int i = 0; i < 3; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double radius = Math.random() * 5;
                    double x = altarCenter.x + Math.cos(angle) * radius;
                    double z = altarCenter.z + Math.sin(angle) * radius;

                    serverWorld.spawnParticles(ParticleTypes.SOUL,
                            x, altarCenter.y + Math.random() * 3, z, 1, 0.1, 0.1, 0.1, 0.02);
                }
            }

            // End cutscene at tick 125
            if (cutsceneTick >= 125) {
                endSpawningCutscene(cutscenePlayers, originalGameModes, originalPositions);
                return false; // End the cutscene
            }

            return true; // Continue cutscene
        }
        return true;
    }


        private void spawnHavenOrbs(ServerWorld world, BlockPos altarPos) {
        System.out.println("DEBUG: Spawning Haven Orbs");

        // Cardinal directions (N, S, E, W) - 11 blocks out
        BlockPos[] cardinalPositions = {
                altarPos.add(0, 2, -11),  // North
                altarPos.add(0, 2, 11),   // South
                altarPos.add(11, 2, 0),   // East
                altarPos.add(-11, 2, 0)   // West
        };

        // Diagonal directions (NE, NW, SE, SW) - 11 blocks out
        BlockPos[] diagonalPositions = {
                altarPos.add(11, 2, -11),  // Northeast (8+3 = 11)
                altarPos.add(-11, 2, -11), // Northwest
                altarPos.add(11, 2, 11),   // Southeast
                altarPos.add(-11, 2, 11)   // Southwest
        };

        // Spawn cardinal orbs
        for (BlockPos pos : cardinalPositions) {
            spawnHavenOrbEntityAt(world, pos, true);
        }

        // Spawn diagonal orbs
        for (BlockPos pos : diagonalPositions) {
            spawnHavenOrbEntityAt(world, pos, false);
        }

        world.playSound(null, altarPos, SoundEvents.BLOCK_BEACON_ACTIVATE,
                SoundCategory.BLOCKS, 1.5f, 1.2f);
    }

    private void spawnHavenOrbEntityAt(ServerWorld world, BlockPos pos, boolean isCardinal) {
        try {
            System.out.println("DEBUG: Spawning Haven Orb at " + pos + " (Cardinal: " + isCardinal + ")");

            // Check if ModEntities.HAVEN_CORE exists
            if (ModEntities.HAVEN_CORE == null) {
                System.out.println("ERROR: ModEntities.HAVEN_CORE is null! Make sure it's registered.");
                spawnHavenOrbParticlesOnly(world, pos, isCardinal); // Fallback to particles
                return;
            }

            // Create the Haven Core entity
            HavenCoreEntity havenCore = ModEntities.HAVEN_CORE.create(world);
            if (havenCore != null) {
                havenCore.refreshPositionAndAngles(
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0);

                boolean spawned = world.spawnEntity(havenCore);
                System.out.println("DEBUG: Haven Orb spawn result: " + spawned);

                if (!spawned) {
                    System.out.println("ERROR: Failed to spawn Haven Orb entity, falling back to particles");
                    spawnHavenOrbParticlesOnly(world, pos, isCardinal);
                }
            } else {
                System.out.println("ERROR: ModEntities.HAVEN_CORE.create() returned null, falling back to particles");
                spawnHavenOrbParticlesOnly(world, pos, isCardinal);
            }

        } catch (Exception e) {
            System.out.println("EXCEPTION during Haven Orb spawn: " + e.getMessage());
            e.printStackTrace();
            // Fallback to particle effect
            spawnHavenOrbParticlesOnly(world, pos, isCardinal);
        }
    }

    private void spawnHavenOrbParticlesOnly(ServerWorld world, BlockPos pos, boolean isCardinal) {
        // Create visual orb effect (particle-only fallback)
        for (int i = 0; i < 20; i++) {
            double angle = (i / 20.0) * Math.PI * 2;
            double radius = 1.0;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;

            world.spawnParticles(ParticleTypes.END_ROD,
                    x, pos.getY() + 0.5, z, 2, 0.1, 0.1, 0.1, 0.0);
            world.spawnParticles(isCardinal ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                    x, pos.getY() + 0.5, z, 1, 0.05, 0.05, 0.05, 0.02);
        }

        // Central orb core
        world.spawnParticles(ParticleTypes.GLOW,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                10, 0.2, 0.2, 0.2, 0.0);
    }

    private void spawnHavenOrbAt(ServerWorld world, BlockPos pos, boolean isCardinal) {
        // Create visual orb effect (you might want to create an actual entity for these)
        for (int i = 0; i < 20; i++) {
            double angle = (i / 20.0) * Math.PI * 2;
            double radius = 1.0;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;

            world.spawnParticles(ParticleTypes.END_ROD,
                    x, pos.getY() + 0.5, z, 2, 0.1, 0.1, 0.1, 0.0);
            world.spawnParticles(isCardinal ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME,
                    x, pos.getY() + 0.5, z, 1, 0.05, 0.05, 0.05, 0.02);
        }

        // Central orb core
        world.spawnParticles(ParticleTypes.GLOW,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                10, 0.2, 0.2, 0.2, 0.0);
    }

    private void createOrbToAltarBeams(ServerWorld world, BlockPos altarPos) {
        Vec3d altarCenter = new Vec3d(altarPos.getX() + 0.5, altarPos.getY() + 1, altarPos.getZ() + 0.5);

        // All orb positions
        Vec3d[] orbPositions = {
                // Cardinals
                new Vec3d(altarPos.getX() + 0.5, altarPos.getY() + 2, altarPos.getZ() - 10.5),
                new Vec3d(altarPos.getX() + 0.5, altarPos.getY() + 2, altarPos.getZ() + 11.5),
                new Vec3d(altarPos.getX() + 11.5, altarPos.getY() + 2, altarPos.getZ() + 0.5),
                new Vec3d(altarPos.getX() - 10.5, altarPos.getY() + 2, altarPos.getZ() + 0.5),
                // Diagonals
                new Vec3d(altarPos.getX() + 11.5, altarPos.getY() + 2, altarPos.getZ() - 7.5),
                new Vec3d(altarPos.getX() - 7.5, altarPos.getY() + 2, altarPos.getZ() - 7.5),
                new Vec3d(altarPos.getX() + 11.5, altarPos.getY() + 2, altarPos.getZ() + 11.5),
                new Vec3d(altarPos.getX() - 7.5, altarPos.getY() + 2, altarPos.getZ() + 11.5)
        };

        for (Vec3d orbPos : orbPositions) {
            Vec3d direction = altarCenter.subtract(orbPos);
            double distance = direction.length();
            direction = direction.normalize();

            // Create beam particles
            for (double d = 0; d < distance; d += 0.3) {
                Vec3d particlePos = orbPos.add(direction.multiply(d));
                world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                        particlePos.x, particlePos.y, particlePos.z, 1, 0.05, 0.05, 0.05, 0.0);

                if (d % 1.2 < 0.3) { // Every few particles
                    world.spawnParticles(ParticleTypes.ENCHANT,
                            particlePos.x, particlePos.y, particlePos.z, 1, 0.1, 0.1, 0.1, 0.02);
                }
            }
        }
    }

    private void endSpawningCutscene(Set<ServerPlayerEntity> cutscenePlayers,
                                     Map<ServerPlayerEntity, GameMode> originalGameModes,
                                     Map<ServerPlayerEntity, Vec3d> originalPositions) {
        // Restore players
        Set<ServerPlayerEntity> playersToRestore = new HashSet<>(cutscenePlayers);

        for (ServerPlayerEntity player : playersToRestore) {
            if (player != null && player.isAlive()) {
                try {
                    GameMode originalMode = originalGameModes.get(player);
                    if (originalMode != null) {
                        player.changeGameMode(originalMode);
                    }

                    Vec3d originalPos = originalPositions.get(player);
                    if (originalPos != null) {
                        player.teleport(player.getServerWorld(),
                                originalPos.x, originalPos.y, originalPos.z,
                                player.getYaw(), player.getPitch());
                    }

                    player.sendMessage(Text.literal("§2§l『 §a§lTHE GUARDIAN AWAKENS §2§l』"), true);
                    player.sendMessage(Text.literal("§6Havenica has been summoned to defend the grove!"), false);
                    player.sendMessage(Text.literal("§c§lThe battle begins!"), false);
                } catch (Exception e) {
                    System.out.println("ERROR: Failed to restore player " + player.getName().getString());
                    e.printStackTrace();
                }
            }
        }

        System.out.println("DEBUG: Cutscene completed successfully!");
    }
}