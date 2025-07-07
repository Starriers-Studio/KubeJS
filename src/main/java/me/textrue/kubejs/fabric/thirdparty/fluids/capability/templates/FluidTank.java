package me.textrue.kubejs.fabric.thirdparty.fluids.capability.templates;

import java.util.function.Predicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import me.textrue.kubejs.fabric.thirdparty.fluids.FluidStack;
import me.textrue.kubejs.fabric.thirdparty.fluids.IFluidTank;
import me.textrue.kubejs.fabric.thirdparty.fluids.capability.IFluidHandler;

public class FluidTank implements IFluidHandler, IFluidTank {
	protected Predicate<FluidStack> validator;
	protected FluidStack fluid = FluidStack.EMPTY;
	protected int capacity;

	public FluidTank(int capacity) {
		this(capacity, e -> true);
	}

	public FluidTank(int capacity, Predicate<FluidStack> validator) {
		this.capacity = capacity;
		this.validator = validator;
	}

	public FluidTank setCapacity(int capacity) {
		this.capacity = capacity;
		return this;
	}

	public FluidTank setValidator(Predicate<FluidStack> validator) {
		if (validator != null) {
			this.validator = validator;
		}
		return this;
	}

	public boolean isFluidValid(FluidStack stack) {
		return validator.test(stack);
	}

	public int getCapacity() {
		return capacity;
	}

	public FluidStack getFluid() {
		return fluid;
	}

	public int getFluidAmount() {
		return Math.toIntExact(fluid.getAmount());
	}

	public FluidTank readFromNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
		fluid = FluidStack.parseOptional(lookupProvider, nbt.getCompound("Fluid"));
		return this;
	}

	public CompoundTag writeToNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
		if (!fluid.isEmpty()) {
			nbt.put("Fluid", fluid.save(lookupProvider));
		}

		return nbt;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return getCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return isFluidValid(stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.isEmpty() || !isFluidValid(resource)) {
			return 0;
		}
		if (action.simulate()) {
			if (fluid.isEmpty()) {
				return Math.toIntExact(Math.min(capacity, resource.getAmount()));
			}
			if (!FluidStack.isSameFluidSameComponents(fluid, resource)) {
				return 0;
			}
			return Math.toIntExact(Math.min(capacity - fluid.getAmount(), resource.getAmount()));
		}
		if (fluid.isEmpty()) {
			fluid = resource.copyWithAmount(Math.min(capacity, resource.getAmount()));
			onContentsChanged();
			return Math.toIntExact(fluid.getAmount());
		}
		if (!FluidStack.isSameFluidSameComponents(fluid, resource)) {
			return 0;
		}
		int filled = Math.toIntExact(capacity - fluid.getAmount());

		if (resource.getAmount() < filled) {
			fluid.setAmount(resource.getAmount());
			filled = Math.toIntExact(resource.getAmount());
		} else {
			fluid.setAmount(capacity);
		}
		if (filled > 0)
			onContentsChanged();
		return filled;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, fluid)) {
			return FluidStack.EMPTY;
		}
		return drain(Math.toIntExact(resource.getAmount()), action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		int drained = maxDrain;
		if (fluid.getAmount() < drained) {
			drained = Math.toIntExact(fluid.getAmount());
		}
		FluidStack stack = fluid.copyWithAmount(drained);
		if (action.execute() && drained > 0) {
			fluid.shrink(drained);
			onContentsChanged();
		}
		return stack;
	}

	protected void onContentsChanged() {}

	public void setFluid(FluidStack stack) {
		this.fluid = stack;
	}

	public boolean isEmpty() {
		return fluid.isEmpty();
	}

	public int getSpace() {
		return Math.toIntExact(Math.max(0, capacity - fluid.getAmount()));
	}
}
