package cassetu.mystbornhorizons.entity.custom;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BoggedEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import cassetu.mystbornhorizons.entity.ModEntities;
import cassetu.mystbornhorizons.sound.ModSounds;
import cassetu.mystbornhorizons.network.ModPackets;
import cassetu.mystbornhorizons.network.BossMusicPacket;

import java.util.*;
import java.util.stream.Collectors;

public class HavenicaEntity extends HostileEntity {
    private ServerBossBar bossBar;
    private Set<ServerPlayerEntity> playersHearingMusic = new HashSet<>();

    private boolean cutsceneActive = false;
    private int cutsceneTick = 0;
    private Set<ServerPlayerEntity> cutscenePlayers = new HashSet<>();
    private Map<ServerPlayerEntity, GameMode> originalGameModes = new HashMap<>();
    private Map<ServerPlayerEntity, Vec3d> originalPositions = new HashMap<>();

    private int shockwaveCooldown = 0;
    private static final int SHOCKWAVE_COOLDOWN = 500;
    private boolean shockwaveActive = false;
    private int shockwaveTimer = 0;
    private List<Double> waveDistances = new ArrayList<>();
    private List<Integer> waveDelays = new ArrayList<>();
    private static final double WAVE_SPEED = 1.4;
    private static final double MAX_WAVE_DISTANCE = 15.0;

    private int toxicLaserCooldown = 0;
    private int rootNetworkCooldown = 0;
    private int boggedSummonCooldown = 0;
    private boolean isGardensWrathActive = false;
    private static final int LASER_COOLDOWN = 95;
    private static final int ROOT_COOLDOWN = 260;
    private static final int BOGGED_SUMMON_COOLDOWN = 570;

    private Vec3d[] lockedLaserTargets = new Vec3d[3];

    private int teleportCooldown = 0;
    private static final int TELEPORT_COOLDOWN = 240;
    private int teleportDelayTicks = 0;
    private static final int TELEPORT_DELAY = 40;

    private boolean toxicLaserCharging = false;
    private int toxicLaserChargeTicks = 0;
    private PlayerEntity toxicLaserTarget = null;
    private static final int LASER_CHARGE_TIME = 75;

    private int healingTick = 0;
    private static final int HEALING_INTERVAL = 20;
    private static final float HEALING_AMOUNT = 25f;

