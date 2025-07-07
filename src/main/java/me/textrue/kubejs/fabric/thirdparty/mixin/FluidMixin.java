package me.textrue.kubejs.fabric.thirdparty.mixin;

import me.textrue.kubejs.fabric.thirdparty.extensions.FluidExtension;
import me.textrue.kubejs.fabric.thirdparty.fluids.FluidType;
import me.textrue.kubejs.fabric.thirdparty.registries.ThirdPartyRegistry;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Fluid.class)
public class FluidMixin implements FluidExtension {
	@Unique
	private FluidType kjs$fluidType;

	@Override
	public FluidType kjs$getFluidType() {
		var fluid = (Fluid) (Object) this;
		if (kjs$fluidType == null) {
			if (fluid == Fluids.EMPTY)
				kjs$fluidType = ThirdPartyRegistry.EMPTY_TYPE.value();
			if (fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER)
				kjs$fluidType = ThirdPartyRegistry.WATER_TYPE.value();
			if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA)
				kjs$fluidType = ThirdPartyRegistry.LAVA_TYPE.value();
		}
		return this.kjs$fluidType;
	}
}
