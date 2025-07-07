package dev.latvian.mods.kubejs.ingredient;

import me.textrue.kubejs.fabric.helper.RegistryHelper;
import me.textrue.kubejs.fabric.thirdparty.ingredients.IngredientType;
import me.textrue.kubejs.fabric.thirdparty.registries.ThirdPartyRegistries;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

public interface KubeJSIngredients {
	//DeferredRegister<IngredientType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, KubeJS.MOD_ID);

	Supplier<IngredientType<WildcardIngredient>> WILDCARD = RegistryHelper.registerIngredientType("wildcard", () -> new IngredientType<>(WildcardIngredient.CODEC, WildcardIngredient.STREAM_CODEC));
	Supplier<IngredientType<NamespaceIngredient>> NAMESPACE = RegistryHelper.registerIngredientType("namespace", () -> new IngredientType<>(NamespaceIngredient.CODEC, NamespaceIngredient.STREAM_CODEC));
	Supplier<IngredientType<RegExIngredient>> REGEX = RegistryHelper.registerIngredientType("regex", () -> new IngredientType<>(RegExIngredient.CODEC, RegExIngredient.STREAM_CODEC));
	Supplier<IngredientType<CreativeTabIngredient>> CREATIVE_TAB = RegistryHelper.registerIngredientType("creative_tab", () -> new IngredientType<>(CreativeTabIngredient.CODEC, CreativeTabIngredient.STREAM_CODEC));

	static void init() {
		RegistryHelper.INGREDIENTS.forEach((id, ingredientType) -> {
			Registry.register(ThirdPartyRegistries.INGREDIENT_TYPES, id, ingredientType);
		});
	}
}
