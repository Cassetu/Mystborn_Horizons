package cassetu.mystbornhorizons.datagen;

import cassetu.mystbornhorizons.block.ModBlocks;
import cassetu.mystbornhorizons.item.ModItems;
import cassetu.mystbornhorizons.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        List<ItemConvertible> FROSTSTONE_SMELTABLES = List.of(ModItems.RAW_FROSTSTONE, ModBlocks.FROSTSTONE_ORE,
                ModBlocks.FROSTSTONE_DEEPSLATE_ORE);
        List<ItemConvertible> STORMITE_SMELTABLES = List.of(ModItems.RAW_STORMITE, ModBlocks.STORMITE_ORE,
                ModBlocks.STORMITE_DEEPSLATE_ORE);
        List<ItemConvertible> SALONDITE_SMELTABLES = List.of(ModItems.RAW_SALONDITE, ModBlocks.SALONDITE_ORE);


        offerSmelting(exporter, FROSTSTONE_SMELTABLES, RecipeCategory.MISC, ModItems.FROSTSTONE, 5f, 200, "froststone");
        offerBlasting(exporter, FROSTSTONE_SMELTABLES, RecipeCategory.MISC, ModItems.FROSTSTONE, 5f, 100, "froststone");
        offerSmelting(exporter, STORMITE_SMELTABLES, RecipeCategory.MISC, ModItems.STORMITE, 5f, 200, "stormite");
        offerBlasting(exporter, STORMITE_SMELTABLES, RecipeCategory.MISC, ModItems.STORMITE, 5f, 100, "stormite");

        offerSmelting(exporter, SALONDITE_SMELTABLES, RecipeCategory.MISC, ModItems.SALONDITE, 17f, 400, "salondite");
        offerBlasting(exporter, SALONDITE_SMELTABLES, RecipeCategory.MISC, ModItems.SALONDITE, 17f, 200, "salondite");

        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, ModItems.FROSTSTONE, RecipeCategory.DECORATIONS, ModBlocks.FROSTSTONE_BLOCK);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.FROSTSTONE_PICKAXE)
                .pattern("RRR")
                .pattern(" S ")
                .pattern(" S ")
                .input('R', ModItems.FROSTSTONE)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.FROSTSTONE), conditionsFromItem(ModItems.FROSTSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.FROSTSTONE_HOE)
                .pattern("RR ")
                .pattern(" S ")
                .pattern(" S ")
                .input('R', ModItems.FROSTSTONE)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.FROSTSTONE), conditionsFromItem(ModItems.FROSTSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.FROSTSTONE_AXE)
                .pattern("RR ")
                .pattern("RS ")
                .pattern(" S ")
                .input('R', ModItems.FROSTSTONE)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.FROSTSTONE), conditionsFromItem(ModItems.FROSTSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.FROSTSTONE_SHOVEL)
                .pattern(" R ")
                .pattern(" S ")
                .pattern(" S ")
                .input('R', ModItems.FROSTSTONE)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.FROSTSTONE), conditionsFromItem(ModItems.FROSTSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.FROSTSTONE_SWORD)
                .pattern(" R ")
                .pattern(" R ")
                .pattern(" S ")
                .input('R', ModItems.FROSTSTONE)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.FROSTSTONE), conditionsFromItem(ModItems.FROSTSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.ROYAL_FROSTSTONE_SWORD)
                .pattern(" R ")
                .pattern("VMV")
                .pattern(" S ")
                .input('R', ModItems.FROSTSTONE)
                .input('M', ModItems.FROSTSTONE_SWORD)
                .input('S', Items.STICK)
                .input('V', Items.GOLD_INGOT)
                .criterion(hasItem(ModItems.FROSTSTONE), conditionsFromItem(ModItems.FROSTSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.ROYAL_TECTONITE_SWORD)
                .pattern(" R ")
                .pattern("VMV")
                .pattern(" S ")
                .input('R', ModItems.TECTONITE_GEODE)
                .input('M', ModItems.MYSTBORN_DUST)
                .input('S', Items.STICK)
                .input('V', Items.GOLD_INGOT)
                .criterion(hasItem(ModItems.MYSTBORN_DUST), conditionsFromItem(ModItems.MYSTBORN_DUST))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.PEACEKEEPER_SWORD)
                .pattern("IRI")
                .pattern("CMT")
                .pattern("VNV")
                .input('R', ModItems.MOONSTONE_RAPIER)
                .input('C', ModItems.ROYAL_FROSTSTONE_SWORD)
                .input('T', ModItems.ROYAL_TECTONITE_SWORD)
                .input('M', ModItems.MYSTBORN_DUST)
                .input('N', Items.NETHERITE_SWORD)
                .input('V', Items.GOLD_INGOT)
                .input('I', Items.IRON_INGOT)
                .criterion(hasItem(ModItems.MYSTBORN_DUST), conditionsFromItem(ModItems.MYSTBORN_DUST))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.VANQUISHER_SWORD)
                .pattern("MRM")
                .pattern("CMT")
                .pattern("NSN")
                .input('R', ModItems.PEACEKEEPER_SWORD)
                .input('C', ModItems.ROYAL_FROSTSTONE_SWORD)
                .input('T', ModItems.ROYAL_TECTONITE_SWORD)
                .input('M', ModItems.MYSTBORN_DUST)
                .input('S', Items.STICK)
                .input('N', Items.NETHERITE_INGOT)
                .criterion(hasItem(ModItems.MYSTBORN_DUST), conditionsFromItem(ModItems.MYSTBORN_DUST))
                .offerTo(exporter);



        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.FROSTSTONE_CHESTPLATE)
                .pattern("I I")
                .pattern("RCR")
                .pattern("RIR")
                .input('R', ModItems.FROSTSTONE)
                .input('I', Items.IRON_INGOT)
                .input('C', Items.IRON_CHESTPLATE)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(ModItems.FROSTSTONE), conditionsFromItem(ModItems.FROSTSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.FROSTSTONE_BOOTS)
                .pattern(" C ")
                .pattern("I I")
                .pattern("R R")
                .input('R', ModItems.FROSTSTONE)
                .input('I', Items.IRON_INGOT)
                .input('C', Items.IRON_BOOTS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(ModItems.FROSTSTONE), conditionsFromItem(ModItems.FROSTSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.FROSTSTONE_LEGGINGS)
                .pattern("RIR")
                .pattern("ICI")
                .pattern("R R")
                .input('R', ModItems.FROSTSTONE)
                .input('I', Items.IRON_INGOT)
                .input('C', Items.IRON_LEGGINGS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(ModItems.FROSTSTONE), conditionsFromItem(ModItems.FROSTSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.FROSTSTONE_HELMET)
                .pattern("IRI")
                .pattern("ICI")
                .pattern("   ")
                .input('R', ModItems.FROSTSTONE)
                .input('I', Items.IRON_INGOT)
                .input('C', Items.IRON_HELMET)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(ModItems.FROSTSTONE), conditionsFromItem(ModItems.FROSTSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.TOMAHAWK)
                .pattern(" IA")
                .pattern(" SI")
                .pattern(" S ")
                .input('I', Items.IRON_INGOT)
                .input('A', ModItems.AXE_HEAD)
                .input('S', Items.STICK)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(ModItems.AXE_HEAD), conditionsFromItem(ModItems.AXE_HEAD))
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.AXE_HEAD)
                .pattern("FXF")
                .pattern("FBI")
                .pattern(" I ")
                .input('I', Items.COBBLESTONE)
                .input('F', Items.OBSIDIAN)
                .input('B', Items.INK_SAC)
                .input('X', Items.BLACKSTONE)
                .criterion(hasItem(Items.COBBLESTONE), conditionsFromItem(Items.COBBLESTONE))
                .criterion(hasItem(Items.OBSIDIAN), conditionsFromItem(Items.OBSIDIAN))
                .criterion(hasItem(Items.INK_SAC), conditionsFromItem(Items.INK_SAC))
                .criterion(hasItem(Items.BLACKSTONE), conditionsFromItem(Items.BLACKSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITAN_SANDS_MUSIC_DISC)
                .pattern("RSR")
                .pattern("MTM")
                .pattern("RSR")
                .input('R', ModItems.FROSTSTONE)
                .input('T', ModItems.TECTONITE_GEODE)
                .input('S', ModItems.STORMITE)
                .input('M', ModItems.MYSTBORN_DUST)
                .criterion(hasItem(ModItems.MYSTBORN_DUST), conditionsFromItem(ModItems.MYSTBORN_DUST))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DUNGEON_ROOTMASS)
                .pattern("   ")
                .pattern(" RR")
                .pattern(" RR")
                .input('R', ModItems.ROOT)
                .criterion(hasItem(ModItems.ROOT), conditionsFromItem(ModItems.ROOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ECHOES_OF_THE_ABYSS_MUSIC_DISC)
                .pattern("ESE")
                .pattern("MTM")
                .pattern("ESE")
                .input('T', ModItems.TECTONITE_GEODE)
                .input('S', ModItems.STORMITE)
                .input('E', Items.ECHO_SHARD)
                .input('M', ModItems.MYSTBORN_DUST)
                .criterion(hasItem(ModItems.MYSTBORN_DUST), conditionsFromItem(ModItems.MYSTBORN_DUST))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.MOONSTONE_RAPIER)
                .pattern(" M ")
                .pattern("GRG")
                .pattern(" S ")
                .input('R', ModItems.MYSTBORN_DUST)
                .input('M', ModItems.MOONSTONE)
                .input('G', Items.GOLD_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.MYSTBORN_DUST), conditionsFromItem(ModItems.MYSTBORN_DUST))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SHARD_BLOCK)
                .pattern(" R ")
                .pattern("RCR")
                .pattern(" R ")
                .input('R', ModItems.FROSTSTONE)
                .input('C', Items.COBBLESTONE)
                .criterion(hasItem(ModItems.FROSTSTONE), conditionsFromItem(ModItems.FROSTSTONE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.POWER_CORE)
                .pattern(" I ")
                .pattern("RCR")
                .pattern(" I ")
                .input('R', ModItems.FROSTSTONE)
                .input('I', Items.IRON_INGOT)
                .input('C', ModItems.MYSTBORN_DUST)
                .criterion(hasItem(ModItems.MYSTBORN_DUST), conditionsFromItem(ModItems.MYSTBORN_DUST))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.SALMON_NIGIRI, 2)
                .pattern("   ")
                .pattern(" S ")
                .pattern("WWW")
                .input('S', Items.SALMON)
                .input('W', Items.WHEAT)
                .criterion(hasItem(Items.SALMON), conditionsFromItem(Items.SALMON))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.COD_NIGIRI, 2)
                .pattern("   ")
                .pattern(" S ")
                .pattern("WWW")
                .input('S', Items.COD)
                .input('W', Items.WHEAT)
                .criterion(hasItem(Items.COD), conditionsFromItem(Items.COD))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.WAFFLE, 3)
                .pattern("   ")
                .pattern("  E")
                .pattern(" WS")
                .input('E', Items.EGG)
                .input('S', Items.SUGAR)
                .input('W', Items.WHEAT)
                .criterion(hasItem(Items.WHEAT), conditionsFromItem(Items.WHEAT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.CHICKEN_NUGGETS, 4)
                .pattern("   ")
                .pattern("  C")
                .pattern("  S")
                .input('C', Items.COOKED_CHICKEN)
                .input('S', Items.WHEAT)
                .criterion(hasItem(Items.COOKED_CHICKEN), conditionsFromItem(Items.COOKED_CHICKEN))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.VEGGIE_SANDWICH)
                .pattern("   ")
                .pattern("  B")
                .pattern(" BV")
                .input('V', ModTags.Items.VEGGIES)
                .input('B', Items.BREAD)
                .criterion(hasItem(Items.BREAD), conditionsFromItem(Items.BREAD))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.MYST_BRICKS, 8)
                .pattern("DDD")
                .pattern("DMD")
                .pattern("DDD")
                .input('D', Items.DEEPSLATE_BRICKS)
                .input('M', ModItems.MYSTBORN_DUST)
                .criterion(hasItem(ModItems.MYSTBORN_DUST), conditionsFromItem(ModItems.MYSTBORN_DUST))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.MYST_CRATE, 2)
                .pattern(" D ")
                .pattern(" M ")
                .pattern(" D ")
                .input('D', Items.DEEPSLATE_BRICK_SLAB)
                .input('M', ModItems.MYSTBORN_DUST)
                .criterion(hasItem(ModItems.MYSTBORN_DUST), conditionsFromItem(ModItems.MYSTBORN_DUST))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.PACKED_ICE_BRICKS, 4)
                .pattern("   ")
                .pattern(" PP")
                .pattern(" PP")
                .input('P', Items.PACKED_ICE)
                .criterion(hasItem(Items.PACKED_ICE), conditionsFromItem(Items.PACKED_ICE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.CARVED_ICE_BRICKS, 4)
                .pattern("   ")
                .pattern(" BP")
                .pattern(" PB")
                .input('P', Items.PACKED_ICE)
                .input('B', ModBlocks.PACKED_ICE_BRICKS)
                .criterion(hasItem(Items.PACKED_ICE), conditionsFromItem(Items.PACKED_ICE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.MOLTEN_GOLD_BLACKSTONE, 4)
                .pattern("   ")
                .pattern(" BG")
                .pattern(" GB")
                .input('G', Items.GOLD_INGOT)
                .input('B', Items.GILDED_BLACKSTONE)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.MOLTEN_GOLD_BASALT, 4)
                .pattern("   ")
                .pattern(" BG")
                .pattern(" GB")
                .input('G', Items.GOLD_INGOT)
                .input('B', Items.BASALT)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter);

//        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FROSTSTONE, 9)
//                .input(ModBlocks.FROSTSTONE_BLOCK)
//                .criterion(hasItem(ModBlocks.FROSTSTONE_BLOCK), conditionsFromItem(ModBlocks.FROSTSTONE_BLOCK))
//                .offerTo(exporter);

//        offerSmithingTrimRecipe(exporter, ModItems.K_ARMOR_TRIM_SMITHING_TEMPLATE, Identifier.of(MystbornHorizons.MOD_ID,"k"));
    }
}
