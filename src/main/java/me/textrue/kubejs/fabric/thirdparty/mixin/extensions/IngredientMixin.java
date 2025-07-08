package me.textrue.kubejs.fabric.thirdparty.mixin.extensions;

import me.textrue.kubejs.fabric.thirdparty.extensions.IngredientExtension;
import me.textrue.kubejs.fabric.thirdparty.ingredients.ICustomIngredient;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Ingredient.class)
public class IngredientMixin implements IngredientExtension {
	@Shadow
	@Final
	public Ingredient.Value[] values;
	@Unique
	@Nullable
	private ICustomIngredient kjs$customIngredient = null;

	@Override
	public void kjs$setCustomIngredient(ICustomIngredient ingredient) {
		kjs$customIngredient = ingredient;
	}

	@Override
	public Ingredient.Value[] kjs$getValues() {
		if (kjs$isCustom()) {
			throw new IllegalStateException("Cannot retrieve values from custom ingredient!");
		}
		return this.values;
	}

	@Override
	public boolean kjs$isCustom() {
		return this.kjs$customIngredient != null;
	}

	@Override
	public boolean kjs$isSimple() {
		return this.kjs$customIngredient == null || this.kjs$customIngredient.isSimple();
	}
}
