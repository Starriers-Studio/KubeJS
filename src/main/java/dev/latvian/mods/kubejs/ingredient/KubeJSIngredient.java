package dev.latvian.mods.kubejs.ingredient;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import me.textrue.kubejs.fabric.thirdparty.ingredients.ICustomIngredient;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface KubeJSIngredient extends ICustomIngredient, Predicate<ItemStack> {
	@Override
	boolean test(ItemStack stack);

	@Override
	default Stream<ItemStack> getItems() {
		return ItemWrapper.getList().stream().filter(this);
	}

	@Override
	default boolean isSimple() {
		return CommonProperties.get().serverOnly;
	}
}
