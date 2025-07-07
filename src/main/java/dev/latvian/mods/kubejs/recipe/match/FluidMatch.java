package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.rhino.Context;
import me.textrue.kubejs.fabric.thirdparty.fluids.FluidStack;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.FluidIngredient;

public interface FluidMatch extends ReplacementMatch {
	boolean matches(Context cx, FluidStack stack, boolean exact);

	boolean matches(Context cx, FluidIngredient ingredient, boolean exact);
}
