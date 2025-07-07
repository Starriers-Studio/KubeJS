package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.special.ShapedKubeJSRecipe;
import dev.latvian.mods.kubejs.recipe.special.ShapelessKubeJSRecipe;
import me.textrue.kubejs.fabric.helper.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public interface KubeJSRecipeSerializers {
	//DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_SERIALIZER, KubeJS.MOD_ID);

	Supplier<RecipeSerializer<?>> SHAPED = RegistryHelper.registerRecipeSerializer("shaped", ShapedKubeJSRecipe.SerializerKJS::new);
	Supplier<RecipeSerializer<?>> SHAPELESS = RegistryHelper.registerRecipeSerializer("shapeless", ShapelessKubeJSRecipe.SerializerKJS::new);

	static void init() {
		RegistryHelper.RECIPE_SERIALIZERS.forEach((id, recipeSerializer) -> {
			Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, recipeSerializer);
		});
	}
}
