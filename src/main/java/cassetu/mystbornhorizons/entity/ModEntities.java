package cassetu.mystbornhorizons.entity;

import cassetu.mystbornhorizons.MystbornHorizons;
import cassetu.mystbornhorizons.entity.custom.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<MantisEntity> MANTIS = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MystbornHorizons.MOD_ID, "mantis"),
            EntityType.Builder.create(MantisEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1f, 2.5f).build());

    public static final EntityType<CopperBulbEntity> COPPERBULB = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MystbornHorizons.MOD_ID, "copperbulb"),
            EntityType.Builder.create(CopperBulbEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.7f, 1.5f).build());

    public static final EntityType<IceSpiderEntity> ICESPIDER = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MystbornHorizons.MOD_ID, "icespider"),
            EntityType.Builder.create(IceSpiderEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.9f, 0.75f).build());

    public static final EntityType<BasaltHowlerEntity> BASALTHOWLER = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MystbornHorizons.MOD_ID, "basalthowler"),
            EntityType.Builder.create(BasaltHowlerEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1f, 1.65f).build());

    public static final EntityType<TomahawkProjectileEntity> TOMAHAWK = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(MystbornHorizons.MOD_ID, "tomahawk"),
            EntityType.Builder.<TomahawkProjectileEntity>create(TomahawkProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 1.15f).build());

    public static void registerModEntities() {
        MystbornHorizons.LOGGER.info("Registering Mod Entities for " + MystbornHorizons.MOD_ID);
    }
}
