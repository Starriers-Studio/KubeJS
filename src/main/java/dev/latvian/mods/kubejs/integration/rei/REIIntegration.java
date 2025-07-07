package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.textrue.kubejs.fabric.helper.FluidStackHelper;
import me.textrue.kubejs.fabric.thirdparty.fluids.FluidStack;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.FluidIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class REIIntegration {
	// TODO: Re-add RVEType -> EntryType remapping registry

	@Nullable
	public static EntryType<?> typeOf(RecipeViewerEntryType type) {
		if (type == RecipeViewerEntryType.ITEM) {
			return VanillaEntryTypes.ITEM;
		} else if (type == RecipeViewerEntryType.FLUID) {
			return VanillaEntryTypes.FLUID;
		} else {
			return null;
		}
	}

	public static EntryStack<?> stackOf(Context cx, RecipeViewerEntryType type, Object from) {
		if (type == RecipeViewerEntryType.ITEM) {
			var in = (ItemStack) type.wrapEntry(cx, from);
			return EntryStacks.of(in);
		} else if (type == RecipeViewerEntryType.FLUID) {
			var in = (FluidStack) type.wrapEntry(cx, from);
			return EntryStacks.of(FluidStackHelper.thirdPartyToArch(in));
		} else {
			((KubeJSContext) cx).getConsole().error("Currently custom type '" + type.id + "' isn't supported");
			return EntryStack.empty();
		}
	}

	public static EntryIngredient fluidIngredient(FluidIngredient ingredient) {
		return EntryIngredient.of(Arrays.stream(ingredient.getStacks()).map(FluidStackHelper::thirdPartyToArch).map(EntryStacks::of).toList());
	}

	public static EntryIngredient ingredientOf(Context cx, RecipeViewerEntryType type, Object from) {
		if (type == RecipeViewerEntryType.ITEM) {
			var in = (ItemPredicate) type.wrapPredicate(cx, from);
			return in instanceof Ingredient i ? EntryIngredients.ofIngredient(i) : EntryIngredients.ofItemStacks(Arrays.asList(in.kjs$getStackArray()));
		} else if (type == RecipeViewerEntryType.FLUID) {
			return fluidIngredient((FluidIngredient) type.wrapPredicate(cx, from));
		} else {
			((KubeJSContext) cx).getConsole().error("Currently custom type '" + type.id + "' isn't supported");
			return EntryIngredient.empty();
		}
	}
}