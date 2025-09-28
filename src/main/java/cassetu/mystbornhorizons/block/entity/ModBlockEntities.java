package cassetu.mystbornhorizons.block.entity;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<BasaltSpawnerBlockEntity> BASALT_SPAWNER =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(MystbornHorizons.MOD_ID, "basalt_spawner"),
                    BlockEntityType.Builder.create(BasaltSpawnerBlockEntity::new,
                            ModBlocks.BASALT_SPAWNER).build());

    public static void registerBlockEntities() {
        MystbornHorizons.LOGGER.info("Registering Block Entities for " + MystbornHorizons.MOD_ID);
    }
}