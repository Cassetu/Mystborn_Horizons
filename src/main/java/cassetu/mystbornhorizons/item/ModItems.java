package cassetu.mystbornhorizons.item;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.block.ModBlocks;
import cassetu.mystbornhorizons.entity.ModEntities;
import cassetu.mystbornhorizons.item.custom.ModArmorItem;
import cassetu.mystbornhorizons.sound.ModSounds;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModItems {
    public static final Item FROSTSTONE = registerItem("froststone", new Item(new Item.Settings()));

    public static final Item SALONDITE = registerItem("salondite", new Item(new Item.Settings()));
    public static final Item RAW_SALONDITE = registerItem("raw_salondite", new Item(new Item.Settings()));

    public static final Item RAW_FROSTSTONE = registerItem("raw_froststone", new Item(new Item.Settings()));
    public static final Item STORMITE = registerItem("stormite", new Item(new Item.Settings()));
    public static final Item RAW_STORMITE = registerItem("raw_stormite", new Item(new Item.Settings()));
    public static final Item TECTONITE_GEODE = registerItem("tectonite_geode", new Item(new Item.Settings()));
    public static final Item MOONSTONE = registerItem("moonstone", new Item(new Item.Settings()));

    public static final Item MYSTBORN_DUST = registerItem("mystborn_dust", new Item(new Item.Settings()));

    public static final Item CAULIFLOWER = registerItem("cauliflower", new Item(new Item.Settings().food(ModFoodComponents.CAULIFLOWER)));
    public static final Item POWER_CORE = registerItem("power_core", new Item(new Item.Settings().food(ModFoodComponents.POWER_CORE)) {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            tooltip.add(Text.translatable("tooltip.mystbornhorizons.power_core.tooltip"));
            super.appendTooltip(stack, context, tooltip, type);
        }
    });
    public static final Item SALMON_NIGIRI = registerItem("salmon_nigiri", new Item(new Item.Settings().food(ModFoodComponents.SALMON_NIGIRI)));
    public static final Item COD_NIGIRI = registerItem("cod_nigiri", new Item(new Item.Settings().food(ModFoodComponents.COD_NIGIRI)));
    public static final Item WAFFLE = registerItem("waffle", new Item(new Item.Settings().food(ModFoodComponents.WAFFLE)));
    public static final Item CHICKEN_NUGGETS = registerItem("chicken_nuggets", new Item(new Item.Settings().food(ModFoodComponents.CHICKEN_NUGGETS)));
    public static final Item VEGGIE_SANDWICH = registerItem("veggie_sandwich", new Item(new Item.Settings().food(ModFoodComponents.VEGGIE_SANDWICH)));

    public static final Item FROSTSTONE_SWORD = registerItem("froststone_sword",
            new SwordItem(ModToolMaterials.FROSTSTONE, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.FROSTSTONE, 3, -2.4f))) {
                @Override
                public boolean hasRecipeRemainder() {
                    return true;
                }

                @Override
                public ItemStack getRecipeRemainder(ItemStack stack) {
                    ItemStack copy = stack.copy();
                    copy.setDamage(copy.getDamage() + 2000);
                    return copy.getDamage() >= copy.getMaxDamage() ? ItemStack.EMPTY : copy;
                }
            });
    public static final Item MOONSTONE_RAPIER = registerItem("moonstone_rapier",
            new SwordItem(ModToolMaterials.FROSTSTONE, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.FROSTSTONE, 4, -2.0f))) {
                @Override
                public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
                    tooltip.add(Text.translatable("tooltip.mystbornhorizons.moonstone_rapier.tooltip"));
                    super.appendTooltip(stack, context, tooltip, type);
                }

                @Override
                public boolean hasRecipeRemainder() {
                    return true;
                }

                @Override
                public ItemStack getRecipeRemainder(ItemStack stack) {
                    ItemStack copy = stack.copy();
                    copy.setDamage(copy.getDamage() + 2000);
                    return copy.getDamage() >= copy.getMaxDamage() ? ItemStack.EMPTY : copy;
                }
            });
    public static final Item ROYAL_FROSTSTONE_SWORD = registerItem("royal_froststone_sword",
            new SwordItem(ModToolMaterials.FROSTSTONE, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.FROSTSTONE, 5, -2.4f))) {
                @Override
                public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
                    tooltip.add(Text.translatable("tooltip.mystbornhorizons.royal_sword.tooltip"));
                    super.appendTooltip(stack, context, tooltip, type);
                }

                @Override
                public boolean hasRecipeRemainder() {
                    return true;
                }

                @Override
                public ItemStack getRecipeRemainder(ItemStack stack) {
                    ItemStack copy = stack.copy();
                    copy.setDamage(copy.getDamage() + 2000);
                    return copy.getDamage() >= copy.getMaxDamage() ? ItemStack.EMPTY : copy;
                }
            });
    public static final Item ROYAL_TECTONITE_SWORD = registerItem("royal_tectonite_sword",
            new SwordItem(ModToolMaterials.FROSTSTONE, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.FROSTSTONE, 6, -2.8f))) {
                @Override
                public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
                    tooltip.add(Text.translatable("tooltip.mystbornhorizons.royal_sword.tooltip"));
                    super.appendTooltip(stack, context, tooltip, type);
                }

                @Override
                public boolean hasRecipeRemainder() {
                    return true;
                }

                @Override
                public ItemStack getRecipeRemainder(ItemStack stack) {
                    ItemStack copy = stack.copy();
                    copy.setDamage(copy.getDamage() + 2000);
                    return copy.getDamage() >= copy.getMaxDamage() ? ItemStack.EMPTY : copy;
                }
            });

    public static final Item PEACEKEEPER_SWORD = registerItem("peacekeeper_sword",
            new SwordItem(ModToolMaterials.MYSTBORN_DUST, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.MYSTBORN_DUST, 8, -2.4f))) {
                @Override
                public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
                    tooltip.add(Text.translatable("tooltip.mystbornhorizons.peace.tooltip"));
                    super.appendTooltip(stack, context, tooltip, type);
                }

                @Override
                public boolean hasRecipeRemainder() {
                    return true;
                }

                @Override
                public ItemStack getRecipeRemainder(ItemStack stack) {
                    ItemStack copy = stack.copy();
                    copy.setDamage(copy.getDamage() + 2000000);
                    return copy.getDamage() >= copy.getMaxDamage() ? ItemStack.EMPTY : copy;
                }
            });

    public static final Item VANQUISHER_SWORD = registerItem("vanquisher_sword",
            new SwordItem(ModToolMaterials.MYSTBORN_DUST, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.MYSTBORN_DUST, 12, -2.4f))) {
                @Override
                public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
                    tooltip.add(Text.translatable("tooltip.mystbornhorizons.vanquish.tooltip"));
                    super.appendTooltip(stack, context, tooltip, type);
                }
            });




    public static final Item FROSTSTONE_PICKAXE = registerItem("froststone_pickaxe",
            new PickaxeItem(ModToolMaterials.FROSTSTONE, new Item.Settings()
                    .attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterials.FROSTSTONE, 1, -2.8f))));
    public static final Item FROSTSTONE_SHOVEL = registerItem("froststone_shovel",
            new ShovelItem(ModToolMaterials.FROSTSTONE, new Item.Settings()
                    .attributeModifiers(ShovelItem.createAttributeModifiers(ModToolMaterials.FROSTSTONE, 1.5f, -3f))));
    public static final Item FROSTSTONE_AXE = registerItem("froststone_axe",
            new AxeItem(ModToolMaterials.FROSTSTONE, new Item.Settings()
                    .attributeModifiers(AxeItem.createAttributeModifiers(ModToolMaterials.FROSTSTONE, 6, -3.2f))));
    public static final Item FROSTSTONE_HOE = registerItem("froststone_hoe",
            new HoeItem(ModToolMaterials.FROSTSTONE, new Item.Settings()
                    .attributeModifiers(HoeItem.createAttributeModifiers(ModToolMaterials.FROSTSTONE, 5, -3f))));

    public static final Item FROSTSTONE_HELMET = registerItem("froststone_helmet",
            new ArmorItem(ModArmorMaterials.FROSTSTONE_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings()
                    .maxDamage(ArmorItem.Type.HELMET.getMaxDamage(75))));
    public static final Item FROSTSTONE_CHESTPLATE = registerItem("froststone_chestplate",
            new ModArmorItem(ModArmorMaterials.FROSTSTONE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings()
                    .maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(75))));
    public static final Item FROSTSTONE_LEGGINGS = registerItem("froststone_leggings",
            new ArmorItem(ModArmorMaterials.FROSTSTONE_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings()
                    .maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(75))));
    public static final Item FROSTSTONE_BOOTS = registerItem("froststone_boots",
            new ArmorItem(ModArmorMaterials.FROSTSTONE_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings()
                    .maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(75))));


