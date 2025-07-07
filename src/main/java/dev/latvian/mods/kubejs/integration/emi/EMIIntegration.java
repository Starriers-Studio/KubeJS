package dev.latvian.mods.kubejs.integration.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import me.textrue.kubejs.fabric.thirdparty.fluids.FluidStack;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.FluidIngredient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Predicate;

public class EMIIntegration {
	public static EmiStack fluid(FluidStack stack) {
		return EmiStack.of(stack.getFluid(), stack.getComponentsPatch(), stack.getAmount());
	}

	public static EmiIngredient fluidIngredient(FluidIngredient ingredient) {
		return EmiIngredient.of(Arrays.stream(ingredient.getStacks()).map(EMIIntegration::fluid).toList());
	}

	public static Predicate<EmiStack> predicate(ItemPredicate ingredient) {
		return emiStack -> {
			var is = emiStack.getItemStack();
			return !is.isEmpty() && ingredient.test(is);
		};
	}

	public static Predicate<EmiStack> predicate(FluidIngredient ingredient) {
		var set = new HashSet<>(Arrays.stream(ingredient.getStacks()).map(EMIIntegration::fluid).toList());
		return set::contains;
	}
}
