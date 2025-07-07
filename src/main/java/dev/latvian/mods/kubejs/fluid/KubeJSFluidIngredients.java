package dev.latvian.mods.kubejs.fluid;

import me.textrue.kubejs.fabric.helper.RegistryHelper;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.FluidIngredientType;
import me.textrue.kubejs.fabric.thirdparty.registries.ThirdPartyRegistries;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

public interface KubeJSFluidIngredients {
	//DeferredRegister<FluidIngredientType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.FLUID_INGREDIENT_TYPES, KubeJS.MOD_ID);

	Supplier<FluidIngredientType<?>> REGEX = RegistryHelper.registerFluidIngredientType("regex", () -> new FluidIngredientType<>(RegExFluidIngredient.CODEC, RegExFluidIngredient.STREAM_CODEC));
	Supplier<FluidIngredientType<?>> NAMESPACE = RegistryHelper.registerFluidIngredientType("namespace", () -> new FluidIngredientType<>(NamespaceFluidIngredient.CODEC, NamespaceFluidIngredient.STREAM_CODEC));

	static void init() {
		RegistryHelper.FLUID_INGREDIENTS.forEach((id, fluidIngredientType) -> {
			Registry.register(ThirdPartyRegistries.FLUID_INGREDIENT_TYPES, id, fluidIngredientType);
		});
	}
}
