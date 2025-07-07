package me.textrue.kubejs.fabric.helper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Arrays;

public class ItemStackHelper {
	public static boolean isComponentsPatchEmpty(ItemStack stack) {
        return stack.isEmpty() || stack.getComponentsPatch().isEmpty();
    }

	public static int getEntityLifespan(ItemStack stack, Level level) {
		return 6000;
	}

	public static ItemStack[] createEmptyStackArray(int size) {
		ItemStack[] stacks = new ItemStack[size];
		Arrays.fill(stacks, ItemStack.EMPTY);
		return stacks;
	}
}
