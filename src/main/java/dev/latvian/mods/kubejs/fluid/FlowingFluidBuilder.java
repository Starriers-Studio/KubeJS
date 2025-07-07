package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import me.textrue.kubejs.fabric.thirdparty.fluids.BaseFlowingFluid;
import net.minecraft.world.level.material.FlowingFluid;

public class FlowingFluidBuilder extends BuilderBase<FlowingFluid> {
	public final FluidBuilder fluidBuilder;

	public FlowingFluidBuilder(FluidBuilder b) {
		super(b.newID("flowing_", ""));
		fluidBuilder = b;
	}

	@Override
	public FlowingFluid createObject() {
		return new BaseFlowingFluid.Flowing(fluidBuilder.createProperties());
	}
}
