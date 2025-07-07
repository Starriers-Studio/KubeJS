package me.textrue.kubejs.fabric.thirdparty.extensions;

import me.textrue.kubejs.fabric.thirdparty.ingredients.ICustomIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface IngredientExtension {
	private Ingredient self() {
		return (Ingredient) this;
	}

	void kjs$setCustomIngredient(ICustomIngredient ingredient);

	default Ingredient.Value[] kjs$getValues() {
		return self().values;
	}

	default boolean kjs$isCustom() {
		return false;
	}

	default boolean kjs$isSimple() {
		return true;
	}

	default boolean kjs$hasNoItems() {
		ItemStack[] items = self().getItems();
		if (items.length == 0)
			return true;
		if (items.length == 1) {
			// If we potentially added a barrier due to the ingredient being an empty tag, try and check if it is the stack we added
			ItemStack item = items[0];
			return item.getItem() == net.minecraft.world.item.Items.BARRIER && item.getHoverName() instanceof net.minecraft.network.chat.MutableComponent hoverName && hoverName.getString().startsWith("Empty Tag: ");
		}
		return false;
	}
}
