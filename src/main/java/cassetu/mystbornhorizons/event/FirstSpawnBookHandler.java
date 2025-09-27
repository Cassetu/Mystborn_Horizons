package cassetu.mystbornhorizons.event;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;

public class FirstSpawnBookHandler {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            if (isFirstTimePlayer(player)) {
                giveWelcomeBook(player);
                markPlayerAsJoined(player);
            }
        });
    }

    private static boolean isFirstTimePlayer(ServerPlayerEntity player) {
        return player.getStatHandler().getStat(net.minecraft.stat.Stats.CUSTOM.getOrCreateStat(net.minecraft.stat.Stats.LEAVE_GAME)) == 0;
    }

    private static void markPlayerAsJoined(ServerPlayerEntity player) {
    }

    private static void giveWelcomeBook(ServerPlayerEntity player) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

        String page1 = "§0§lWelcome to Mystborn Horizons§r\n\n" +
                "You have awakened in a world forever changed by ancient magics.\n\n" +
                "§4Beware the Forest's Curse§r - when darkness falls, infected creatures roam seeking to spread corruption.\n\n" +
                "Only by slaying the infected can the curse be lifted.";

        String page2 = "§0§lThe Ancient Prophecy§r\n\n" +
                "When §4Havenica the Destroyer§r falls, reality will tear.\n\n" +
                "Mobs will grow stronger, wearing enchanted armor and wielding cursed weapons.\n\n" +
                "The infected will be marked by §2emerald trimmed armor§r and otherworldly glow.";

        String page3 = "§0§lOres§r\n\n" +
                "§8Froststone§r - Cold biomes\n" +
                "§8Stormite§r - Nether\n" +
                "§4Salondite§r - Badlands (rare)\n" +
                "§8Tectonite Geode§r - Deep underground\n" +
                "§8Moonstone§r - Deepslate\n" +
                "§8Luminite§r - End dimension\n" +
                "§5Mystborn Dust§r - Rarest material, glows";

        String page4 = "§0§lWeapons§r\n\n" +
                "§8Froststone Tools§r - Basic tier\n" +
                "§8Moonstone Rapier§r - Fast attacks\n" +
                "§8Royal Froststone Sword§r - Enhanced with gold\n" +
                "§4Royal Tectonite Sword§r - Very powerful\n" +
                "§8Peacekeeper Sword§r - Combines both royal swords\n" +
                "§4Vanquisher Sword§r - Ultimate weapon";

        String page5 = "§0§lArmor & Tools§r\n\n" +
                "§8Froststone Armor Set§r:\n" +
                "- Helmet (75 durability)\n" +
                "- Chestplate (75 durability)\n" +
                "- Leggings (75 durability)\n" +
                "- Boots (75 durability)\n\n" +
                "§4Tomahawk§r - Throwing weapon, returns to you";

        String page6 = "§0§lBuilding Blocks§r\n\n" +
                "§8Froststone Block§r - Made from 9 froststone\n" +
                "§8Myst Bricks§r - Deepslate + mystborn dust\n" +
                "§8Molten Gold Blackstone§r - Decorative\n" +
                "§8Molten Gold Basalt§r - Decorative\n" +
                "§8Packed Ice Bricks§r - Never melts";

        String page7 = "§0§lSpecial Blocks§r\n\n" +
                "§2Dungeon Rootmass§r - Very strong, drops 3-5 roots\n\n" +
                "§8Dungeon Cobble§r - Unbreakable\n\n" +
                "§8Shard Block§r - Damages when stepped on, creates bubble columns\n\n" +
                "§2Ancient Grove Altar§r - Ritual site for Forest Heart";

        String page8 = "§0§lFood Items§r\n\n" +
                "§8Cauliflower§r - Mantis food\n" +
                "§4Power Core§r - Temporary strength\n" +
                "§2Honey Berries§r - From bushes\n" +
                "§8Root§r - Basic food\n" +
                "§8Salmon/Cod Nigiri§r\n" +
                "§8Waffle§r\n" +
                "§8Chicken Nuggets§r\n" +
                "§8Veggie Sandwich§r\n\n" +
                "§2Forest Heart§r - Key item";

        String page9 = "§0§lMantis§r\n\n" +
                "Peaceful creature\n" +
                "Health: 18 HP\n" +
                "Speed: 0.35\n" +
                "Loves cauliflower\n" +
                "Can be bred\n" +
                "Spawns: plains, cherry groves, flower forests\n" +
                "Makes parrot sounds";

        String page10 = "§0§lCopper Bulb§r\n\n" +
                "Hostile cave dweller\n" +
                "Health: 60 HP\n" +
                "Damage: 2.5\n" +
                "Speed: 0.24\n" +
                "Attack Speed: 20\n" +
                "Immune to poison\n" +
                "Spawns: lush caves, dripstone caves";

        String page11 = "§0§lIce Spider§r\n\n" +
                "Cold biome predator\n" +
                "Health: 20 HP\n" +
                "Damage: 2\n" +
                "Speed: 0.28\n" +
                "Attack Speed: 22\n" +
                "Immune to poison\n" +
                "Spawns: cold biomes\n" +
                "Makes spider sounds";

        String page12 = "§0§lBasalt Howler§r\n\n" +
                "Extremely dangerous\n" +
                "Health: 90 HP\n" +
                "Damage: 2.5\n" +
                "Speed: 0.28\n" +
                "Attack Speed: 34\n" +
                "Fire immune\n" +
                "Poison immune\n" +
                "Special attack: 3 block range\n" +
                "Makes blaze sounds";

        String page13 = "§0§lHavenica Boss§r\n\n" +
                "§2Guardian of the Grove§r\n" +
                "Health: 1220 HP (scales with players)\n" +
                "Cannot be moved\n" +
                "Fire immune\n" +
                "Has boss bar\n" +
                "Heals every 20 ticks\n\n" +
                "§4WARNING§r: Defeating activates world curse!";

        String page14 = "§0§lHavenica Attacks§r\n\n" +
                "§4Toxic Laser§r - Charges 60 ticks, fires poison beams\n\n" +
                "§2Root Network§r - Root pillars then damage lines\n\n" +
                "§8Shockwave Blast§r - Ground waves, damage and slow\n\n" +
                "§8Bogged Summoning§r - Skeleton archers with emerald armor";

        String page15 = "§0§lHavenica Phase 2§r\n\n" +
                "At 50% health:\n" +
                "§2Garden's Wrath§r activates\n" +
                "All attacks more frequent\n" +
                "Can teleport to Haven Cores\n" +
                "Summons more minions\n" +
                "Becomes much deadlier\n\n" +
                "Defeat triggers permanent curse";

        String page16 = "§0§lHaven Core§r\n\n" +
                "Support entity\n" +
                "Health: 20 + 30 per player\n" +
                "Cannot move\n" +
                "Fire immune\n" +
                "Heals Havenica\n" +
                "Explodes when killed\n" +
                "Drops: Forest Haven Disc Fragment\n" +
                "Appears during summoning";

        String page17 = "§0§lMagical Effects§r\n\n" +
                "§2Slimey Effect§r:\n" +
                "Climb walls when touching\n" +
                "Speed reduces over time\n\n" +
                "§5Spore Vision Effect§r:\n" +
                "Dense particle clouds\n" +
                "Causes nausea, blindness\n" +
                "Shows phantom creatures\n" +
                "Higher levels: darkness, slowness\n" +
                "Very dangerous";

        String page18 = "§0§lForest's Curse§r\n\n" +
                "When active:\n" +
                "Dark stormy sky\n" +
                "Spore clouds everywhere\n" +
                "All mobs get emerald armor\n" +
                "Infected mobs stronger\n" +
                "No equipment drops\n" +
                "Curse strengthens daily\n\n" +
                "Activates when Havenica dies";

        String page19 = "§0§lMusic Discs§r\n\n" +
                "§4Doomsday§r\n" +
                "§8Titan Sands§r - Craftable\n" +
                "§5Ender Fury§r\n" +
                "§1Echoes of the Abyss§r - Craftable\n" +
                "§3Water Horizons§r\n" +
                "§2Forest Haven§r - Craftable\n\n" +
                "All max stack: 1";

        String page20 = "§0§lTomahawk Details§r\n\n" +
                "Throwing weapon:\n" +
                "Base damage: 12\n" +
                "Returns to thrower\n" +
                "Rotates while flying\n" +
                "Can be retrieved\n\n" +
                "Boss versions:\n" +
                "Custom damage\n" +
                "Particle trails\n" +
                "Cannot be picked up";

        String page21 = "§0§lWorld Generation§r\n\n" +
                "§8Froststone§r: cold biomes only\n" +
                "§8Stormite§r: Nether only\n" +
                "§4Salondite§r: badlands, dripstone\n" +
                "§8Tectonite§r: overworld\n" +
                "§8Moonstone§r: overworld\n" +
                "§8Luminite§r: End only\n" +
                "§5Mystborn Dust§r: overworld\n\n" +
                "Molten gold basalt: basalt deltas, soul sand valleys";

        String page22 = "§0§lCrafting Notes§r\n\n" +
                "Froststone tools: normal recipes\n" +
                "Royal swords: base + gold\n" +
                "Peacekeeper: both royals + materials\n" +
                "Vanquisher: peacekeeper + more\n" +
                "Forest Heart: emerald blocks + luminite\n" +
                "Power Core: mystborn dust + materials\n\n" +
                "Check recipe book for exact patterns";

        String page23 = "§0§lSurvival Tips§r\n\n" +
                "Make froststone gear early\n" +
                "Tame mantis with cauliflower\n" +
                "Use shard blocks for defense\n" +
                "Stock power cores for fights\n" +
                "Avoid fighting Havenica unless ready for curse\n" +
                "Honey berries good early food\n" +
                "Keep tomahawks ready\n" +
                "Build underground during curse";

        String page24 = "§0§lFighting Havenica§r\n\n" +
                "If you must fight:\n" +
                "Best gear (Vanquisher)\n" +
                "Multiple players help\n" +
                "Destroy Haven Cores first\n" +
                "Watch laser charge particles\n" +
                "Stay mobile for shockwaves\n" +
                "Kill summoned bogged\n\n" +
                "Remember: winning = permanent world curse";

        String page25 = "§0§lWarnings§r\n\n" +
                "§4Defeating Havenica permanently changes world§r\n\n" +
                "Shard blocks damage you\n" +
                "Spore vision very dangerous\n" +
                "Basalt howlers extremely deadly\n" +
                "Some weapons break but leave materials\n" +
                "Dungeon cobble unbreakable\n" +
                "World curse cannot be lifted";

        String page26 = "§0§lFinal Notes§r\n\n" +
                "This guide covers Mystborn Horizons mechanics.\n\n" +
                "Remember:\n" +
                "Explore carefully\n" +
                "Craft better gear progressively\n" +
                "Think before boss fights\n" +
                "World curse is permanent\n" +
                "Work with others\n\n" +
                "§8- Cassetu§r";

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
                new RawFilteredPair<>(Text.literal(page26), Optional.of(Text.literal(page26)))
        );

        WrittenBookContentComponent bookContent = new WrittenBookContentComponent(
                new RawFilteredPair<>("§4The Mystborn Chronicles§r", Optional.of("§4The Mystborn Chronicles§r")),
                "§8Cassetu§r",
                0,
                pages,
                true
        );

        book.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, bookContent);

        if (player.getInventory().getEmptySlot() != -1) {
            player.getInventory().insertStack(book);
            player.sendMessage(Text.literal("§8A mysterious book has appeared in your inventory...").formatted(Formatting.ITALIC), false);
        } else {
            player.dropItem(book, false);
            player.sendMessage(Text.literal("§8A mysterious book has fallen at your feet...").formatted(Formatting.ITALIC), false);
        }
    }
}