    private boolean rootNetworkActive = false;
    private int rootNetworkTimer = 0;
    private static final int ROOT_PILLAR_PHASE = 60;
    private static final int ROOT_LINE_PHASE = 220;
    private List<Vec3d> rootPillars = new ArrayList<>();

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public HavenicaEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setCustomNameVisible(true);
        if (!world.isClient()) {
            this.bossBar = new ServerBossBar(
                    this.getDisplayName(),
                    BossBar.Color.GREEN,
                    BossBar.Style.PROGRESS
            );
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        if (this.bossBar != null) {
            this.bossBar.addPlayer(player);
        }
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        if (this.bossBar != null) {
            this.bossBar.removePlayer(player);
        }
        if (playersHearingMusic.contains(player)) {
            ModPackets.sendToPlayer(new BossMusicPacket(false, null), player);
            playersHearingMusic.remove(player);
        }
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        if (this.bossBar != null) {
            this.bossBar.clearPlayers();
        }

        if (!this.getWorld().isClient()) {
            for (ServerPlayerEntity player : playersHearingMusic) {
                ModPackets.sendToPlayer(
                        new BossMusicPacket(false, null),
                        player
                );
            }
            playersHearingMusic.clear();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.getWorld().isClient()) {
            for (ServerPlayerEntity player : playersHearingMusic) {
                ModPackets.sendToPlayer(
                        new BossMusicPacket(false, null),
                        player
                );
            }
            playersHearingMusic.clear();
        }
        super.remove(reason);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1220)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 20)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.5)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 100.0)
                .add(EntityAttributes.GENERIC_ARMOR, 11.0);

    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    private void handleCutscene() {
        if (!cutsceneActive) return;

        cutsceneTick++;

        // Make sure we're on server side
        if (this.getWorld().isClient()) {
            return;
        }

        ServerWorld serverWorld = (ServerWorld) this.getWorld();
        if (serverWorld == null) {
            System.out.println("ERROR: ServerWorld is null!");
            return;
        }

        // Initialize cutscene on first tick
        if (cutsceneTick == 1) {
            initializeCutscene(serverWorld);
        }

        // Update camera positions during cutscene
        updateCutsceneCameras();

        // Debug logging every 20 ticks
        if (cutsceneTick % 20 == 0) {
            System.out.println("DEBUG: Cutscene tick " + cutsceneTick + "/80");
        }

        // Delayed sound effects with null checks
        if (cutsceneTick == 20) {
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                    this.getSoundCategory(), 1.5f, 0.8f);
        }
        if (cutsceneTick == 40) {
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_WARDEN_ROAR,
                    this.getSoundCategory(), 1.0f, 0.7f);
        }

        // Phase 1: Dark energy buildup (0-40 ticks) - Fixed division by zero
        if (cutsceneTick <= 40 && cutsceneTick > 0) {
            // Dark spiraling energy around Havenica
            for (int spiral = 0; spiral < 3; spiral++) {
                double spiralHeight = ((double)cutsceneTick / 40.0) * 4.0;
                double angle = (cutsceneTick * 0.3) + (spiral * Math.PI * 2 / 3);
                double radius = Math.max(0.5, 2.0 - (spiralHeight / 4.0) * 0.5); // Prevent negative radius

                double x = this.getX() + Math.cos(angle) * radius;
                double z = this.getZ() + Math.sin(angle) * radius;

                // Add bounds checking for particle positions
                if (isValidParticlePosition(x, this.getY() + spiralHeight, z)) {
                    serverWorld.spawnParticles(
                            ParticleTypes.WITCH,
                            x, this.getY() + spiralHeight, z,
                            1, 0.1, 0.1, 0.1, 0.0
                    );

                    serverWorld.spawnParticles(
                            ParticleTypes.SMOKE,
                            x, this.getY() + spiralHeight, z,
                            2, 0.2, 0.2, 0.2, 0.05
                    );
                }
            }

            // Ground cracks expanding outward - Fixed potential infinity
            double expansion = Math.min(((double)cutsceneTick / 40.0) * 8.0, 10.0); // Cap at 10 blocks
            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 16) {
                double x = this.getX() + Math.cos(angle) * expansion;
                double z = this.getZ() + Math.sin(angle) * expansion;

                if (isValidParticlePosition(x, this.getY() + 0.1, z)) {
                    serverWorld.spawnParticles(
                            ParticleTypes.EGG_CRACK,
                            x, this.getY() + 0.1, z,
                            3, 0.3, 0.1, 0.3, 0.1
                    );
                }
            }

            // Pulsing dark aura
            if (cutsceneTick % 5 == 0) {
                for (double r = 1; r <= 4; r += 0.5) {
                    for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 8) {
                        double x = this.getX() + Math.cos(angle) * r;
                        double z = this.getZ() + Math.sin(angle) * r;

                        if (isValidParticlePosition(x, this.getY() + 0.5, z)) {
                            serverWorld.spawnParticles(
                                    ParticleTypes.ASH,
                                    x, this.getY() + 0.5, z,
                                    1, 0.1, 0.1, 0.1, 0.0
                            );
                        }
                    }
                }
            }
        }

        // Phase 2: Explosive transformation (at tick 40)
        if (cutsceneTick == 40) {
            // Massive explosion of particles
            serverWorld.spawnParticles(
                    ParticleTypes.EXPLOSION_EMITTER,
                    this.getX(), this.getY() + 2.0, this.getZ(),
                    1, 0.0, 0.0, 0.0, 0.0
            );

            // Ring of energy bursts - Fixed potential performance issue
            int maxRings = 5;
            for (int ring = 0; ring < maxRings; ring++) {
                double radius = (ring + 1) * 2.0;
                int particlesPerRing = Math.min(12, (int)(radius * 2)); // Limit particles based on radius

                for (int p = 0; p < particlesPerRing; p++) {
                    double angle = (p / (double)particlesPerRing) * Math.PI * 2;
                    double x = this.getX() + Math.cos(angle) * radius;
                    double z = this.getZ() + Math.sin(angle) * radius;

                    if (isValidParticlePosition(x, this.getY() + 1.0, z)) {
                        serverWorld.spawnParticles(
                                ParticleTypes.SOUL_FIRE_FLAME,
                                x, this.getY() + 1.0, z,
                                5, 0.3, 0.5, 0.3, 0.1
                        );

                        serverWorld.spawnParticles(
                                ParticleTypes.LARGE_SMOKE,
                                x, this.getY() + 2.0, z,
                                3, 0.5, 0.5, 0.5, 0.1
                        );
                    }
                }
            }

            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE,
                    this.getSoundCategory(), 2.0f, 0.6f);
        }

        // Phase 3: Root emergence and nature awakening (40-80 ticks)
        if (cutsceneTick > 40 && cutsceneTick <= 80) {
            double progress = Math.min((cutsceneTick - 40) / 40.0, 1.0); // Ensure progress doesn't exceed 1
            double maxRadius = progress * 12.0;

            for (int rootLine = 0; rootLine < 8; rootLine++) {
                double angle = rootLine * (Math.PI / 4);

                for (double dist = 0; dist <= maxRadius; dist += 0.8) {
                    if (Math.abs(dist - maxRadius) < 0.8) {
                        double x = this.getX() + Math.cos(angle) * dist;
                        double z = this.getZ() + Math.sin(angle) * dist;

                        if (isValidParticlePosition(x, this.getY() + 0.1, z)) {
                            serverWorld.spawnParticles(
                                    ParticleTypes.MYCELIUM,
                                    x, this.getY() + 0.1, z,
                                    8, 0.5, 0.1, 0.5, 0.1
                            );

                            // Branch particles with bounds checking
                            for (int branch = 0; branch < 3; branch++) {
                                double branchAngle = angle + (branch - 1) * 0.3;
                                double branchX = x + Math.cos(branchAngle) * 0.5;
                                double branchZ = z + Math.sin(branchAngle) * 0.5;

                                if (isValidParticlePosition(branchX, this.getY() + 0.1, branchZ)) {
                                    serverWorld.spawnParticles(
                                            ParticleTypes.COMPOSTER,
                                            branchX, this.getY() + 0.1, branchZ,
                                            3, 0.2, 0.1, 0.2, 0.05
                                    );
                                }
                            }

                            serverWorld.spawnParticles(
                                    ParticleTypes.EGG_CRACK,
                                    x, this.getY() + 0.5, z,
                                    5, 0.3, 0.3, 0.3, 0.2
                            );
                        }
                    }
                }
            }

            // Floating spores - Limited to reasonable range
            int maxSpores = 10;
            for (int i = 0; i < maxSpores; i++) {
                double sporeX = this.getX() + (this.random.nextDouble() - 0.5) * 20;
                double sporeZ = this.getZ() + (this.random.nextDouble() - 0.5) * 20;
                double sporeY = this.getY() + this.random.nextDouble() * 8;

                if (isValidParticlePosition(sporeX, sporeY, sporeZ)) {
                    serverWorld.spawnParticles(
                            ParticleTypes.SPORE_BLOSSOM_AIR,
                            sporeX, sporeY, sporeZ,
                            1, 0.1, 0.1, 0.1, 0.02
                    );
                }
            }
        }

        // Final phase: Completion effects (at tick 80)
        if (cutsceneTick == 80) {
            for (double y = 2; y <= 6; y += 0.3) {
                double radius = Math.max(0.5, 1.5 + Math.sin(y) * 0.5); // Prevent negative radius
                int particleCount = Math.min(32, (int)(radius * 8)); // Cap particle count

                for (int i = 0; i < particleCount; i++) {
                    double angle = (i / (double)particleCount) * Math.PI * 2;
                    double x = this.getX() + Math.cos(angle) * radius;
                    double z = this.getZ() + Math.sin(angle) * radius;

                    if (isValidParticlePosition(x, this.getY() + y, z)) {
                        serverWorld.spawnParticles(
                                ParticleTypes.END_ROD,
                                x, this.getY() + y, z,
                                1, 0.05, 0.05, 0.05, 0.0
                        );

                        if (y > 4) {
                            serverWorld.spawnParticles(
                                    ParticleTypes.ENCHANT,
                                    x, this.getY() + y, z,
                                    2, 0.1, 0.1, 0.1, 0.02
                            );
                        }
                    }
                }
            }

            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                    this.getSoundCategory(), 2.0f, 0.8f);

            endCutscene();
        }
    }

    // Helper method to validate particle positions
    private boolean isValidParticlePosition(double x, double y, double z) {
        // Check for NaN or infinite values
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            return false;
        }

        // Check reasonable bounds (adjust as needed for your world)
        double maxDistance = 100.0; // Maximum distance from entity
        double entityX = this.getX();
        double entityY = this.getY();
        double entityZ = this.getZ();

        double distance = Math.sqrt(
                Math.pow(x - entityX, 2) +
                        Math.pow(y - entityY, 2) +
                        Math.pow(z - entityZ, 2)
        );

        return distance <= maxDistance && y >= -64 && y <= 320; // Minecraft world height bounds
    }

    // Initialize the cutscene - put players in spectator mode and position cameras
    private void initializeCutscene(ServerWorld serverWorld) {
        try {
            Box cutsceneArea = this.getBoundingBox().expand(30.0);
            List<ServerPlayerEntity> nearbyPlayers = serverWorld.getNonSpectatingEntities(PlayerEntity.class, cutsceneArea)
                    .stream()
                    .filter(player -> player instanceof ServerPlayerEntity)
                    .map(player -> (ServerPlayerEntity) player)
                    .filter(Objects::nonNull) // Add null check
                    .collect(Collectors.toList());

            for (ServerPlayerEntity player : nearbyPlayers) {
                try {
                    // Store original state
                    cutscenePlayers.add(player);
                    originalGameModes.put(player, player.interactionManager.getGameMode());
                    originalPositions.put(player, player.getPos());

                    // Set to spectator mode
                    player.changeGameMode(GameMode.SPECTATOR);

                    // Position camera for cinematic view - with safety checks
                    double angle = Math.atan2(player.getZ() - this.getZ(), player.getX() - this.getX());
                    double distance = 12.0;
                    double cameraX = this.getX() + Math.cos(angle) * distance;
                    double cameraY = Math.max(this.getY() + 6.0, -60); // Prevent going below world
                    double cameraZ = this.getZ() + Math.sin(angle) * distance;

                    // Validate teleport position
                    if (Double.isFinite(cameraX) && Double.isFinite(cameraY) && Double.isFinite(cameraZ)) {
                        player.teleport(player.getServerWorld(), cameraX, cameraY, cameraZ, player.getYaw(), player.getPitch());

                        // Make them look at Havenica - with safety checks
                        Vec3d lookTarget = new Vec3d(this.getX(), this.getY() + 2, this.getZ());
                        if (Double.isFinite(lookTarget.x) && Double.isFinite(lookTarget.y) && Double.isFinite(lookTarget.z)) {
                            player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, lookTarget);
                        }
                    }

                    // Send dramatic message
                    player.sendMessage(Text.literal("§c§l⚠ §4§lHAVENICA'S TRANSFORMATION BEGINS §c§l⚠"), false);
                    player.sendMessage(Text.literal("§6§lWitness the awakening of the Garden's Wrath!"), false);
                } catch (Exception e) {
                    System.out.println("ERROR: Failed to initialize cutscene for player " + player.getName().getString() + ": " + e.getMessage());
                    // Remove player from cutscene if initialization failed
                    cutscenePlayers.remove(player);
                    originalGameModes.remove(player);
                    originalPositions.remove(player);
                }
            }

            System.out.println("DEBUG: Cutscene initialized for " + cutscenePlayers.size() + " players");
        } catch (Exception e) {
            System.out.println("ERROR: Failed to initialize cutscene: " + e.getMessage());
            // Cleanup on failure
            cleanupCutscene();
        }
    }

    // Update camera positions during cutscene for cinematic effect
    private void updateCutsceneCameras() {
        if (cutscenePlayers.isEmpty()) return;

        // Create a copy to avoid concurrent modification
        Set<ServerPlayerEntity> playersToUpdate = new HashSet<>(cutscenePlayers);

        for (ServerPlayerEntity player : playersToUpdate) {
            if (player == null || !player.isAlive()) {
                // Remove invalid players
                cutscenePlayers.remove(player);
                originalGameModes.remove(player);
                originalPositions.remove(player);
                continue;
            }

            try {
                Vec3d originalPos = originalPositions.get(player);
                if (originalPos == null) continue;

                // Different camera movements for different phases
                if (cutsceneTick <= 40) {
                    // Phase 1: Slowly orbit around Havenica
                    double baseAngle = Math.atan2(
                            originalPos.z - this.getZ(),
                            originalPos.x - this.getX()
                    );
                    double angle = (cutsceneTick * 0.02) + baseAngle;
                    double distance = Math.max(8.0, 10.0 + Math.sin(cutsceneTick * 0.1) * 2.0);
                    double height = Math.max(this.getY() + 5.0 + Math.sin(cutsceneTick * 0.05) * 1.0, -60);

                    double cameraX = this.getX() + Math.cos(angle) * distance;
                    double cameraZ = this.getZ() + Math.sin(angle) * distance;

                    if (isValidPosition(cameraX, height, cameraZ)) {
                        player.teleport(player.getServerWorld(), cameraX, height, cameraZ, player.getYaw(), player.getPitch());
                        Vec3d lookTarget = new Vec3d(this.getX(), this.getY() + 2, this.getZ());
                        if (isValidPosition(lookTarget.x, lookTarget.y, lookTarget.z)) {
                            player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, lookTarget);
                        }
                    }

                } else if (cutsceneTick == 40) {
                    // Phase 2: Pull back for explosion
                    double angle = Math.atan2(originalPos.z - this.getZ(), originalPos.x - this.getX());
                    double distance = 15.0;
                    double cameraX = this.getX() + Math.cos(angle) * distance;
                    double cameraY = Math.max(this.getY() + 8.0, -60);
                    double cameraZ = this.getZ() + Math.sin(angle) * distance;

                    if (isValidPosition(cameraX, cameraY, cameraZ)) {
                        player.teleport(player.getServerWorld(), cameraX, cameraY, cameraZ, player.getYaw(), player.getPitch());
                        Vec3d lookTarget = new Vec3d(this.getX(), this.getY() + 2, this.getZ());
                        if (isValidPosition(lookTarget.x, lookTarget.y, lookTarget.z)) {
                            player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, lookTarget);
                        }
                    }

                } else if (cutsceneTick > 40) {
                    // Phase 3: High aerial view to see root network
                    double progress = Math.min((cutsceneTick - 40) / 40.0, 1.0);
                    double height = Math.max(this.getY() + 8.0 + progress * 4.0, -60);
                    double angle = Math.atan2(originalPos.z - this.getZ(), originalPos.x - this.getX());
                    double distance = Math.max(8.0, 12.0 + progress * 3.0);

                    double cameraX = this.getX() + Math.cos(angle) * distance;
                    double cameraZ = this.getZ() + Math.sin(angle) * distance;

                    if (isValidPosition(cameraX, height, cameraZ)) {
                        player.teleport(player.getServerWorld(), cameraX, height, cameraZ, player.getYaw(), player.getPitch());
                        Vec3d lookTarget = new Vec3d(this.getX(), this.getY(), this.getZ());
                        if (isValidPosition(lookTarget.x, lookTarget.y, lookTarget.z)) {
                            player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, lookTarget);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("ERROR: Failed to update camera for player " + player.getName().getString() + ": " + e.getMessage());
                // Remove problematic player from cutscene
                cutscenePlayers.remove(player);
                originalGameModes.remove(player);
                originalPositions.remove(player);
            }
        }
    }

    // Helper method to validate positions
    private boolean isValidPosition(double x, double y, double z) {
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)
                && y >= -64 && y <= 320; // Minecraft world height bounds
    }

    // End the cutscene and restore player states
    private void endCutscene() {
        // Create a copy to avoid concurrent modification
        Set<ServerPlayerEntity> playersToRestore = new HashSet<>(cutscenePlayers);

        for (ServerPlayerEntity player : playersToRestore) {
            if (player != null && player.isAlive()) {
                try {
                    // Restore original game mode
                    GameMode originalMode = originalGameModes.get(player);
                    if (originalMode != null) {
                        player.changeGameMode(originalMode);
                    }

                    // Teleport back to original position (slightly adjusted to avoid being inside Havenica)
                    Vec3d originalPos = originalPositions.get(player);
                    if (originalPos != null && isValidPosition(originalPos.x, originalPos.y, originalPos.z)) {
                        // Move them back a bit if they're too close
                        double distanceToHavenica = originalPos.distanceTo(this.getPos());
                        if (distanceToHavenica < 8.0) {
                            Vec3d direction = originalPos.subtract(this.getPos()).normalize();
                            Vec3d safePos = this.getPos().add(direction.multiply(8.0));
                            if (isValidPosition(safePos.x, safePos.y, safePos.z)) {
                                player.teleport(player.getServerWorld(), safePos.x, safePos.y, safePos.z, player.getYaw(), player.getPitch());
                            } else {
                                // Fallback to a safe position near Havenica
                                player.teleport(player.getServerWorld(), this.getX() + 10, this.getY() + 2, this.getZ(), player.getYaw(), player.getPitch());
                            }
                        } else {
                            player.teleport(player.getServerWorld(), originalPos.x, originalPos.y, originalPos.z, player.getYaw(), player.getPitch());
                        }
                    }

                    // Final dramatic messages
                    player.sendMessage(Text.literal("§2§l『 §a§lGARDEN'S WRATH AWAKENED §2§l』"), true);
                    player.sendMessage(Text.literal("§6The ancient forest spirit has been enraged!"), false);
                    player.sendMessage(Text.literal("§c§lPrepare for battle!"), false);
                } catch (Exception e) {
                    System.out.println("ERROR: Failed to restore player " + player.getName().getString() + ": " + e.getMessage());
                    // At least try to restore game mode
                    try {
                        GameMode originalMode = originalGameModes.get(player);
                        if (originalMode != null) {
                            player.changeGameMode(originalMode);
                        }
                    } catch (Exception e2) {
                        System.out.println("ERROR: Failed to restore game mode for " + player.getName().getString());
                    }
                }
            }
        }

        // Clear cutscene data
        cutscenePlayers.clear();
        originalGameModes.clear();
        originalPositions.clear();
        cutsceneActive = false;
        cutsceneTick = 0;

        System.out.println("DEBUG: Cutscene completed and players restored!");
    }

    // Enhanced cleanup method with better error handling
    private void cleanupCutscene() {
        if (!cutscenePlayers.isEmpty()) {
            Set<ServerPlayerEntity> playersToCleanup = new HashSet<>(cutscenePlayers);

            for (ServerPlayerEntity player : playersToCleanup) {
                if (player != null && player.isAlive()) {
                    try {
                        GameMode originalMode = originalGameModes.get(player);
                        if (originalMode != null) {
                            player.changeGameMode(originalMode);
                        }

                        Vec3d originalPos = originalPositions.get(player);
                        if (originalPos != null && isValidPosition(originalPos.x, originalPos.y, originalPos.z)) {
                            player.teleport(player.getServerWorld(), originalPos.x, originalPos.y, originalPos.z, player.getYaw(), player.getPitch());
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR: Failed to cleanup player " + player.getName().getString() + ": " + e.getMessage());
                    }
                }
            }

            cutscenePlayers.clear();
            originalGameModes.clear();
            originalPositions.clear();
        }

        cutsceneActive = false;
        cutsceneTick = 0;
    }

    private void handleBossMusicForPlayers() {
        Set<ServerPlayerEntity> currentNearbyPlayers = new HashSet<>();

        for (ServerPlayerEntity player : ((ServerWorld) this.getWorld()).getPlayers()) {
            if (this.squaredDistanceTo(player) <= 2025) {
                currentNearbyPlayers.add(player);
            }
        }

        for (ServerPlayerEntity player : currentNearbyPlayers) {
            if (!playersHearingMusic.contains(player)) {
                ModPackets.sendToPlayer(
                        new BossMusicPacket(true, ModSounds.HAVENICA_BOSS_MUSIC),
                        player
                );
                playersHearingMusic.add(player);
            }
        }

        for (ServerPlayerEntity player : new HashSet<>(playersHearingMusic)) {
            if (!currentNearbyPlayers.contains(player)) {
                ModPackets.sendToPlayer(
                        new BossMusicPacket(false, null),
                        player
                );
                playersHearingMusic.remove(player);
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        if (!this.getWorld().isClient()) {
            for (ServerPlayerEntity player : playersHearingMusic) {
                ModPackets.sendToPlayer(
                        new BossMusicPacket(false, null),
                        player
                );
            }
            playersHearingMusic.clear();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.bossBar != null) {
            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        }

        // Handle cutscene early in tick - on both client and server for sync
        if (cutsceneActive) {
            handleCutscene();
        }

        if (!this.getWorld().isClient()) {
            if (!this.isDead()) {
                handleBossMusicForPlayers();
            }

            if (this.isOnFire() && !this.hasStatusEffect(StatusEffects.GLOWING)) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 600, 0));
            }

            if (toxicLaserCooldown > 0) toxicLaserCooldown--;
            if (rootNetworkCooldown > 0) rootNetworkCooldown--;
            if (boggedSummonCooldown > 0) boggedSummonCooldown--;
            if (teleportCooldown > 0) teleportCooldown--;
            if (shockwaveCooldown > 0) shockwaveCooldown--;

            if (shockwaveActive) { tickShockwaveBlast(); }

            if (rootNetworkActive) {
                rootNetworkTimer++;

                if (rootNetworkTimer <= ROOT_PILLAR_PHASE) {
                    spawnRootPillarParticles();
                } else if (rootNetworkTimer <= ROOT_LINE_PHASE) {
                    spawnRootLineParticles();
                    damagePlayersInLines();
                } else {
                    rootNetworkActive = false;
                    rootNetworkTimer = 0;
                    rootPillars.clear();
                    rootNetworkCooldown = ROOT_COOLDOWN;
                }
            }

            if (toxicLaserCharging) {
                toxicLaserChargeTicks++;

                if (hasValidLaserTargets()) {
                    createTripleLaserChargingEffect();

                    if (toxicLaserChargeTicks >= LASER_CHARGE_TIME) {
                        fireTripleToxicLaser();
                        toxicLaserCharging = false;
                        toxicLaserChargeTicks = 0;
                        toxicLaserTarget = null;
                        clearLaserTargets();
                    }
                } else {
                    toxicLaserCharging = false;
                    toxicLaserChargeTicks = 0;
                    toxicLaserTarget = null;
                    clearLaserTargets();
                }
            }

            if (teleportDelayTicks > 0) {
                teleportDelayTicks--;
                if (teleportDelayTicks == 0 && teleportCooldown <= 0) {
                    tryTeleportToFroglight();
                }
            }

            healingTick++;
            int healingInterval = this.hasStatusEffect(StatusEffects.GLOWING) ? HEALING_INTERVAL * 8 : HEALING_INTERVAL;

            if (healingTick >= healingInterval) {
                if (this.getHealth() < this.getMaxHealth()) {
                    this.heal(HEALING_AMOUNT);

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.HEART,
                            this.getX(), this.getY() + 1.5, this.getZ(),
                            3,
                            0.3, 0.3, 0.3,
                            0.1
                    );

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            this.getX(), this.getY() + 1.0, this.getZ(),
                            5,
                            0.5, 0.5, 0.5,
                            0.05
                    );
                }
                healingTick = 0;
            }

            // Check if Gardens Wrath should activate
            if (!isGardensWrathActive && this.getHealth() <= this.getMaxHealth() * 0.5f) {
                System.out.println("DEBUG: Health threshold reached (" + this.getHealth() + "/" + this.getMaxHealth() + "), activating Gardens Wrath");
                activateGardensWrath();
            }

            PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 20.0);
            if (nearestPlayer != null) {
                if (toxicLaserCooldown <= 0 && !toxicLaserCharging) useToxicLaser(nearestPlayer);
                if (rootNetworkCooldown <= 0 && !rootNetworkActive) useRootNetwork(nearestPlayer);
                if (!isGardensWrathActive && shockwaveCooldown <= 0) useShockwaveBlast();
                if (isGardensWrathActive && boggedSummonCooldown <= 0) summonBoggedMinions();
            }
        }

        if (this.getWorld().isClient()) {
            this.setupAnimationStates();
        }
    }

    private void summonBoggedMinions() {
        for (int i = 0; i < 2; i++) {
            double angle = i * (Math.PI / 2);
            double distance = 3.0;
            double x = this.getX() + Math.cos(angle) * distance;
            double z = this.getZ() + Math.sin(angle) * distance;

            BoggedEntity bogged = EntityType.BOGGED.create(this.getWorld());
            if (bogged != null) {
                bogged.refreshPositionAndAngles(x, this.getY(), z, 0, 0);

                ItemStack chestplate = new ItemStack(Items.IRON_CHESTPLATE);

                try {
                    RegistryEntry<ArmorTrimMaterial> emeraldMaterial =
                            this.getWorld().getRegistryManager().get(RegistryKeys.TRIM_MATERIAL).getEntry(Identifier.of("minecraft", "emerald")).orElse(null);
                    RegistryEntry<net.minecraft.item.trim.ArmorTrimPattern> eyePattern =
                            this.getWorld().getRegistryManager().get(RegistryKeys.TRIM_PATTERN).getEntry(Identifier.of("minecraft", "eye")).orElse(null);

                    if (emeraldMaterial != null && eyePattern != null) {
                        ArmorTrim trim = new ArmorTrim(emeraldMaterial, eyePattern);
                        chestplate.set(net.minecraft.component.DataComponentTypes.TRIM, trim);
                    }
                } catch (Exception e) {
                }

                bogged.equipStack(net.minecraft.entity.EquipmentSlot.CHEST, chestplate);

                ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);

                try {
                    RegistryEntry<ArmorTrimMaterial> emeraldMaterial =
                            this.getWorld().getRegistryManager().get(RegistryKeys.TRIM_MATERIAL).getEntry(Identifier.of("minecraft", "emerald")).orElse(null);
                    RegistryEntry<net.minecraft.item.trim.ArmorTrimPattern> ribPattern =
                            this.getWorld().getRegistryManager().get(RegistryKeys.TRIM_PATTERN).getEntry(Identifier.of("minecraft", "rib")).orElse(null);

                    if (emeraldMaterial != null && ribPattern != null) {
                        ArmorTrim helmetTrim = new ArmorTrim(emeraldMaterial, ribPattern);
                        helmet.set(net.minecraft.component.DataComponentTypes.TRIM, helmetTrim);
                    }
                } catch (Exception e) {
                }

                bogged.equipStack(net.minecraft.entity.EquipmentSlot.HEAD, helmet);

                ItemStack bow = new ItemStack(Items.BOW);
                bogged.equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, bow);

                this.getWorld().spawnEntity(bogged);

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS,
                        x, this.getY() + 1.0, z,
                        25,
                        0.5, 1.0, 0.5,
                        0.1
                );
            }
        }

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.literal("§2§l⚘ §a§lHavenica §r§2summons §6§lBogged Guardians§r§2! §2⚘"), true)
        );

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_BOGGED_AMBIENT,
                this.getSoundCategory(), 1.0f, 0.8f);

        boggedSummonCooldown = BOGGED_SUMMON_COOLDOWN;
    }

    private void useToxicLaser(PlayerEntity target) {
        if (!hasLineOfSight(target)) {
            return;
        }

        toxicLaserCharging = true;
        toxicLaserChargeTicks = 0;
        toxicLaserTarget = target;

        Vec3d playerPos = new Vec3d(target.getX(), target.getY() + 1.0, target.getZ());
        Vec3d directionToPlayer = playerPos.subtract(this.getX(), this.getY() + 1.5, this.getZ()).normalize();

        Vec3d extendedTarget = playerPos.add(directionToPlayer.multiply(12.0));

        Vec3d perpendicular = new Vec3d(-directionToPlayer.z, 0, directionToPlayer.x).normalize();

        lockedLaserTargets[0] = extendedTarget;
        lockedLaserTargets[1] = extendedTarget.add(perpendicular.multiply(1.2));
        lockedLaserTargets[2] = extendedTarget.subtract(perpendicular.multiply(1.2));

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.toxic_laser_charging"), true)
        );

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT,
                this.getSoundCategory(), 1.0f, 0.5f);
    }

    private boolean hasLineOfSight(PlayerEntity target) {
        Vec3d start = new Vec3d(this.getX(), this.getY() + 1.5, this.getZ());
        Vec3d end = new Vec3d(target.getX(), target.getY() + 1.0, target.getZ());

        return this.getWorld().raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                this
        )).getType() == net.minecraft.util.hit.HitResult.Type.MISS;
    }

    private boolean hasValidLaserTargets() {
        return lockedLaserTargets[0] != null && lockedLaserTargets[1] != null && lockedLaserTargets[2] != null;
    }

    private void clearLaserTargets() {
        lockedLaserTargets[0] = null;
        lockedLaserTargets[1] = null;
        lockedLaserTargets[2] = null;
    }

    private void createTripleLaserChargingEffect() {
        if (!hasValidLaserTargets() || this.getWorld().isClient()) return;

        Vec3d start = new Vec3d(this.getX(), this.getY() + 1.5, this.getZ());
        double intensity = (double) toxicLaserChargeTicks / LASER_CHARGE_TIME;
        int particleCount = (int)(3 + intensity * 7);

        for (int laserIndex = 0; laserIndex < 3; laserIndex++) {
            Vec3d end = lockedLaserTargets[laserIndex];
            Vec3d direction = end.subtract(start);
            double distance = direction.length();
            direction = direction.normalize();

            for (double d = 0; d < distance; d += 0.4) {
                Vec3d particlePos = start.add(direction.multiply(d));

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.COMPOSTER,
                        particlePos.x, particlePos.y + 0.1, particlePos.z,
                        2,
                        0.1, 0.1, 0.1,
                        0.0
                );

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        particlePos.x, particlePos.y + 0.5, particlePos.z,
                        1,
                        0.05, 0.05, 0.05,
                        0.0
                );
            }

            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI/8) {
                double radius = 2.0 - (intensity * 0.5);
                double x = end.x + Math.cos(angle) * radius;
                double z = end.z + Math.sin(angle) * radius;

                ((ServerWorld)this.getWorld()).spawnParticles(
                        intensity > 0.7 ? ParticleTypes.FLAME : ParticleTypes.SMOKE,
                        x, end.y + 0.1, z,
                        1,
                        0.1, 0.1, 0.1,
                        0.0
                );
            }
        }

        if (toxicLaserChargeTicks % 10 == 0) {
            float pitch = 0.5f + (float)intensity * 1.0f;
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_WEEPING_VINES_HIT,
                    this.getSoundCategory(), 0.3f, pitch);
        }
    }

    private void fireTripleToxicLaser() {
        if (!hasValidLaserTargets() || this.getWorld().isClient()) return;

        Vec3d start = new Vec3d(this.getX(), this.getY() + 1.5, this.getZ());

        for (int laserIndex = 0; laserIndex < 3; laserIndex++) {
            Vec3d end = lockedLaserTargets[laserIndex];
            Vec3d direction = end.subtract(start);
            double distance = direction.length();
            direction = direction.normalize();

            for (double d = 0; d < distance; d += 0.2) {
                Vec3d particlePos = start.add(direction.multiply(d));

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.EXPLOSION,
                        particlePos.x, particlePos.y, particlePos.z,
                        3,
                        0.2, 0.2, 0.2,
                        0.1
                );

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.GLOW_SQUID_INK,
                        particlePos.x, particlePos.y, particlePos.z,
                        5,
                        0.3, 0.3, 0.3,
                        0.1
                );

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.COMPOSTER,
                        particlePos.x, particlePos.y, particlePos.z,
                        3,
                        0.2, 0.2, 0.2,
                        0.05
                );
            }

            Box damageBox = new Box(end.x - 2.5, end.y - 1, end.z - 2.5,
                    end.x + 2.5, end.y + 2, end.z + 2.5);

            this.getWorld().getNonSpectatingEntities(PlayerEntity.class, damageBox).forEach(player -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 160, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 1));
                player.damage(this.getDamageSources().magic(), 7.0f);

                for (double angle = 0; angle < Math.PI * 2; angle += Math.PI/6) {
                    double radius = 1.5;
                    double x = player.getX() + Math.cos(angle) * radius;
                    double z = player.getZ() + Math.sin(angle) * radius;

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.FLAME,
                            x, player.getY() + 1.0, z,
                            2,
                            0.1, 0.2, 0.1,
                            0.05
                    );
                }
            });
        }

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                this.getSoundCategory(), 1.0f, 1.5f);
        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE,
                this.getSoundCategory(), 1.0f, 1.5f);

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.toxic_laser_fire"), true)
        );

        toxicLaserCooldown = LASER_COOLDOWN;
        clearLaserTargets();

        if (this.random.nextFloat() < 0.30f && teleportCooldown <= 0) {
            teleportDelayTicks = TELEPORT_DELAY;

            Box teleportMessageRange = this.getBoundingBox().expand(20.0);
            this.getWorld().getNonSpectatingEntities(PlayerEntity.class, teleportMessageRange).forEach(
                    player -> player.sendMessage(Text.literal("Havenica prepares to teleport..."), true)
            );
        }
    }

    private void damagePlayersInLines() {
        if (this.getWorld().isClient()) return;

        for (int i = 0; i < rootPillars.size(); i++) {
            Vec3d start = rootPillars.get(i);
            Vec3d end = rootPillars.get((i + 1) % rootPillars.size());

            Vec3d direction = end.subtract(start);
            double distance = direction.length();
            direction = direction.normalize();

            for (double d = 0; d < distance; d += 0.5) {
                Vec3d checkPos = start.add(direction.multiply(d));

                Box damageBox = new Box(
                        checkPos.x - 1.0, checkPos.y - 0.5, checkPos.z - 1.0,
                        checkPos.x + 1.0, checkPos.y + 2.5, checkPos.z + 1.0
                );

                this.getWorld().getNonSpectatingEntities(PlayerEntity.class, damageBox).forEach(player -> {
                    if (!player.hasStatusEffect(StatusEffects.SLOWNESS) || player.getStatusEffect(StatusEffects.SLOWNESS).getDuration() < 20) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 2));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 80, 1));
                        player.damage(this.getDamageSources().magic(), 6.0f);

                        player.getInventory().armor.forEach(itemStack -> {
                            if (!itemStack.isEmpty() && itemStack.isDamageable()) {
                                int currentDamage = itemStack.getDamage();
                                int maxDamage = itemStack.getMaxDamage();
                                int newDamage = Math.min(maxDamage - 1, currentDamage + 15);
                                itemStack.setDamage(newDamage);
                            }
                        });

                        if (!player.getMainHandStack().isEmpty() && player.getMainHandStack().isDamageable()) {
                            player.getMainHandStack().setDamage(player.getMainHandStack().getDamage() + 5);
                        }

                        for (int p = 0; p < 8; p++) {
                            double angle = (p / 8.0) * Math.PI * 2;
                            double radius = 1.2;
                            double x = player.getX() + Math.cos(angle) * radius;
                            double z = player.getZ() + Math.sin(angle) * radius;

                            ((ServerWorld)this.getWorld()).spawnParticles(
                                    ParticleTypes.CRIT,
                                    x, player.getY() + 1.0, z,
                                    2,
                                    0.1, 0.1, 0.1,
                                    0.05
                            );
                        }
                    }
                });
            }
        }
    }

    private void useShockwaveBlast() {
        shockwaveActive = true;
        shockwaveTimer = 0;
        waveDistances.clear();
        waveDelays.clear();

        waveDistances.add(0.0);
        waveDistances.add(0.0);
        waveDistances.add(0.0);

        waveDelays.add(0);
        waveDelays.add(20);
        waveDelays.add(40);

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.shockwave_blast"), true)
        );

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM,
                this.getSoundCategory(), 1.0f, 0.8f);

        shockwaveCooldown = SHOCKWAVE_COOLDOWN;
    }

    private void tickShockwaveBlast() {
        if (!shockwaveActive || this.getWorld().isClient()) return;

        shockwaveTimer++;

        for (int i = 0; i < waveDistances.size(); i++) {
            if (shockwaveTimer >= waveDelays.get(i)) {
                double currentDistance = waveDistances.get(i);

                if (currentDistance < MAX_WAVE_DISTANCE) {
                    createShockwaveRing(currentDistance);
                    damagePlayersInWave(currentDistance);
                    waveDistances.set(i, currentDistance + WAVE_SPEED);
                }
            }
        }

        boolean allWavesFinished = true;
        for (double distance : waveDistances) {
            if (distance < MAX_WAVE_DISTANCE) {
                allWavesFinished = false;
                break;
            }
        }

        if (allWavesFinished) {
            shockwaveActive = false;
            shockwaveTimer = 0;
            waveDistances.clear();
            waveDelays.clear();
        }
    }

    private void createShockwaveRing(double radius) {
        if (radius <= 0) return;

        double circumference = 2 * Math.PI * radius;
        int particleCount = Math.max(8, (int)(circumference / 0.5));

        for (int i = 0; i < particleCount; i++) {
            double angle = (i / (double)particleCount) * Math.PI * 2;
            double x = this.getX() + Math.cos(angle) * radius;
            double z = this.getZ() + Math.sin(angle) * radius;

            ((ServerWorld)this.getWorld()).spawnParticles(
                    ParticleTypes.BUBBLE_POP,
                    x, this.getY() + 0.1, z,
                    3,
                    0.1, 0.1, 0.1,
                    0.0
            );

            ((ServerWorld)this.getWorld()).spawnParticles(
                    ParticleTypes.CRIT,
                    x, this.getY() + 0.3, z,
                    1,
                    0.05, 0.05, 0.05,
                    0.0
            );
        }

        if (radius % 3.0 < WAVE_SPEED) {
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_STONE_BREAK,
                    this.getSoundCategory(), 0.6f, 0.9f);
        }
    }

    private void damagePlayersInWave(double waveRadius) {
        double waveThickness = 0.8;

        Box searchBox = new Box(
                this.getX() - waveRadius - waveThickness, this.getY() - 0.5, this.getZ() - waveRadius - waveThickness,
                this.getX() + waveRadius + waveThickness, this.getY() + 1.5, this.getZ() + waveRadius + waveThickness
        );

        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, searchBox).forEach(player -> {
            double distanceToPlayer = Math.sqrt(
                    Math.pow(player.getX() - this.getX(), 2) +
                            Math.pow(player.getZ() - this.getZ(), 2)
            );

            if (Math.abs(distanceToPlayer - waveRadius) <= waveThickness &&
                    player.getY() <= this.getY() + 1.2) {

                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
                player.damage(this.getDamageSources().magic(), 6.0f);

                Vec3d knockbackDir = new Vec3d(
                        player.getX() - this.getX(),
                        0.3,
                        player.getZ() - this.getZ()
                ).normalize().multiply(1.2);

                player.setVelocity(player.getVelocity().add(knockbackDir));

                for (int p = 0; p < 8; p++) {
                    double angle = (p / 8.0) * Math.PI * 2;
                    double particleRadius = 1.0;
                    double x = player.getX() + Math.cos(angle) * particleRadius;
                    double z = player.getZ() + Math.sin(angle) * particleRadius;

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.DAMAGE_INDICATOR,
                            x, player.getY() + 1.0, z,
                            1,
                            0.1, 0.1, 0.1,
                            0.05
                    );
                }
            }
        });
    }

    private void activateGardensWrath() {
        if (this.getWorld().isClient()) return;

        System.out.println("DEBUG: Gardens Wrath activated!");

        // Dramatic pause - freeze all nearby entities briefly
        Box freezeArea = this.getBoundingBox().expand(25.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, freezeArea).forEach(player -> {
            // Give players brief slowness to create dramatic pause
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 4, true, false));
            // Dramatic message
            player.sendMessage(Text.literal("§c§l⚠ §4§lHAVENICA'S RAGE AWAKENS §c§l⚠"), true);
        });

        // Play dramatic transformation sounds
        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL,
                this.getSoundCategory(), 2.0f, 0.5f);

        // Start the cutscene timing system
        this.cutsceneActive = true;
        this.cutsceneTick = 0;

        System.out.println("DEBUG: Cutscene started! cutsceneActive=" + cutsceneActive);

        // Set the flag immediately so other systems know we're in wrath mode
        isGardensWrathActive = true;
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return !effect.getEffectType().equals(StatusEffects.POISON) && super.canHaveStatusEffect(effect);
    }

    private void tryTeleportToFroglight() {
        if (this.getWorld().isClient()) return;

        List<BlockPos> validFroglights = new ArrayList<>();
        BlockPos currentPos = this.getBlockPos();

        for (int x = -15; x <= 15; x++) {
            for (int y = -15; y <= 15; y++) {
                for (int z = -15; z <= 15; z++) {
                    BlockPos checkPos = currentPos.add(x, y, z);
                    Block block = this.getWorld().getBlockState(checkPos).getBlock();

                    if (block == Blocks.OCHRE_FROGLIGHT ||
                            block == Blocks.VERDANT_FROGLIGHT ||
                            block == Blocks.PEARLESCENT_FROGLIGHT) {

                        BlockPos above1 = checkPos.up();
                        BlockPos above2 = checkPos.up(2);

                        if (this.getWorld().getBlockState(above1).isAir() &&
                                this.getWorld().getBlockState(above2).isAir()) {
                            validFroglights.add(above1);
                        }
                    }
                }
            }
        }

        if (!validFroglights.isEmpty()) {
            BlockPos teleportPos = validFroglights.get(this.random.nextInt(validFroglights.size()));

            ((ServerWorld)this.getWorld()).spawnParticles(
                    ParticleTypes.PORTAL,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    20,
                    0.5, 1.0, 0.5,
                    0.1
            );

            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                    this.getSoundCategory(), 1.0f, 1.0f);

            this.refreshPositionAndAngles(teleportPos.getX() + 0.5, teleportPos.getY(), teleportPos.getZ() + 0.5, this.getYaw(), this.getPitch());

            ((ServerWorld)this.getWorld()).spawnParticles(
                    ParticleTypes.PORTAL,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    20,
                    0.5, 1.0, 0.5,
                    0.1
            );

            Box messageRange = this.getBoundingBox().expand(20.0);
            this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                    player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.teleport"), true)
            );

            teleportCooldown = TELEPORT_COOLDOWN;
        } else {
            Box messageRange = this.getBoundingBox().expand(20.0);
            this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                    player -> player.sendMessage(Text.literal("Havenica searches for a froglight to teleport to..."), true)
            );
        }
    }

    private void useRootNetwork(PlayerEntity target) {
        rootNetworkActive = true;
        rootNetworkTimer = 0;
        rootPillars.clear();

        double centerX = target.getX();
        double centerZ = target.getZ();

        rootPillars.add(new Vec3d(centerX, target.getY(), centerZ));

        int pillarCount = isGardensWrathActive ? 8 : 6;
        for (int i = 0; i < pillarCount; i++) {
            double angle = (i / (double)pillarCount) * Math.PI * 2;
            double radius = 8.0 + this.random.nextDouble() * 4.0;
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            rootPillars.add(new Vec3d(x, target.getY(), z));
        }

        Box messageRange = this.getBoundingBox().expand(20.0);
        this.getWorld().getNonSpectatingEntities(PlayerEntity.class, messageRange).forEach(
                player -> player.sendMessage(Text.translatable("entity.mystbornhorizons.havenica.root_network"), true)
        );

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_ROOTED_DIRT_BREAK,
                this.getSoundCategory(), 1.0f, 0.7f);
    }

    private void spawnRootPillarParticles() {
        if (this.getWorld().isClient()) return;

        for (Vec3d pillar : rootPillars) {
            for (double y = 0; y < 5; y += 0.3) {
                double radius = 0.8;
                int particlesPerRing = 8;

                for (int p = 0; p < particlesPerRing; p++) {
                    double angle = (p / (double)particlesPerRing) * Math.PI * 2;
                    double x = pillar.x + Math.cos(angle) * radius;
                    double z = pillar.z + Math.sin(angle) * radius;

                    ((ServerWorld)this.getWorld()).spawnParticles(
                            ParticleTypes.ITEM_COBWEB,
                            x, pillar.y + y, z,
                            1,
                            0.1, 0.1, 0.1,
                            0.0
                    );

                    if (y > 3) {
                        ((ServerWorld)this.getWorld()).spawnParticles(
                                ParticleTypes.END_ROD,
                                x, pillar.y + y, z,
                                1,
                                0.05, 0.05, 0.05,
                                0.0
                        );
                    }
                }
            }
        }

        if (rootNetworkTimer == ROOT_PILLAR_PHASE - 10) {
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_ROOTED_DIRT_PLACE,
                    this.getSoundCategory(), 1.0f, 1.0f);
        }
    }

    private void spawnRootLineParticles() {
        if (this.getWorld().isClient()) return;

        for (int i = 0; i < rootPillars.size(); i++) {
            Vec3d start = rootPillars.get(i);
            Vec3d end = rootPillars.get((i + 1) % rootPillars.size());

            Vec3d direction = end.subtract(start);
            double distance = direction.length();
            direction = direction.normalize();

            for (double d = 0; d < distance; d += 0.3) {
                Vec3d particlePos = start.add(direction.multiply(d));

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.CRIT,
                        particlePos.x, particlePos.y, particlePos.z,
                        2,
                        0.1, 0.1, 0.1,
                        0.0
                );

                ((ServerWorld)this.getWorld()).spawnParticles(
                        ParticleTypes.SCRAPE,
                        particlePos.x, particlePos.y + 0.5, particlePos.z,
                        1,
                        0.05, 0.05, 0.05,
                        0.0
                );
            }
        }
    }

    // Debug method for testing - remove in production
    public void debugTriggerCutscene() {
        if (!this.getWorld().isClient()) {
            System.out.println("DEBUG: Manually triggering cutscene");
            this.activateGardensWrath();
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ITEM_BONE_MEAL_USE;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_CHORUS_FLOWER_DEATH;
    }
}