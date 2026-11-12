package me.aleksilassila.litematica.printer.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

public final class PotionStackUtils {
    private PotionStackUtils() {
    }

    public static ItemStack createWaterPotionStack() {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
    }

    public static boolean isWaterPotion(ItemStack stack) {
        if (stack == null || !stack.is(Items.POTION)) {
            return false;
        }
        return PotionUtils.getPotion(stack) == Potions.WATER;
    }
}
