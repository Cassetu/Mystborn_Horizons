package cassetu.mystbornhorizons.datagen;

import cassetu.mystbornhorizons.item.ModItems;
import cassetu.mystbornhorizons.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ModTags.Items.VEGGIES)
                .add(ModItems.CAULIFLOWER)
                .add(Items.CARROT)
                .add(Items.POTATO)
                .add(Items.NETHER_WART)
                .add(Items.PUMPKIN)
                .add(Items.BEETROOT)
                .add(Items.KELP)
                .add(Items.MELON)
                .add(Items.POPPED_CHORUS_FRUIT)
                .add(Items.CHORUS_FRUIT)
                .add(Items.MELON_SLICE)
                .add(Items.GOLDEN_CARROT)
                .add(Items.GOLDEN_APPLE)
                .add(Items.ENCHANTED_GOLDEN_APPLE)
                .add(Items.SEAGRASS)
                .add(Items.SHORT_GRASS)
                .add(Items.TALL_GRASS)
                .add(Items.DRIED_KELP)
                .add(Items.GLOW_BERRIES)
                .add(Items.SWEET_BERRIES)
                .add(Items.TORCHFLOWER_SEEDS)
                .add(Items.BROWN_MUSHROOM)
                .add(Items.RED_MUSHROOM)
                .add(Items.SEA_PICKLE)
                .add(Items.BAMBOO)
                .add(Items.CACTUS)
                .add(Items.SUGAR_CANE)
                .add(Items.COCOA_BEANS)
                .add(Items.APPLE);


        getOrCreateTagBuilder(ItemTags.SWORDS)
                .add(ModItems.FROSTSTONE_SWORD)
                .add(ModItems.ROYAL_FROSTSTONE_SWORD)
                .add(ModItems.ROYAL_TECTONITE_SWORD)
                .add(ModItems.MOONSTONE_RAPIER)
                .add(ModItems.PEACEKEEPER_SWORD);
        getOrCreateTagBuilder(ItemTags.PICKAXES)
                .add(ModItems.FROSTSTONE_PICKAXE);
        getOrCreateTagBuilder(ItemTags.SHOVELS)
                .add(ModItems.FROSTSTONE_SHOVEL);
        getOrCreateTagBuilder(ItemTags.AXES)
                .add(ModItems.FROSTSTONE_AXE);
        getOrCreateTagBuilder(ItemTags.HOES)
                .add(ModItems.FROSTSTONE_HOE);


        getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.FROSTSTONE_HELMET)
                .add(ModItems.FROSTSTONE_CHESTPLATE)
                .add(ModItems.FROSTSTONE_LEGGINGS)
                .add(ModItems.FROSTSTONE_BOOTS);

        getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ModItems.FROSTSTONE_CHESTPLATE)
                .add(ModItems.FROSTSTONE_BOOTS)
                .add(ModItems.FROSTSTONE_HELMET)
                .add(ModItems.FROSTSTONE_SWORD)
                .add(ModItems.ROYAL_FROSTSTONE_SWORD)
                .add(ModItems.ROYAL_TECTONITE_SWORD)
                .add(ModItems.MOONSTONE_RAPIER)
                .add(ModItems.FROSTSTONE_PICKAXE)
                .add(ModItems.FROSTSTONE_SHOVEL)
                .add(ModItems.FROSTSTONE_AXE)
                .add(ModItems.FROSTSTONE_HOE)
                .add(ModItems.FROSTSTONE_LEGGINGS);

        getOrCreateTagBuilder(ItemTags.EQUIPPABLE_ENCHANTABLE)
                .add(ModItems.FROSTSTONE_CHESTPLATE)
                .add(ModItems.FROSTSTONE_BOOTS)
                .add(ModItems.FROSTSTONE_HELMET)
                .add(ModItems.FROSTSTONE_LEGGINGS);

        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR_ENCHANTABLE)
                .add(ModItems.FROSTSTONE_CHESTPLATE);
        getOrCreateTagBuilder(ItemTags.FOOT_ARMOR_ENCHANTABLE)
                .add(ModItems.FROSTSTONE_BOOTS);
        getOrCreateTagBuilder(ItemTags.LEG_ARMOR_ENCHANTABLE)
                .add(ModItems.FROSTSTONE_LEGGINGS);
        getOrCreateTagBuilder(ItemTags.HEAD_ARMOR_ENCHANTABLE)
                .add(ModItems.FROSTSTONE_HELMET);
        getOrCreateTagBuilder(ItemTags.ARMOR_ENCHANTABLE)
                .add(ModItems.FROSTSTONE_BOOTS)
                .add(ModItems.FROSTSTONE_CHESTPLATE)
                .add(ModItems.FROSTSTONE_LEGGINGS)
                .add(ModItems.FROSTSTONE_HELMET);

        getOrCreateTagBuilder(ItemTags.TRIM_MATERIALS)
                .add(ModItems.FROSTSTONE);

        getOrCreateTagBuilder(ItemTags.TRIM_TEMPLATES);
//                .add(ModItems.K_ARMOR_TRIM_SMITHING_TEMPLATE);

        getOrCreateTagBuilder(ItemTags.COW_FOOD)
                .add(ModItems.CAULIFLOWER);
    }
}
