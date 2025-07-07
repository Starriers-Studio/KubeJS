package me.textrue.kubejs.fabric.helper;

import dev.latvian.mods.kubejs.KubeJS;
import me.textrue.kubejs.fabric.thirdparty.holdersets.HolderSetType;
import me.textrue.kubejs.fabric.thirdparty.ingredients.IngredientType;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.FluidIngredientType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RegistryHelper {
	public static Map<ResourceLocation, RecipeSerializer<?>> RECIPE_SERIALIZERS = new LinkedHashMap<>();
	public static Map<ResourceLocation, MenuType<?>> MENUS = new LinkedHashMap<>();
	public static Map<ResourceLocation, IngredientType<?>> INGREDIENTS = new LinkedHashMap<>();
	public static Map<ResourceLocation, FluidIngredientType<?>> FLUID_INGREDIENTS = new LinkedHashMap<>();
	public static Map<ResourceLocation, CreativeModeTab> CREATIVE_MODE_TABS = new LinkedHashMap<>();
	public static Map<ResourceLocation, RuleTestType<?>> RULE_TESTS = new LinkedHashMap<>();
	public static Map<ResourceLocation, HolderSetType> HOLDER_SETS = new LinkedHashMap<>();

	public static <T extends RecipeSerializer<?>> Supplier<T> registerRecipeSerializer(String id, Supplier<T> recipeSerializer) {
		RECIPE_SERIALIZERS.put(KubeJS.id(id), recipeSerializer.get());
		return recipeSerializer;
	}

	public static <T extends MenuType<?>> Supplier<T> registerMenu(String id, Supplier<T> menu) {
		MENUS.put(KubeJS.id(id), menu.get());
		return menu;
	}

	public static <T extends IngredientType<?>> Supplier<T> registerIngredientType(String id, Supplier<T> ingredientType) {
		INGREDIENTS.put(KubeJS.id(id), ingredientType.get());
		return ingredientType;
	}

	public static <T extends FluidIngredientType<?>> Supplier<T> registerFluidIngredientType(String id, Supplier<T> ingredientType) {
		FLUID_INGREDIENTS.put(KubeJS.id(id), ingredientType.get());
		return ingredientType;
	}

	public static <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String id, Supplier<T> tab) {
		CREATIVE_MODE_TABS.put(KubeJS.id(id), tab.get());
		return tab;
	}

	public static <T extends RuleTestType<?>> Supplier<T> registerRuleTest(String id, Supplier<T> ruleTest) {
		RULE_TESTS.put(KubeJS.id(id), ruleTest.get());
		return ruleTest;
	}

	public static <T extends HolderSetType> Holder<T> registerHolderSet(String id, Supplier<T> holderSet) {
		HOLDER_SETS.put(KubeJS.id(id), holderSet.get());
		return Holder.direct(holderSet.get());
	}


}
