package me.textrue.kubejs.fabric.thirdparty.mixin;

import me.textrue.kubejs.fabric.thirdparty.ingredients.ICustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CustomIngredient.class)
public interface CustomIngredientMixin extends ICustomIngredient {
}
