package me.textrue.kubejs.fabric.thirdparty.ingredients;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.stream.Stream;

import me.textrue.kubejs.fabric.thirdparty.registries.ThirdPartyRegistry;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/** Ingredient that matches everything from the first ingredient that is not included in the second ingredient */
public record DifferenceIngredient(Ingredient base, Ingredient subtracted) implements ICustomIngredient {
	public static final MapCodec<DifferenceIngredient> CODEC = RecordCodecBuilder.mapCodec(
		builder -> builder
			.group(
				Ingredient.CODEC.fieldOf("base").forGetter(DifferenceIngredient::base),
				Ingredient.CODEC.fieldOf("subtracted").forGetter(DifferenceIngredient::subtracted))
			.apply(builder, DifferenceIngredient::new));
	public static final CustomIngredientSerializer<DifferenceIngredient> SERIALIZER = (CustomIngredientSerializer<DifferenceIngredient>) CustomIngredientSerializer.get(ThirdPartyRegistry.id("difference"));

	@Override
	public Stream<ItemStack> getItems() {
		return Stream.of(base.getItems()).filter(subtracted.negate());
	}

	@Override
	public boolean test(ItemStack stack) {
		return base.test(stack) && !subtracted.test(stack);
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		return getItems().toList();
	}

	@Override
	public boolean requiresTesting() {
		return false;
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean isSimple() {
		return base.kjs$isSimple() && subtracted.kjs$isSimple();
	}

	@Override
	public IngredientType<?> getType() {
		return ThirdPartyRegistry.DIFFERENCE_INGREDIENT_TYPE;
	}

	/**
	 * Gets the difference from the two ingredients
	 *
	 * @param base       Ingredient the item must match
	 * @param subtracted Ingredient the item must not match
	 * @return Ingredient that {@code base} anything in base that is not in {@code subtracted}
	 */
	public static Ingredient of(Ingredient base, Ingredient subtracted) {
		return new DifferenceIngredient(base, subtracted).toVanilla();
	}
}
