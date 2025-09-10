package cassetu.mystbornhorizons.datagen;


import cassetu.mystbornhorizons.block.ModBlocks;
import cassetu.mystbornhorizons.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(ModBlocks.DUNGEON_ROOTMASS);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.FROSTSTONE_BLOCK)
                .add(ModBlocks.FROSTSTONE_ORE)
                .add(ModBlocks.STORMITE_ORE)
                .add(ModBlocks.SALONDITE_ORE)
                .add(ModBlocks.LUMINITE_END_ORE)
                .add(ModBlocks.STORMITE_DEEPSLATE_ORE)
                .add(ModBlocks.FROSTSTONE_DEEPSLATE_ORE)
                .add(ModBlocks.MOONSTONE_DEEPSLATE_ORE)
                .add(ModBlocks.TECTONITE_DEEPSLATE_ORE)

                .add(ModBlocks.MYSTBORN_DUST_ORE)
                .add(ModBlocks.MYST_BRICKS)
                .add(ModBlocks.MYST_CRATE)
                .add(ModBlocks.PACKED_ICE_BRICKS)
                .add(ModBlocks.CARVED_ICE_BRICKS)
                .add(ModBlocks.MOLTEN_GOLD_BLACKSTONE)
                .add(ModBlocks.MOLTEN_GOLD_BASALT)

                .add(ModBlocks.SHARD_BLOCK);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.MYST_CRATE)
                .add(ModBlocks.PACKED_ICE_BRICKS)
                .add(ModBlocks.CARVED_ICE_BRICKS)
                .add(ModBlocks.MOLTEN_GOLD_BLACKSTONE)
                .add(ModBlocks.MOLTEN_GOLD_BASALT)
                .add(ModBlocks.MYST_BRICKS);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.FROSTSTONE_DEEPSLATE_ORE)
                .add(ModBlocks.TECTONITE_DEEPSLATE_ORE)
                .add(ModBlocks.SALONDITE_ORE)
                .add(ModBlocks.LUMINITE_END_ORE)
                .add(ModBlocks.STORMITE_DEEPSLATE_ORE)
                .add(ModBlocks.MOONSTONE_DEEPSLATE_ORE)
                .add(ModBlocks.DUNGEON_ROOTMASS);

        getOrCreateTagBuilder(ModTags.Blocks.NEEDS_FROSTSTONE_TOOL)
                .addTag(BlockTags.NEEDS_IRON_TOOL);
    }
}
