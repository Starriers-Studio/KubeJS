package me.textrue.kubejs.fabric.thirdparty.ingredients;

import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import me.textrue.kubejs.fabric.thirdparty.registries.ThirdPartyRegistry;
import me.textrue.kubejs.fabric.thirdparty.util.ThirdPartyExtraCodecs;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/** Ingredient that matches if any of the child ingredients match */
public record CompoundIngredient(List<Ingredient> children) implements ICustomIngredient {

	public CompoundIngredient {
		if (children.isEmpty()) {
			// Empty ingredients are always represented as Ingredient.EMPTY.
			throw new IllegalArgumentException("Compound ingredient must have at least one child");
		}
	}
	public static final MapCodec<CompoundIngredient> CODEC = ThirdPartyExtraCodecs.aliasedFieldOf(IngredientCodecs.LIST_CODEC_NONEMPTY, "children", "ingredients").xmap(CompoundIngredient::new, CompoundIngredient::children);
	public static final CustomIngredientSerializer<CompoundIngredient> SERIALIZER = (CustomIngredientSerializer<CompoundIngredient>) CustomIngredientSerializer.get(ThirdPartyRegistry.id("compound"));

	/** Creates a compound ingredient from the given list of ingredients */
	public static Ingredient of(Ingredient... children) {
		if (children.length == 0)
			return Ingredient.EMPTY;
		if (children.length == 1)
			return children[0];

		return new CompoundIngredient(List.of(children)).toVanilla();
	}

	@Override
	public Stream<ItemStack> getItems() {
		return children.stream().flatMap(child -> Arrays.stream(child.getItems()));
	}

	@Override
	public boolean test(ItemStack stack) {
		for (var child : children) {
			if (child.test(stack)) {
				return true;
			}
		}
		return false;
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
		for (var child : children) {
			if (!child.kjs$isSimple()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public IngredientType<?> getType() {
		return ThirdPartyRegistry.COMPOUND_INGREDIENT_TYPE;
	}
}
