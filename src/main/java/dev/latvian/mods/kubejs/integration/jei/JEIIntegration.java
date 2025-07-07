package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.rhino.Context;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.FluidIngredient;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.fabric.constants.FabricTypes;
import mezz.jei.api.ingredients.IIngredientType;
import org.jetbrains.annotations.Nullable;

public class JEIIntegration {
	@Nullable
	public static IIngredientType<?> typeOf(RecipeViewerEntryType type) {
		if (type == RecipeViewerEntryType.ITEM) {
			return VanillaTypes.ITEM_STACK;
		} else if (type == RecipeViewerEntryType.FLUID) {
			return FabricTypes.FLUID_STACK;
		} else {
			return null;
		}
	}

	public static Object[] getEntries(RecipeViewerEntryType type, Context cx, Object filter) {
		if (type == RecipeViewerEntryType.ITEM) {
			return ((ItemPredicate) type.wrapPredicate(cx, filter)).kjs$getStackArray();
		} else if (type == RecipeViewerEntryType.FLUID) {
			return ((FluidIngredient) type.wrapPredicate(cx, filter)).getStacks();
		} else {
			return new Object[0];
		}
	}
}