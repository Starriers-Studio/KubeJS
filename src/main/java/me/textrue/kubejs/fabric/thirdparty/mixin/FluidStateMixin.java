package me.textrue.kubejs.fabric.thirdparty.mixin;

import me.textrue.kubejs.fabric.thirdparty.extensions.FluidStateExtension;
import me.textrue.kubejs.fabric.thirdparty.fluids.FluidType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FluidState.class)
public abstract class FluidStateMixin implements FluidStateExtension {
	@Shadow
	public abstract Fluid getType();

	@Override
	public FluidType kjs_thirdparty$getFluidType() {
		return this.getType().kjs$getFluidType();
	}
}
