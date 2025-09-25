package cassetu.mystbornhorizons.command;

import cassetu.mystbornhorizons.world.HavenicaDefeatState;
import cassetu.mystbornhorizons.world.ForestsCurseState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

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
                                                        Text.literal("§4Forest's Curse is ACTIVE. Progress: " + mobsKilled + "/10 infected mobs killed."),
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
        );
    }
}