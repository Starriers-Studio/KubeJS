package me.textrue.kubejs.fabric.thirdparty.registries;

import com.mojang.serialization.Lifecycle;
import me.textrue.kubejs.fabric.thirdparty.fluids.FluidType;
import me.textrue.kubejs.fabric.thirdparty.holdersets.HolderSetType;
import me.textrue.kubejs.fabric.thirdparty.ingredients.IngredientType;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.FluidIngredientType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;

public class ThirdPartyRegistries {
	public static final Registry<HolderSetType> HOLDER_SET_TYPES = createRegistry(Keys.HOLDER_SET_TYPES);
	public static final Registry<IngredientType<?>> INGREDIENT_TYPES = createRegistry(Keys.INGREDIENT_TYPES);
	public static final Registry<FluidIngredientType<?>> FLUID_INGREDIENT_TYPES = createRegistry(Keys.FLUID_INGREDIENT_TYPES);
	public static final Registry<FluidType> FLUID_TYPES = createRegistry(Keys.FLUID_TYPES);

	public static final class Keys {
		// NeoForge
		public static final ResourceKey<Registry<HolderSetType>> HOLDER_SET_TYPES = key("holder_set_type");
		public static final ResourceKey<Registry<IngredientType<?>>> INGREDIENT_TYPES = key("ingredient_serializer");
		public static final ResourceKey<Registry<FluidIngredientType<?>>> FLUID_INGREDIENT_TYPES = key("fluid_ingredient_type");
		public static final ResourceKey<Registry<FluidType>> FLUID_TYPES = key("fluid_type");

		private static <T> ResourceKey<Registry<T>> key(String name) {
			return ResourceKey.createRegistryKey(ThirdPartyRegistry.id(name));
		}
	}

	private static <T> Registry<T> createRegistry(ResourceKey<? extends Registry<T>> resourceKey) {
		final WritableRegistry<T> registry = new MappedRegistry<>(resourceKey, Lifecycle.stable(), false);
		return FabricRegistryBuilder.from(registry).buildAndRegister();
	}
}
