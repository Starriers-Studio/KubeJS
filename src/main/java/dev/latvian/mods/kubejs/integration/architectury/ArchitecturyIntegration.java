package dev.latvian.mods.kubejs.integration.architectury;

import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import me.textrue.kubejs.fabric.thirdparty.fluids.FluidStack;

public class ArchitecturyIntegration implements KubeJSPlugin {
	public static dev.architectury.fluid.FluidStack wrapArchFluid(RegistryAccessContainer registries, Object o) {
		FluidStack fluidStack = FluidWrapper.wrap(registries, o);
		return FluidStackHooksFabric.fromFabric(fluidStack.getVariant(), fluidStack.getAmount());
	}

	@Override
	public void registerTypeWrappers(TypeWrapperRegistry registry) {
		registry.register(dev.architectury.fluid.FluidStack.class, ArchitecturyIntegration::wrapArchFluid);
	}
}