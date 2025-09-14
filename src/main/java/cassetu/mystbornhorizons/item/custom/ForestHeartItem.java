package cassetu.mystbornhorizons.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ForestHeartItem extends Item {
    public ForestHeartItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.mystbornhorizons.forest_heart.tooltip1").formatted(Formatting.GREEN));
        tooltip.add(Text.translatable("tooltip.mystbornhorizons.forest_heart.tooltip2").formatted(Formatting.DARK_GREEN));
        tooltip.add(Text.translatable("tooltip.mystbornhorizons.forest_heart.tooltip3").formatted(Formatting.RED));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true; // Makes the item have an enchanted glint
    }
}