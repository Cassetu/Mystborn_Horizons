package cassetu.mystbornhorizons.event;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
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
        // You can later add NBT tags or a persistent state marker if you want this to only happen once
    }

    private static void giveWelcomeBook(ServerPlayerEntity player) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

        List<RawFilteredPair<Text>> pages = new ArrayList<>();

        // --- Page 1: Intro ---
        pages.add(new RawFilteredPair<>(
                Text.literal("§0§lMystborn Horizons§r\n\n" +
                        "§7A world reborn as tectonic plates shift, bringing§r\n" +
                        "§6new ores, mobs, bosses, weapons, armor§r\n" +
                        "§7and more!§r\n\n" +
                        "§oCreated March 16, 2025 — still in development.§r"),
                Optional.of(Text.literal("Welcome!"))));

        // --- Page 2: Misc Features ---
        pages.add(new RawFilteredPair<>(
                Text.literal("§0§lFeatures§r\n\n" +
                        "§7- Shard Block, deadlier than magma.\n" +
                        "- 5 Music Discs: Titan Sands, Doomsday,\nWater Horizons, Ender Fury, Echoes Abyss.\n" +
                        "- Slimy Effect (wall scaling).\n" +
                        "- Enchant: Lightning Striker (calls lightning).\n" +
                        "- Taller mountains, dunes, erosion, etc."),
                Optional.of(Text.literal("Features"))));

        // --- Page 3: Mobs ---
        pages.add(new RawFilteredPair<>(
                Text.literal("§0§lMobs§r\n\n" +
                        "- Mantis (peaceful, breed w/ cauliflower).\n" +
                        "- Copper Bulb (drops copper/power cores).\n" +
                        "- Ice Spider (drops Froststone).\n" +
                        "- Basalt Howler (AOE attacks, rare drops).\n" +
                        "- §4Boss: Havenica!§r Summoned w/ Forest Heart."),
                Optional.of(Text.literal("Mobs"))));

        // --- Page 4: Ores ---
        pages.add(new RawFilteredPair<>(
                Text.literal("§0§lOres§r\n\n" +
                        "- Froststone: Cold biomes\n" +
                        "- Tectonite: Overworld\n" +
                        "- Mystborn Dust: Overworld\n" +
                        "- Moonstone: -80 to -15\n" +
                        "- Stormite: Nether 80–127\n" +
                        "- Luminite: End\n" +
                        "- Salondite: Badlands"),
                Optional.of(Text.literal("Ores"))));

        // --- Page 5: Weapons ---
        pages.add(new RawFilteredPair<>(
                Text.literal("§0§lWeapons§r\n\n" +
                        "- PeaceKeeper Sword (10k dura, ench.)\n" +
                        "- Vanquisher Sword (10k dura, stronger).\n" +
                        "- Tomahawk (throwable, piercing)."),
                Optional.of(Text.literal("Weapons"))));

        // --- Page 6: Structures ---
        pages.add(new RawFilteredPair<>(
                Text.literal("§0§lStructures§r\n\n" +
                        "- Jungle Pyramid\n" +
                        "- Enhanced Witch Hut\n" +
                        "- Ice Tower\n" +
                        "- Myst Chamber Room\n" +
                        "- Mushroom Tower\n" +
                        "- Moss Arena (Havenica’s lair)"),
                Optional.of(Text.literal("Structures"))));

        // --- Page 7: Loot & Foods ---
        pages.add(new RawFilteredPair<>(
                Text.literal("§0§lLoot & Foods§r\n\n" +
                        "- New chest loot (discs, ores, waffles).\n" +
                        "- Creepers drop cauliflower.\n\n" +
                        "Foods:\n" +
                        "- Veggie Sandwich (regen)\n" +
                        "- Chicken Nugget (slowness)\n" +
                        "- Waffle (resistance)\n" +
                        "- Nigiri, Honey Berries, Power Core etc."),
                Optional.of(Text.literal("Loot"))));

        // --- Page 8: Usage & Notes ---
        pages.add(new RawFilteredPair<>(
                Text.literal("§0§lUsage & Notes§r\n\n" +
                        "§7Use w/ Fabric API on 1.21.1.\n" +
                        "(Almost) everything is survival-obtainable.\n\n" +
                        "Visit the wiki:\n" +
                        "§9https://tinyurl.com/mystbornwiki§r"),
                Optional.of(Text.literal("Usage"))));

        // --- Page 9: Support ---
        pages.add(new RawFilteredPair<>(
                Text.literal("§0§lSupport Me§r\n\n" +
                        "You can find my channel §b@Cassetu§r!\n" +
                        "Comment your ideas & suggestions.\n\n" +
                        "§oAll Rights Reserved 2025 @Cassetu§r"),
                Optional.of(Text.literal("Support"))));

        WrittenBookContentComponent bookContent = new WrittenBookContentComponent(
                new RawFilteredPair<>("§4Mystborn Horizons Guide§r", Optional.of("§4Mystborn Horizons Guide§r")),
                "§8Cassetu§r",
                0,
                pages,
                true
        );

        book.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, bookContent);

        if (player.getInventory().getEmptySlot() != -1) {
            player.getInventory().insertStack(book);
            player.sendMessage(Text.literal("§6A mysterious tome has appeared in your inventory...").formatted(Formatting.ITALIC), false);
        } else {
            player.dropItem(book, false);
            player.sendMessage(Text.literal("§6A mysterious tome has fallen at your feet...").formatted(Formatting.ITALIC), false);
        }
    }
}