//    public static final Item K_ARMOR_TRIM_SMITHING_TEMPLATE = registerItem("k_armor_trim_smithing_template",
//            SmithingTemplateItem.of(Identifier.of(MystbornHorizons.MOD_ID,"k"), FeatureFlags.VANILLA));
    

    public static final Item DOOMSDAY_MUSIC_DISC = registerItem("doomsday_music_disc",
            new Item(new Item.Settings().jukeboxPlayable(ModSounds.DOOMSDAY_KEY).maxCount(1)));
    public static final Item TITAN_SANDS_MUSIC_DISC = registerItem("titan_sands_music_disc",
            new Item(new Item.Settings().jukeboxPlayable(ModSounds.TITAN_SANDS_KEY).maxCount(1)));
    public static final Item ENDER_FURY_MUSIC_DISC = registerItem("ender_fury_music_disc",
            new Item(new Item.Settings().jukeboxPlayable(ModSounds.ENDER_FURY_KEY).maxCount(1)));
    public static final Item ECHOES_OF_THE_ABYSS_MUSIC_DISC = registerItem("echoes_of_the_abyss_music_disc",
            new Item(new Item.Settings().jukeboxPlayable(ModSounds.ECHOES_OF_THE_ABYSS_KEY).maxCount(1)));
    public static final Item WATER_HORIZONS_MUSIC_DISC = registerItem("water_horizons_music_disc",
            new Item(new Item.Settings().jukeboxPlayable(ModSounds.WATER_HORIZONS_KEY).maxCount(1)));

    public static final Item MANTIS_SPAWN_EGG = registerItem("mantis_spawn_egg",
            new SpawnEggItem(ModEntities.MANTIS, 0x9dc783, 0xbfaf5f, new Item.Settings()));

    public static final Item COPPER_BULB_SPAWN_EGG = registerItem("copper_bulb_spawn_egg",
            new SpawnEggItem(ModEntities.COPPERBULB, 0x9a5038, 0xc36c4d, new Item.Settings()));

    public static final Item ICE_SPIDER_SPAWN_EGG = registerItem("ice_spider_spawn_egg",
            new SpawnEggItem(ModEntities.ICESPIDER, 0x0098dc, 0x00cdf9, new Item.Settings()));

    public static final Item BASALT_HOWLER_SPAWN_EGG = registerItem("basalt_howler_spawn_egg",
            new SpawnEggItem(ModEntities.BASALTHOWLER, 0x3a3b48, 0xffa214, new Item.Settings()));

    public static final Item HONEY_BERRIES = registerItem("honey_berries",
            new AliasedBlockItem(ModBlocks.HONEY_BERRY_BUSH, new Item.Settings().food(ModFoodComponents.HONEY_BERRY)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(MystbornHorizons.MOD_ID, name), item);
    }

    public static void registerModItems() {
        MystbornHorizons.LOGGER.info("Registering Mod Items for " + MystbornHorizons.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(FROSTSTONE);
            entries.add(RAW_FROSTSTONE);
            entries.add(RAW_STORMITE);
            entries.add(STORMITE);
        });
    }
}
