package cassetu.mystbornhorizons.item;

import cassetu.mystbornhorizons.effect.ModEffects;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class ModFoodComponents {
    public static final FoodComponent CAULIFLOWER = new FoodComponent.Builder().alwaysEdible().nutrition(3).saturationModifier(0.25f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 200), 0.3f).build();
    public static final FoodComponent SALMON_NIGIRI = new FoodComponent.Builder().snack().alwaysEdible().nutrition(3).saturationModifier(0.15f)
            .statusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 600), 0.3f).build();
    public static final FoodComponent COD_NIGIRI = new FoodComponent.Builder().snack().alwaysEdible().nutrition(3).saturationModifier(0.15f)
            .statusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 100), 0.3f).build();
    public static final FoodComponent WAFFLE = new FoodComponent.Builder().alwaysEdible().nutrition(3).saturationModifier(0.25f)
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100), 0.3f).build();
    public static final FoodComponent CHICKEN_NUGGETS = new FoodComponent.Builder().alwaysEdible().snack().nutrition(1).saturationModifier(0.05f)
            .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20), 0.1f).build();
    public static final FoodComponent VEGGIE_SANDWICH = new FoodComponent.Builder().alwaysEdible().nutrition(5).saturationModifier(0.5f)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 80), 1.0f).build();
    public static final FoodComponent POWER_CORE = new FoodComponent.Builder().nutrition(3).saturationModifier(0.3f)
            .statusEffect(new StatusEffectInstance(ModEffects.SLIMEY, 70), 0.3f)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 80, 2), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 30), 0.3f)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 20), 0.3f)
            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 1), 1.0f)
            .alwaysEdible()
            .build();

    public static final FoodComponent HONEY_BERRY = new FoodComponent.Builder().nutrition(2).saturationModifier(0.15f)
            .snack()
            .statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 0, 2), 0.3f)
            .build();

    public static final FoodComponent ROOT = new FoodComponent.Builder().alwaysEdible().nutrition(1).saturationModifier(5f)
            .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 140), 0.5f)
            .build();
}
