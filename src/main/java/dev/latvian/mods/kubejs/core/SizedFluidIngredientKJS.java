package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import me.textrue.kubejs.fabric.thirdparty.fluids.FluidStack;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.FluidIngredient;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.SizedFluidIngredient;

@RemapPrefixForJS("kjs$")
public interface SizedFluidIngredientKJS extends Replaceable, FluidMatch {
	default SizedFluidIngredient kjs$self() {
		return (SizedFluidIngredient) (Object) this;
	}

	@Override
	default Object replaceThisWith(Context cx, Object with) {
		var ingredient = FluidWrapper.wrapIngredient(RegistryAccessContainer.of(cx), with);

		if (!ingredient.equals(kjs$self().ingredient())) {
			return new SizedFluidIngredient(ingredient, kjs$self().amount());
		}

		return this;
	}

	@Override
	default boolean matches(Context cx, FluidStack s, boolean exact) {
		return ((FluidMatch) kjs$self().ingredient()).matches(cx, s, exact);
	}

	@Override
	default boolean matches(Context cx, FluidIngredient in, boolean exact) {
		return ((FluidMatch) kjs$self().ingredient()).matches(cx, in, exact);
	}
}
