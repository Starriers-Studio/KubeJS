package me.textrue.kubejs.fabric.helper;

import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import me.textrue.kubejs.fabric.thirdparty.fluids.FluidStack;
import mezz.jei.api.fabric.ingredients.fluids.IJeiFluidIngredient;
import mezz.jei.api.fabric.ingredients.fluids.JeiFluidIngredient;

import java.util.ArrayList;
import java.util.List;

public class FluidStackHelper {
	public static dev.architectury.fluid.FluidStack thirdPartyToArch(FluidStack stack) {
		return FluidStackHooksFabric.fromFabric(stack.getVariant(), stack.getAmount());
	}

	public static FluidStack archToThirdParty(dev.architectury.fluid.FluidStack stack) {
		return new FluidStack(stack.getFluid(), stack.getAmount());
	}

	public static IJeiFluidIngredient thirdPartyToJei(FluidStack stack) {
		return new JeiFluidIngredient(stack.getVariant(), stack.getAmount());
	}

	public static List<IJeiFluidIngredient> thirdPartyToJeis(List<FluidStack> stacks) {
		List<IJeiFluidIngredient> jeiFluidIngredients = new ArrayList<>();

		for (var stack : stacks) {
			jeiFluidIngredients.add(thirdPartyToJei(stack));
		}
		return jeiFluidIngredients;
	}

	public static FluidStack jeiToThirdParty(IJeiFluidIngredient jeiFluidIngredient) {
		return new FluidStack(jeiFluidIngredient.getFluidVariant(), jeiFluidIngredient.getAmount());
	}

	public static List<FluidStack> jeiToThirdParties(List<IJeiFluidIngredient> jeiFluidIngredients) {
		List<FluidStack> fluidStacks = new ArrayList<>();

		for (var stack : jeiFluidIngredients) {
			fluidStacks.add(jeiToThirdParty(stack));
		}
		return fluidStacks;
	}
}
