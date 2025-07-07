package me.textrue.kubejs.fabric.thirdparty.ingredients;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import me.textrue.kubejs.fabric.thirdparty.registries.ThirdPartyRegistries;
import me.textrue.kubejs.fabric.thirdparty.util.ThirdPartyExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public class CraftingHelper {
	public static Codec<Ingredient> makeIngredientCodec(boolean allowEmpty) {
		var listCodec = Codec.lazyInitialized(() -> allowEmpty ? IngredientCodecs.LIST_CODEC : IngredientCodecs.LIST_CODEC_NONEMPTY);
		return Codec.either(listCodec, makeIngredientMapCodec().codec())
			.xmap(either -> either.map(list -> {
				// Use CompoundIngredient.of(...) to convert empty ingredients to Ingredient.EMPTY
				return CompoundIngredient.of(list.toArray(Ingredient[]::new));
			}, i -> i), ingredient -> {
				if (ingredient.kjs$isCustom()) {
					if (ingredient.getCustomIngredient() instanceof CompoundIngredient compound) {
						// Use [] syntax for CompoundIngredients.
						return Either.left(compound.children());
					}
				} else if (ingredient.kjs$getValues().length != 1) {
					// Use [] syntax for vanilla ingredients that either 0 or 2+ values.
					return Either.left(Stream.of(ingredient.kjs$getValues()).map(v -> Ingredient.fromValues(Stream.of(v))).toList());
				}
				// Else use {} syntax.
				return Either.right(ingredient);
			});
	}

	public static MapCodec<Ingredient> makeIngredientMapCodec() {
		// Dispatch codec for custom ingredient types, else fallback to vanilla ingredient codec.
		return ThirdPartyExtraCodecs.<IngredientType<?>, ICustomIngredient, Ingredient.Value>dispatchMapOrElse(
				ThirdPartyRegistries.INGREDIENT_TYPES.byNameCodec(),
				ICustomIngredient::getType,
				IngredientType::codec,
				IngredientCodecs.VALUE_MAP_CODEC)
			.xmap(either -> either.map(ICustomIngredient::toVanilla, v -> Ingredient.fromValues(Stream.of(v))), ingredient -> {
				if (!ingredient.kjs$isCustom()) {
					var values = ingredient.kjs$getValues();
					if (values.length == 1) {
						return Either.right(values[0]);
					}
					// Convert vanilla ingredients with 2+ values to a CompoundIngredient. Empty ingredients are not allowed here.
					return Either.left(new CompoundIngredient(Stream.of(ingredient.kjs$getValues()).map(v -> Ingredient.fromValues(Stream.of(v))).toList()));
				}
				return Either.left(((ICustomIngredient) ingredient.getCustomIngredient()));
			})
			.validate(ingredient -> {
				if (!ingredient.kjs$isCustom() && ingredient.kjs$getValues().length == 0) {
					return DataResult.error(() -> "Cannot serialize empty ingredient using the map codec");
				}
				return DataResult.success(ingredient);
			});
	}
}
