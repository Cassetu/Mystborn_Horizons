package cassetu.mystbornhorizons.util;

import cassetu.mystbornhorizons.item.ModItems;
import cassetu.mystbornhorizons.world.HavenicaDefeatState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;

public class EnhancedMobEquipment {

    public static void equipPostHavenicaMob(MobEntity mob, ServerWorld world) {
        HavenicaDefeatState state = HavenicaDefeatState.getOrCreate(world);

        if (!state.isHavenicaDefeated()) {
            return;
        }

        Random random = mob.getRandom();
        float enhancementChance = calculateEnhancementChance(world, state.getDefeatTime());

        if (random.nextFloat() < enhancementChance) {
            equipEnhancedArmor(mob, world, random, false);
            applyPostDefeatBuffs(mob, random);
            preventEquipmentDrops(mob);
        }
    }

    public static void equipCursedMob(MobEntity mob, ServerWorld world) {
        Random random = mob.getRandom();
        equipEnhancedArmor(mob, world, random, true);
        applyCursedBuffs(mob, random);
        preventEquipmentDrops(mob);
    }

    private static void preventEquipmentDrops(MobEntity mob) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            mob.setEquipmentDropChance(slot, 0.0f);
        }
    }

    private static float calculateEnhancementChance(ServerWorld world, long defeatTime) {
        long timeSinceDefeat = world.getTime() - defeatTime;
        long daysPassedTicks = timeSinceDefeat / 24000;

        return Math.min(0.8f, 0.3f + (daysPassedTicks * 0.05f));
    }

    private static void equipEnhancedArmor(MobEntity mob, ServerWorld world, Random random, boolean isCursed) {
        equipSpecificWeapon(mob, world, random);

        if (isCursed) {
            setCursedName(mob);
        } else {
            setInfectedName(mob);
        }

        if (random.nextFloat() < 0.8f) {
            ItemStack helmet = createEnhancedHelmet(random, world);
            mob.equipStack(EquipmentSlot.HEAD, helmet);
        }

        if (random.nextFloat() < 0.9f) {
            ItemStack chestplate = createEnhancedChestplate(random, world);
            mob.equipStack(EquipmentSlot.CHEST, chestplate);
        }

        if (random.nextFloat() < 0.7f) {
            ItemStack leggings = createEnhancedLeggings(random, world);
            mob.equipStack(EquipmentSlot.LEGS, leggings);
        }

        if (random.nextFloat() < 0.8f) {
            ItemStack boots = createEnhancedBoots(random, world);
            mob.equipStack(EquipmentSlot.FEET, boots);
        }

        if (random.nextFloat() < 0.3f) {
            ItemStack offhand = createSpecialOffhandItem(random);
            mob.equipStack(EquipmentSlot.OFFHAND, offhand);
        }
    }

    private static void setInfectedName(MobEntity mob) {
        String mobTypeName = mob.getType().getName().getString();
        String infectedName = "§2Infected §r" + mobTypeName;
        mob.setCustomName(net.minecraft.text.Text.literal(infectedName));
        mob.setCustomNameVisible(true);
    }

    private static void setCursedName(MobEntity mob) {
        String mobTypeName = mob.getType().getName().getString();
        String cursedName = "§4Cursed §r" + mobTypeName;
        mob.setCustomName(net.minecraft.text.Text.literal(cursedName));
        mob.setCustomNameVisible(true);
    }

    private static ItemStack createEnhancedHelmet(Random random, ServerWorld world) {
        ItemStack helmet;

        float armorRoll = random.nextFloat();
        if (armorRoll < 0.5f) {
            helmet = new ItemStack(Items.DIAMOND_HELMET);
        } else {
            helmet = new ItemStack(Items.IRON_HELMET);
        }

        addEmeraldTrim(helmet, world);

        try {
            ItemEnchantmentsComponent.Builder enchantments = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);

            if (random.nextFloat() < 0.8f) {
                RegistryEntry<Enchantment> protection = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.PROTECTION).orElse(null);
                if (protection != null) {
                    enchantments.add(protection, 1 + random.nextInt(2));
                }
            }
            if (random.nextFloat() < 0.4f) {
                RegistryEntry<Enchantment> unbreaking = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.UNBREAKING).orElse(null);
                if (unbreaking != null) {
                    enchantments.add(unbreaking, 1 + random.nextInt(2));
                }
            }
            if (random.nextFloat() < 0.3f) {
                RegistryEntry<Enchantment> respiration = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.RESPIRATION).orElse(null);
                if (respiration != null) {
                    enchantments.add(respiration, 1 + random.nextInt(2));
                }
            }

            helmet.set(DataComponentTypes.ENCHANTMENTS, enchantments.build());
        } catch (Exception e) {
            System.out.println("Error adding helmet enchantments: " + e.getMessage());
        }

        return helmet;
    }

    private static ItemStack createEnhancedChestplate(Random random, ServerWorld world) {
        ItemStack chestplate;

        float armorRoll = random.nextFloat();
        if (armorRoll < 0.6f) {
            chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
        } else {
            chestplate = new ItemStack(Items.IRON_CHESTPLATE);
        }

        addEmeraldTrim(chestplate, world);

        try {
            ItemEnchantmentsComponent.Builder enchantments = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);

            if (random.nextFloat() < 0.9f) {
                RegistryEntry<Enchantment> protection = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.PROTECTION).orElse(null);
                if (protection != null) {
                    enchantments.add(protection, 1 + random.nextInt(2));
                }
            }
            if (random.nextFloat() < 0.5f) {
                RegistryEntry<Enchantment> unbreaking = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.UNBREAKING).orElse(null);
                if (unbreaking != null) {
                    enchantments.add(unbreaking, 1 + random.nextInt(2));
                }
            }

            chestplate.set(DataComponentTypes.ENCHANTMENTS, enchantments.build());
        } catch (Exception e) {
            System.out.println("Error adding chestplate enchantments: " + e.getMessage());
        }

        return chestplate;
    }

    private static ItemStack createEnhancedLeggings(Random random, ServerWorld world) {
        ItemStack leggings;

        float armorRoll = random.nextFloat();
        if (armorRoll < 0.5f) {
            leggings = new ItemStack(Items.DIAMOND_LEGGINGS);
        } else {
            leggings = new ItemStack(Items.IRON_LEGGINGS);
        }

        addEmeraldTrim(leggings, world);

        try {
            ItemEnchantmentsComponent.Builder enchantments = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);

            if (random.nextFloat() < 0.8f) {
                RegistryEntry<Enchantment> protection = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.PROTECTION).orElse(null);
                if (protection != null) {
                    enchantments.add(protection, 1 + random.nextInt(2));
                }
            }
            if (random.nextFloat() < 0.4f) {
                RegistryEntry<Enchantment> unbreaking = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.UNBREAKING).orElse(null);
                if (unbreaking != null) {
                    enchantments.add(unbreaking, 1 + random.nextInt(2));
                }
            }

            leggings.set(DataComponentTypes.ENCHANTMENTS, enchantments.build());
        } catch (Exception e) {
            System.out.println("Error adding leggings enchantments: " + e.getMessage());
        }

        return leggings;
    }

    private static ItemStack createEnhancedBoots(Random random, ServerWorld world) {
        ItemStack boots;

        float armorRoll = random.nextFloat();
        if (armorRoll < 0.4f) {
            boots = new ItemStack(Items.DIAMOND_BOOTS);
        } else {
            boots = new ItemStack(Items.IRON_BOOTS);
        }

        addEmeraldTrim(boots, world);

        try {
            ItemEnchantmentsComponent.Builder enchantments = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);

            if (random.nextFloat() < 0.7f) {
                RegistryEntry<Enchantment> protection = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.PROTECTION).orElse(null);
                if (protection != null) {
                    enchantments.add(protection, 1 + random.nextInt(2));
                }
            }
            if (random.nextFloat() < 0.6f) {
                RegistryEntry<Enchantment> featherFalling = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.FEATHER_FALLING).orElse(null);
                if (featherFalling != null) {
                    enchantments.add(featherFalling, 1 + random.nextInt(2));
                }
            }
            if (random.nextFloat() < 0.4f) {
                RegistryEntry<Enchantment> depthStrider = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.DEPTH_STRIDER).orElse(null);
                if (depthStrider != null) {
                    enchantments.add(depthStrider, 1 + random.nextInt(2));
                }
            }
            if (random.nextFloat() < 0.3f) {
                RegistryEntry<Enchantment> unbreaking = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.UNBREAKING).orElse(null);
                if (unbreaking != null) {
                    enchantments.add(unbreaking, 1 + random.nextInt(2));
                }
            }

            boots.set(DataComponentTypes.ENCHANTMENTS, enchantments.build());
        } catch (Exception e) {
            System.out.println("Error adding boots enchantments: " + e.getMessage());
        }

        return boots;
    }

    private static void addEmeraldTrim(ItemStack armor, ServerWorld world) {
        try {
            RegistryEntry<net.minecraft.item.trim.ArmorTrimMaterial> emerald = world.getRegistryManager()
                    .get(net.minecraft.registry.RegistryKeys.TRIM_MATERIAL)
                    .getEntry(net.minecraft.util.Identifier.of("emerald"))
                    .orElse(null);

            RegistryEntry<net.minecraft.item.trim.ArmorTrimPattern> pattern = world.getRegistryManager()
                    .get(net.minecraft.registry.RegistryKeys.TRIM_PATTERN)
                    .getEntry(net.minecraft.util.Identifier.of("wild"))
                    .orElse(null);

            if (emerald != null && pattern != null) {
                net.minecraft.item.trim.ArmorTrim trim = new net.minecraft.item.trim.ArmorTrim(emerald, pattern);
                armor.set(DataComponentTypes.TRIM, trim);
            }
        } catch (Exception e) {
            System.out.println("Error adding armor trim: " + e.getMessage());
        }
    }

    private static void equipSpecificWeapon(MobEntity mob, ServerWorld world, Random random) {
        String mobType = mob.getClass().getSimpleName().toLowerCase();

        if (mobType.contains("pillager") || mobType.contains("vindicator")) {
            ItemStack axe;
            if (random.nextFloat() < 0.3f) {
                axe = new ItemStack(Items.DIAMOND_AXE);
            } else if (random.nextFloat() < 0.6f) {
                axe = new ItemStack(Items.IRON_AXE);
            } else {
                axe = new ItemStack(Items.STONE_AXE);
            }
            mob.equipStack(EquipmentSlot.MAINHAND, axe);
        } else if (mobType.contains("skeleton") || mobType.contains("bogged")) {
            ItemStack bow = new ItemStack(Items.BOW);
            mob.equipStack(EquipmentSlot.MAINHAND, bow);
        } else if (mobType.contains("zombie") || mobType.contains("husk") || mobType.contains("drowned")) {
            ItemStack weapon;
            float weaponRoll = random.nextFloat();
            if (weaponRoll < 0.2f) {
                weapon = new ItemStack(Items.IRON_SWORD);
            } else if (weaponRoll < 0.4f) {
                weapon = new ItemStack(Items.STONE_SWORD);
            } else {
                weapon = new ItemStack(Items.WOODEN_SWORD);
            }
            mob.equipStack(EquipmentSlot.MAINHAND, weapon);
        } else if (mobType.contains("spider")) {
            if (random.nextFloat() < 0.3f) {
                ItemStack weapon = new ItemStack(Items.STONE_SWORD);
                mob.equipStack(EquipmentSlot.MAINHAND, weapon);
            }
        } else if (mobType.contains("enderman")) {
            if (random.nextFloat() < 0.4f) {
                ItemStack weapon;
                if (random.nextFloat() < 0.5f) {
                    weapon = new ItemStack(Items.IRON_SWORD);
                } else {
                    weapon = new ItemStack(Items.STONE_SWORD);
                }
                mob.equipStack(EquipmentSlot.MAINHAND, weapon);
            }
        } else if (mobType.contains("creeper")) {
            if (random.nextFloat() < 0.15f) {
                ItemStack weapon = new ItemStack(Items.WOODEN_SWORD);
                mob.equipStack(EquipmentSlot.MAINHAND, weapon);
            }
        } else if (mobType.contains("witch")) {
            if (random.nextFloat() < 0.6f) {
                ItemStack weapon;
                if (random.nextFloat() < 0.3f) {
                    weapon = new ItemStack(Items.SPLASH_POTION);
                } else {
                    weapon = new ItemStack(Items.STICK);
                }
                mob.equipStack(EquipmentSlot.MAINHAND, weapon);
            }
        } else if (mobType.contains("blaze")) {
            if (random.nextFloat() < 0.5f) {
                ItemStack weapon = new ItemStack(Items.BLAZE_ROD);
                mob.equipStack(EquipmentSlot.MAINHAND, weapon);
            }
        }
    }

    private static ItemStack createSpecialOffhandItem(Random random) {
        float roll = random.nextFloat();

        if (roll < 0.4f) {
            return new ItemStack(Items.SHIELD);
        } else if (roll < 0.6f) {
            return new ItemStack(Items.TOTEM_OF_UNDYING);
        } else if (roll < 0.8f) {
            return new ItemStack(ModItems.ROOT);
        } else {
            return new ItemStack(Items.POISONOUS_POTATO);
        }
    }

    private static void applyPostDefeatBuffs(MobEntity mob, Random random) {
        if (random.nextFloat() < 0.6f) {
            float healthMultiplier = 1.5f + (random.nextFloat() * 1.0f);
            if (mob.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH) != null) {
                mob.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH)
                        .setBaseValue(mob.getMaxHealth() * healthMultiplier);
                mob.setHealth(mob.getMaxHealth());
            }
        }

        if (random.nextFloat() < 0.5f) {
            float damageMultiplier = 1.2f + (random.nextFloat() * 0.5f);
            if (mob.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE) != null) {
                mob.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(mob.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE).getBaseValue() * damageMultiplier);
            }
        }
    }

    private static void applyCursedBuffs(MobEntity mob, Random random) {
        if (random.nextFloat() < 0.8f) {
            float healthMultiplier = 2.0f + (random.nextFloat() * 1.5f);
            if (mob.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH) != null) {
                mob.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH)
                        .setBaseValue(mob.getMaxHealth() * healthMultiplier);
                mob.setHealth(mob.getMaxHealth());
            }
        }

        if (random.nextFloat() < 0.7f) {
            float damageMultiplier = 1.5f + (random.nextFloat() * 1.0f);
            if (mob.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE) != null) {
                mob.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(mob.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE).getBaseValue() * damageMultiplier);
            }
        }
    }

    public static void applyCorruptionEffects(MobEntity mob, ServerWorld world) {
        if (world.getRandom().nextFloat() < 0.25f) {
            mob.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    StatusEffects.RESISTANCE,
                    2400,
                    2,
                    false,
                    true
            ));
        }

        if (world.getRandom().nextFloat() < 0.2f) {
            mob.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    StatusEffects.REGENERATION,
                    2400,
                    1,
                    false,
                    true
            ));
        }

        if (world.getRandom().nextFloat() < 0.2f) {
            mob.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    StatusEffects.GLOWING,
                    400,
                    1,
                    false,
                    true
            ));
        }

        if (world.getTime() % 20 == 0) {
            int particleCount = 2 + world.getRandom().nextInt(3);
            for (int i = 0; i < particleCount; i++) {
                double offsetX = (world.getRandom().nextDouble() - 0.5) * 1.2;
                double offsetY = world.getRandom().nextDouble() * 1.5 + 0.2;
                double offsetZ = (world.getRandom().nextDouble() - 0.5) * 1.2;

                world.spawnParticles(
                        net.minecraft.particle.ParticleTypes.SPORE_BLOSSOM_AIR,
                        mob.getX() + offsetX,
                        mob.getY() + offsetY,
                        mob.getZ() + offsetZ,
                        1,
                        0.1, 0.1, 0.1,
                        0.01
                );
            }

            if (world.getRandom().nextFloat() < 0.3f) {
                world.spawnParticles(
                        ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS,
                        mob.getX(),
                        mob.getY() + 1.2,
                        mob.getZ(),
                        8,
                        0.3, 0.3, 0.3,
                        0.02
                );
            }
        }
    }

    public static void applyCurseEffects(MobEntity mob, ServerWorld world) {
        if (world.getRandom().nextFloat() < 0.4f) {
            mob.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    StatusEffects.RESISTANCE,
                    3600,
                    3,
                    false,
                    true
            ));
        }

        if (world.getRandom().nextFloat() < 0.3f) {
            mob.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    StatusEffects.REGENERATION,
                    3600,
                    2,
                    false,
                    true
            ));
        }

        if (world.getTime() % 15 == 0) {
            int particleCount = 3 + world.getRandom().nextInt(4);
            for (int i = 0; i < particleCount; i++) {
                double offsetX = (world.getRandom().nextDouble() - 0.5) * 1.5;
                double offsetY = world.getRandom().nextDouble() * 2.0 + 0.3;
                double offsetZ = (world.getRandom().nextDouble() - 0.5) * 1.5;

                world.spawnParticles(
                        net.minecraft.particle.ParticleTypes.SOUL_FIRE_FLAME,
                        mob.getX() + offsetX,
                        mob.getY() + offsetY,
                        mob.getZ() + offsetZ,
                        1,
                        0.15, 0.15, 0.15,
                        0.02
                );
            }

            if (world.getRandom().nextFloat() < 0.4f) {
                world.spawnParticles(
                        ParticleTypes.CRIMSON_SPORE,
                        mob.getX(),
                        mob.getY() + 1.5,
                        mob.getZ(),
                        10,
                        0.4, 0.4, 0.4,
                        0.03
                );
            }
        }
    }
}