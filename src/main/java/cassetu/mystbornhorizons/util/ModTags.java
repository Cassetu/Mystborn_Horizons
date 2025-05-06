package cassetu.mystbornhorizons.util;

import cassetu.mystbornhorizons.MystbornHorizons;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {


    public static class Blocks {
        public static final TagKey<Block> NEEDS_FROSTSTONE_TOOL = createTag("needs_froststone_tool");
        public static final TagKey<Block> INCORRECT_FOR_FROSTSTONE_TOOL = createTag("incorrect_for_froststone_tool");

        public static final TagKey<Block> NEEDS_MYSTBORN_DUST_TOOL = createTag("needs_mystborn_dust_tool");
        public static final TagKey<Block> INCORRECT_FOR_MYSTBORN_DUST_TOOL = createTag("incorrect_for_mystborn_dust_tool");

        public static final TagKey<Block> BASALT = createTag("basalt");
        public static final TagKey<Block> BLACKSTONE = createTag("blackstone");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(MystbornHorizons.MOD_ID));
        }
    }

    public static class Items {
        public static final TagKey<Item> VEGGIES = createTag("veggies");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(MystbornHorizons.MOD_ID));
        }
    }
}
