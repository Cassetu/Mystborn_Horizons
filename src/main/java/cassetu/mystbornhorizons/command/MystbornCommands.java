package cassetu.mystbornhorizons.command;

import cassetu.mystbornhorizons.util.EnhancedMobEquipment;
import cassetu.mystbornhorizons.world.HavenicaDefeatState;
import cassetu.mystbornhorizons.world.ForestsCurseState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import java.util.List;
import java.util.Optional;

public class MystbornCommands {

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            register(dispatcher, registryAccess, environment);
        });
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("mystborn")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("havenica")
                        .then(CommandManager.literal("status")
                                .executes(context -> {
                                    ServerWorld world = context.getSource().getWorld();
                                    HavenicaDefeatState state = HavenicaDefeatState.getOrCreate(world);

                                    if (state.isHavenicaDefeated()) {
                                        long defeatTime = state.getDefeatTime();
                                        long currentTime = world.getTime();
                                        long timeSinceDefeat = currentTime - defeatTime;
                                        long daysPassed = timeSinceDefeat / 24000;

                                        context.getSource().sendFeedback(() ->
                                                        Text.literal("§cHavenica was defeated " + daysPassed + " days ago. Enhanced mob spawning is ACTIVE."),
                                                false);
                                    } else {
                                        context.getSource().sendFeedback(() ->
                                                        Text.literal("§aHavenica has not been defeated. Normal mob spawning."),
                                                false);
                                    }
                                    return 1;
                                }))
                        .then(CommandManager.literal("reset")
                                .executes(context -> {
                                    ServerWorld world = context.getSource().getWorld();
                                    world.getPersistentStateManager().set("havenica_defeat_state", new HavenicaDefeatState());

                                    context.getSource().sendFeedback(() ->
                                                    Text.literal("§aHavenica defeat status has been reset. Mob spawning returned to normal."),
                                            true);
                                    return 1;
                                }))
                        .then(CommandManager.literal("setdefeated")
                                .then(CommandManager.argument("defeated", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean defeated = BoolArgumentType.getBool(context, "defeated");
                                            ServerWorld world = context.getSource().getWorld();
                                            HavenicaDefeatState state = HavenicaDefeatState.getOrCreate(world);

                                            if (defeated) {
                                                state.setHavenicaDefeated(world);
                                                context.getSource().sendFeedback(() ->
                                                                Text.literal("§cHavenica marked as defeated. Enhanced mob spawning enabled."),
                                                        true);
                                            } else {
                                                world.getPersistentStateManager().set("havenica_defeat_state", new HavenicaDefeatState());
                                                context.getSource().sendFeedback(() ->
                                                                Text.literal("§aHavenica marked as not defeated. Normal mob spawning."),
                                                        true);
                                            }
                                            return 1;
                                        }))))
                .then(CommandManager.literal("curse")
                        .then(CommandManager.literal("status")
                                .executes(context -> {
                                    ServerWorld world = context.getSource().getWorld();
                                    ForestsCurseState curseState = ForestsCurseState.getOrCreate(world);

                                    if (curseState.isCurseActive()) {
                                        int mobsKilled = curseState.getMobsKilled();
                                        context.getSource().sendFeedback(() ->
                                                        Text.literal("§4Forest's Curse is ACTIVE. Progress: " + mobsKilled + "/10 cursed mobs killed."),
                                                false);
                                    } else {
                                        context.getSource().sendFeedback(() ->
                                                        Text.literal("§aForest's Curse is not active."),
                                                false);
                                    }
                                    return 1;
                                }))
                        .then(CommandManager.literal("start")
                                .executes(context -> {
                                    ServerWorld world = context.getSource().getWorld();
                                    ForestsCurseState curseState = ForestsCurseState.getOrCreate(world);

                                    if (!curseState.isCurseActive()) {
                                        curseState.activateCurse(world);
                                        context.getSource().sendFeedback(() ->
                                                        Text.literal("§4§lThe Forest's Curse has been activated!"),
                                                true);
                                    } else {
                                        context.getSource().sendFeedback(() ->
                                                        Text.literal("§cThe Forest's Curse is already active."),
                                                false);
                                    }
                                    return 1;
                                }))
                        .then(CommandManager.literal("end")
                                .executes(context -> {
                                    ServerWorld world = context.getSource().getWorld();
                                    ForestsCurseState curseState = ForestsCurseState.getOrCreate(world);

                                    if (curseState.isCurseActive()) {
                                        curseState.endCurse(world);
                                        context.getSource().sendFeedback(() ->
                                                        Text.literal("§2§lThe Forest's Curse has been lifted!"),
                                                true);
                                    } else {
                                        context.getSource().sendFeedback(() ->
                                                        Text.literal("§aThe Forest's Curse is not currently active."),
                                                false);
                                    }
                                    return 1;
                                })))
                .then(CommandManager.literal("lore")
                        .then(CommandManager.literal("book")
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    if (source.getEntity() instanceof ServerPlayerEntity player) {
                                        giveLoreBook(player);
                                        source.sendFeedback(() ->
                                                        Text.literal("§2The Chronicles of Havenica has been given to you."),
                                                false);
                                    } else {
                                        source.sendFeedback(() ->
                                                        Text.literal("§cThis command can only be used by players."),
                                                false);
                                    }
                                    return 1;
                                })))
                .then(CommandManager.literal("summon")
                        .then(CommandManager.literal("infected")
                                .executes(context -> {
                                    ServerWorld world = context.getSource().getWorld();
                                    BlockPos pos = BlockPos.ofFloored(context.getSource().getPosition());

                                    EntityType<?>[] mobTypes = {
                                            EntityType.ZOMBIE, EntityType.WITCH, EntityType.BOGGED,
                                            EntityType.SPIDER, EntityType.ENDERMAN, EntityType.VINDICATOR,
                                            EntityType.SKELETON
                                    };

                                    EntityType<?> chosenType = mobTypes[world.getRandom().nextInt(mobTypes.length)];

                                    if (chosenType.create(world) instanceof HostileEntity mob) {
                                        mob.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                                        mob.initialize(world, world.getLocalDifficulty(pos), SpawnReason.COMMAND, null);
                                        HavenicaDefeatState state = HavenicaDefeatState.getOrCreate(world);

                                        state.setHavenicaDefeated(world);
                                        EnhancedMobEquipment.equipPostHavenicaMob(mob, world);
                                        EnhancedMobEquipment.applyCorruptionEffects(mob, world);

                                        world.spawnParticles(
                                                net.minecraft.particle.ParticleTypes.SPORE_BLOSSOM_AIR,
                                                mob.getX(), mob.getY() + 1, mob.getZ(),
                                                10,
                                                1.0, 1.0, 1.0,
                                                0.1
                                        );

                                        world.spawnEntity(mob);

                                        context.getSource().sendFeedback(() ->
                                                        Text.literal("§2Infected " + mob.getType().getName().getString() + " summoned!"),
                                                false);
                                    }

                                    return 1;
                                }))
                        .then(CommandManager.literal("cursed")
                                .executes(context -> {
                                    ServerWorld world = context.getSource().getWorld();
                                    BlockPos pos = BlockPos.ofFloored(context.getSource().getPosition());

                                    EntityType<?>[] mobTypes = {
                                            EntityType.ZOMBIE, EntityType.WITCH, EntityType.BOGGED,
                                            EntityType.SPIDER, EntityType.ENDERMAN, EntityType.VINDICATOR,
                                            EntityType.SKELETON
                                    };

                                    EntityType<?> chosenType = mobTypes[world.getRandom().nextInt(mobTypes.length)];

                                    if (chosenType.create(world) instanceof HostileEntity mob) {
                                        mob.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                                        mob.initialize(world, world.getLocalDifficulty(pos), SpawnReason.COMMAND, null);

                                        EnhancedMobEquipment.equipCursedMob(mob, world);
                                        EnhancedMobEquipment.applyCurseEffects(mob, world);

                                        world.spawnParticles(
                                                net.minecraft.particle.ParticleTypes.SOUL_FIRE_FLAME,
                                                mob.getX(), mob.getY() + 1, mob.getZ(),
                                                10,
                                                1.0, 1.0, 1.0,
                                                0.1
                                        );

                                        world.spawnEntity(mob);

                                        context.getSource().sendFeedback(() ->
                                                        Text.literal("§4Cursed " + mob.getType().getName().getString() + " summoned!"),
                                                false);
                                    }

                                    return 1;
                                })))
        );
    }

    private static void giveLoreBook(ServerPlayerEntity player) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

        String page1 = "§0§lThe Chronicles of Havenica§r\n\n" +
                "§8The Rise and Fall of the Forest Guardian§r\n\n" +
                "§7This tome contains the tragic tale of Havenica, the ancient guardian whose corruption brought darkness to the world.";

        String page2 = "§0§lChapter 1: The Ancient Grove§r\n\n" +
                "§8In the primordial age, there existed a sacred grove at the heart of an endless forest. This nexus of life force was where Havenica first awakened...";

        String page3 = "§0§lThe Guardian's Birth§r\n\n" +
                "§8Born from the collective will of the forest itself, Havenica emerged as the ultimate guardian—a sentient embodiment of nature's protective instincts.";

        String page4 = "§0§lChapter 2: The Golden Age§r\n\n" +
                "§8Under Havenica's protection, the forest flourished. The Haven Cores emerged—eight crystalline entities that extended the guardian's influence.";

        String page5 = "§0§lThe Haven Core Network§r\n\n" +
                "§8Four primary cores at cardinal points, four secondary at diagonals. They channeled Havenica's will while feeding energy back to their creator.";

        String page6 = "§0§lThe Verdant Circle§r\n\n" +
                "§8Wise humans learned to coexist with Havenica's domain. They crafted the Forest Heart as both offering and key to commune with the guardian.";

        String page7 = "§0§lChapter 3: The Corruption§r\n\n" +
                "§8Industrial expansion and dark rituals began to poison the forest. The corruption was insidious, working slowly to undermine harmony.";

        String page8 = "§0§lThe Creeping Darkness§r\n\n" +
                "§8Each cleansing left residue. The corruption whispered to wildlife, tainted soil, and most dangerously, began affecting Havenica itself.";

        String page9 = "§0§lSigns of Change§r\n\n" +
                "§8Havenica became more aggressive. The Haven Cores pulsed with angry red light. The Forest Heart produced unpredictable results.";

        String page10 = "§0§lChapter 4: The Fall§r\n\n" +
                "§8During 'The Night of Screaming Wood,' Havenica's protective rage transformed into an overwhelming compulsion to destroy.";

        String page11 = "§0§lThe Transformed Guardian§r\n\n" +
                "§8Havenica's bark turned sickly black-green with pulsing red veins. Its eyes burned with cold, hungry fire that consumed rather than illuminated.";

        String page12 = "§0§lNew Abilities§r\n\n" +
                "§8Toxic Laser: poisonous beams. Root Network: life-draining patterns. Shockwave Blast: devastating area attacks. Gardens Wrath: reality-bending fury.";

        String page13 = "§0§lThe Haven Cores' Corruption§r\n\n" +
                "§8The cores' green light turned sickly red. Instead of healing, they spread corruption. They gained malevolent intelligence.";

        String page14 = "§0§lThe Final Ritual§r\n\n" +
                "§8The Verdant Circle attempted one last cleansing ritual, but Havenica's corruption was beyond redemption. The guardian destroyed its former allies.";

        String page15 = "§0§lChapter 5: The Forest's Curse§r\n\n" +
                "§8When Havenica falls in battle, its accumulated corruption explodes outward, fundamentally altering the world's nature.";

        String page16 = "§0§lCassetu's Voice§r\n\n" +
                "§8The curse learned to mimic Cassetu, a Verdant Circle member, delivering twisted messages: 'Something ancient stirs beneath our feet...'";

        String page17 = "§0§lThe Whispers§r\n\n" +
                "§8'We've poisoned this place just by being here.' 'The earth itself rejects us now.' 'The world grows darker with each breath...'";

        String page18 = "§0§lManifestation§r\n\n" +
                "§8'Can you feel it spreading? The taint seeps into everything.' The curse reveals its truth: 'The curse feeds on our fear.'";

        String page19 = "§0§lMemory and Madness§r\n\n" +
                "§8'The forest remembers what we've done.' 'It knows we're here.' 'Do you hear them too? The voices in the wind?'";

        String page20 = "§0§lThe Curse's Effects§r\n\n" +
                "§8Eternal twilight. Chaotic weather. Creatures become 'cursed' with enhanced bodies but twisted minds, echoing Havenica's fall.";

        String page21 = "§0§lCursed Essence§r\n\n" +
                "§8Cursed creatures drop fragments of Havenica's tainted life force. Each essence cleansed brings the world closer to lifting the burden.";

        String page22 = "§0§lPsychological Warfare§r\n\n" +
                "§8The curse clouds vision with dirt and corruption, representing Havenica's tainted perspective. Spores carry spiritual contamination.";

        String page23 = "§0§lDistorted Purpose§r\n\n" +
                "§8The curse doesn't seek random destruction. It follows patterns reflecting Havenica's desperate attempts to restore 'natural order.'";

        String page24 = "§0§lThe Tragic Guardian§r\n\n" +
                "§8Even corrupted, traces of purpose remain. The curse targets disrupted balance between civilization and wilderness.";

        String page25 = "§0§lA Warning§r\n\n" +
                "§8Havenica's tale warns of the balance between protection and obsession, between love and possession.";

        String page26 = "§0§lThe Scars Remain§r\n\n" +
                "§8Even when cleansed, the world bears permanent scars—a reminder of what was lost when the great guardian fell to corruption.";

        String page27 = "§0§lWhat Might Have Been§r\n\n" +
                "§8The tragedy serves as a monument to what the world might have been if that ancient harmony had been allowed to continue.";

        String page28 = "§0§lEpilogue§r\n\n" +
                "§8Only through great sacrifice can the world cleanse Havenica's legacy, but the guardian's corruption will echo through the ages.";

        List<RawFilteredPair<Text>> pages = List.of(
                new RawFilteredPair<>(Text.literal(page1), Optional.of(Text.literal(page1))),
                new RawFilteredPair<>(Text.literal(page2), Optional.of(Text.literal(page2))),
                new RawFilteredPair<>(Text.literal(page3), Optional.of(Text.literal(page3))),
                new RawFilteredPair<>(Text.literal(page4), Optional.of(Text.literal(page4))),
                new RawFilteredPair<>(Text.literal(page5), Optional.of(Text.literal(page5))),
                new RawFilteredPair<>(Text.literal(page6), Optional.of(Text.literal(page6))),
                new RawFilteredPair<>(Text.literal(page7), Optional.of(Text.literal(page7))),
                new RawFilteredPair<>(Text.literal(page8), Optional.of(Text.literal(page8))),
                new RawFilteredPair<>(Text.literal(page9), Optional.of(Text.literal(page9))),
                new RawFilteredPair<>(Text.literal(page10), Optional.of(Text.literal(page10))),
                new RawFilteredPair<>(Text.literal(page11), Optional.of(Text.literal(page11))),
                new RawFilteredPair<>(Text.literal(page12), Optional.of(Text.literal(page12))),
                new RawFilteredPair<>(Text.literal(page13), Optional.of(Text.literal(page13))),
                new RawFilteredPair<>(Text.literal(page14), Optional.of(Text.literal(page14))),
                new RawFilteredPair<>(Text.literal(page15), Optional.of(Text.literal(page15))),
                new RawFilteredPair<>(Text.literal(page16), Optional.of(Text.literal(page16))),
                new RawFilteredPair<>(Text.literal(page17), Optional.of(Text.literal(page17))),
                new RawFilteredPair<>(Text.literal(page18), Optional.of(Text.literal(page18))),
                new RawFilteredPair<>(Text.literal(page19), Optional.of(Text.literal(page19))),
                new RawFilteredPair<>(Text.literal(page20), Optional.of(Text.literal(page20))),
                new RawFilteredPair<>(Text.literal(page21), Optional.of(Text.literal(page21))),
                new RawFilteredPair<>(Text.literal(page22), Optional.of(Text.literal(page22))),
                new RawFilteredPair<>(Text.literal(page23), Optional.of(Text.literal(page23))),
                new RawFilteredPair<>(Text.literal(page24), Optional.of(Text.literal(page24))),
                new RawFilteredPair<>(Text.literal(page25), Optional.of(Text.literal(page25))),
                new RawFilteredPair<>(Text.literal(page26), Optional.of(Text.literal(page26))),
                new RawFilteredPair<>(Text.literal(page27), Optional.of(Text.literal(page27))),
                new RawFilteredPair<>(Text.literal(page28), Optional.of(Text.literal(page28)))
        );

        WrittenBookContentComponent bookContent = new WrittenBookContentComponent(
                new RawFilteredPair<>("§4The Chronicles of Havenica§r", Optional.of("§4The Chronicles of Havenica§r")),
                "§8Ancient Scribe§r",
                0,
                pages,
                true
        );

        book.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, bookContent);

        if (player.getInventory().getEmptySlot() != -1) {
            player.getInventory().insertStack(book);
        } else {
            player.dropItem(book, false);
        }
    }
}