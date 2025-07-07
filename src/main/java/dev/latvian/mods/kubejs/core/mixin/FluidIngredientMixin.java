package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.FluidIngredientKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import me.textrue.kubejs.fabric.thirdparty.ingredients.fluids.FluidIngredient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = FluidIngredient.class, remap = false)
@RemapPrefixForJS("kjs$")
public abstract class FluidIngredientMixin implements FluidIngredientKJS {
}
