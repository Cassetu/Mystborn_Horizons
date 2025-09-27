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

        String page1 = "§0§lMystborn Horizons§r\n\n" +
                "§8Adds ores, mobs, bosses, weapons, armor, structures, foods, discs, effects, enchants & terrain.\n\n" +
                "§8Created: 2025-03-16  §4Status: STILL IN DEVELOPMENT";

        String page2 = "§0§lFeatures§r\n\n" +
                "§8- New ores, tools & armor\n" +
                "§8- Bosses, mobs & structures\n" +
                "§8- Music discs & custom food\n" +
                "§8- Effects, enchantments & events";

        String page3 = "§0§lMisc§r\n\n" +
                "§8Shard Block: damages like magma; ignores Frost Walker.\n" +
                "§8Slimy (command): wall-climb ability.\n" +
                "§8Spore Vision: nausea, blindness, phantom visuals.";

        String page4 = "§0§lMusic Discs§r\n\n" +
                "§8Titan Sands, Doomsday, Water Horizons,\n" +
                "§8Ender Fury, Echoes of the Abyss, Forest Haven.";

        String page5 = "§0§lMobs§r\n\n" +
                "§8Mantis: passive; plains/flower/cherry; breed w/ cauliflower.\n" +
                "§8Copper Bulb: hostile; lush/dripstone caves; drops copper/Power Cores.\n" +
                "§8Ice Spider: hostile; cold biomes & caves; drops Froststone.";

        String page6 = "§0§lBasalt Howler§r\n\n" +
                "§8Hostile, fire & poison immune.\n" +
                "§8Charged AoE knockback (lava particles while charging).\n" +
                "§8Howl: applies slowness & weakness.\n" +
                "§8Drops: molten gold blocks, gold, rare netherite scrap.";

        String page7 = "§0§lHavenica (Boss)§r\n\n" +
                "§8Summon: right-click Forest Heart on Ancient Grove Altar (Moss Arena).\n" +
                "§8Multi-phase. Scales with players. Heals from Haven Orbs unless glowing.";

        String page8 = "§0§lHavenica Attacks§r\n\n" +
                "§8Root Network: pillars + wither/slowness; damages armor.\n" +
                "§8Shockwave: 3 waves, knockback & damage.\n" +
                "§8Teleport: moves to Haven Cores.\n" +
                "§8Toxic Laser: charged poison beams; dodge when charging.";

        String page9 = "§0§lOres (1)§r\n\n" +
                "§8Froststone: cold biomes (Y -80 to 80).\n" +
                "§8Tectonite: overworld (Y -80 to 80).\n" +
                "§8Mystborn Dust: overworld, very rare (Y -80 to 80).";

        String page10 = "§0§lOres (2)§r\n\n" +
                "§8Moonstone: overworld low Y (-80 to -15).\n" +
                "§8Stormite: Nether high Y (80 to 127).\n" +
                "§8Luminite: End (Y -80 to 80).\n" +
                "§8Salondite: Badlands variants (Y -80 to 160).";

        String page11 = "§0§lWeapons§r\n\n" +
                "§8PeaceKeeper Sword: 10,000 durability; enchantable; above netherite.\n" +
                "§8Vanquisher Sword: 10,000 durability; stronger; not enchantable.\n" +
                "§8Tomahawk: throwable; pierces; retrievable; damage only when thrown.";

        String page12 = "§0§lArmor & Tools§r\n\n" +
                "§8Froststone Tools/Armor: early tier.\n" +
                "§8Moonstone Rapier: fast attacks.\n" +
                "§8Royal Froststone/Tectonite: upgraded sword variants.";

        String page13 = "§0§lStructures§r\n\n" +
                "§8Jungle Pyramid, Enhanced Witch Hut, Ice Tower,\n" +
                "§8Myst Chamber (spawns Copper Bulb), Mushroom Tower,\n" +
                "§8Moss Arena (Havenica).";

        String page14 = "§0§lChest Loot (examples)§r\n\n" +
                "§8Butcher Village: Chicken Nuggets, Veggie Sandwich.\n" +
                "§8Shipwrecks: Nigiri.\n" +
                "§8Desert Pyramid/Village: Titan Sands disc.";

        String page15 = "§0§lChest Loot (cont.)§r\n\n" +
                "§8Bastion: Doomsday disc, Stormite.\n" +
                "§8Bastion Bridge: Molten Gold Blackstone/Basalt.\n" +
                "§8Stronghold: Waffles; Crossing: Royal Tectonite Sword.";

        String page16 = "§0§lMob Drops§r\n\n" +
                "§8Ender Dragon: Ender Fury disc.\n" +
                "§8Creepers: drop Cauliflower.";

        String page17 = "§0§lFoods§r\n\n" +
                "§8Veggie Sandwich: Regeneration.\n" +
                "§8Chicken Nugget: small chance Slowness.\n" +
                "§8Cauliflower: chance Health Boost (from Creepers).\n" +
                "§8Waffle: chance Resistance.\n" +
                "§8Nigiri: chance Dolphin's Grace or Conduit Power.\n" +
                "§8Honey Berries: small chance Instant Health.\n" +
                "§8Power Core: strong buffs but risky.";

        String page18 = "§0§lBlocks§r\n\n" +
                "§8Froststone Block, Myst Bricks, Molten Gold Blackstone/Basalt, Packed Ice Bricks.\n" +
                "§8Dungeon Rootmass (strong), Dungeon Cobble (unbreakable), Shard Block (damages), Ancient Grove Altar.";

        String page19 = "§0§lWorld Gen Notes§r\n\n" +
                "§8Froststone in cold biomes; Stormite in Nether high Y; Luminite in End; Salondite in Badlands.\n" +
                "§8Molten gold basalt appears in basalt deltas & soul sand valleys.";

        String page20 = "§0§lCrafting Notes§r\n\n" +
                "§8Froststone tools: standard recipes.\n" +
                "§8Royal swords: base + gold.\n" +
                "§8Peacekeeper: combines royal swords + materials.\n" +
                "§8Vanquisher: upgraded from Peacekeeper.\n" +
                "§8Forest Heart: emerald blocks + luminite.";

        String page21 = "§0§lSurvival Tips§r\n\n" +
                "§8Make Froststone gear early.\n" +
                "§8Tame Mantis with Cauliflower.\n" +
                "§8Use Shard Blocks defensively.\n" +
                "§8Stock Power Cores for fights.";

        String page22 = "§0§lFighting Havenica§r\n\n" +
                "§8Destroy Haven Cores first.\n" +
                "§8Avoid Root Network areas.\n" +
                "§8Dodge Toxic Laser when charged.\n" +
                "§8Stay mobile vs Shockwaves. Multi-player recommended.";

        String page23 = "§0§lWarnings§r\n\n" +
                "§4Defeating Havenica triggers a permanent World Curse.\n\n" +
                "§8Shard Blocks deal damage; Spore Vision is dangerous; Basalt Howlers are lethal.";

        String page24 = "§0§lUsage§r\n\n" +
                "§8Requires Fabric API.\n" +
                "§8Tested on Minecraft 1.21.1. May not work on other versions.\n" +
                "§8Report issues to @Cassetu.";

        String page25 = "§0§lSupport & License§r\n\n" +
                "§8Find me: @Cassetu\n" +
                "§8All Rights Reserved 2025 © Cassetu";

        String page26 = "§0§lFinal Notes§r\n\n" +
                "§8Most content is obtainable in survival.\n" +
                "§8Check mod pages for updates and full details.";

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
