package me.textrue.kubejs.fabric.thirdparty.items.wapper;

import me.textrue.kubejs.fabric.thirdparty.items.ItemHandlerModifiable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class InvWrapper implements ItemHandlerModifiable {
	private final Container inv;

	public InvWrapper(Container inv) {
		this.inv = inv;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		InvWrapper that = (InvWrapper) o;

		return getInv().equals(that.getInv());
	}

	@Override
	public int hashCode() {
		return getInv().hashCode();
	}

	@Override
	public int getSlotCount() {
		return getInv().getContainerSize();
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		SingleSlotStorage storage = (SingleSlotStorage) this;
		return storage.getSlot(slot);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return getInv().getItem(slot);
	}

	@Override
	public long insertItem(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		ItemStack stack = resource.toStack();

		if (stack.isEmpty())
			return ItemStack.EMPTY.getCount();


		ItemStack stackInSlot = getInv().getItem(slot);

		int m;
		if (!stackInSlot.isEmpty()) {
			if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot)))
				return stack.getCount();

			if (!ItemStack.isSameItemSameComponents(stack, stackInSlot))
				return stack.getCount();

			if (!getInv().canPlaceItem(slot, stack))
				return stack.getCount();

			m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

			if (stack.getCount() <= m) {
				ItemStack copy = stack.copy();
				copy.grow(stackInSlot.getCount());
				getInv().setItem(slot, copy);
				getInv().setChanged();

				return ItemStack.EMPTY.getCount();
			} else {
				// copy the stack to not modify the original one
				stack = stack.copy();
				ItemStack copy = stack.split(m);
				copy.grow(stackInSlot.getCount());
				getInv().setItem(slot, copy);
				getInv().setChanged();
				return stack.getCount();
//				if (!simulate) {
//					ItemStack copy = stack.split(m);
//					copy.grow(stackInSlot.getCount());
//					getInv().setItem(slot, copy);
//					getInv().setChanged();
//					return stack.getCount();
//				} else {
//					resource.toStack().copy().shrink(m);
//					return resource.toStack().copy().getCount();
//				}
			}
		} else {
			if (!getInv().canPlaceItem(slot, stack))
				return stack.getCount();

			m = Math.min(resource.toStack().getMaxStackSize(), getSlotLimit(slot));
			if (m < resource.toStack().getCount()) {
				// copy the stack to not modify the original one
				stack = stack.copy();
				getInv().setItem(slot, stack.split(m));
				getInv().setChanged();
				return stack.getCount();
//				if (!simulate) {
//					getInv().setItem(slot, stack.split(m));
//					getInv().setChanged();
//					return stack.getCount();
//				} else {
//					stack.shrink(m);
//					return stack.getCount();
//				}
			} else {
				getInv().setItem(slot, stack);
				getInv().setChanged();
				return ItemStack.EMPTY.getCount();
			}
		}
	}

	@Override
	public long extractItem(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (maxAmount == 0)
			return ItemStack.EMPTY.getCount();

		ItemStack stackInSlot = getInv().getItem(slot);

		if (stackInSlot.isEmpty())
			return ItemStack.EMPTY.getCount();

//		if (simulate) {
//			if (stackInSlot.getCount() < amount) {
//				return stackInSlot.copy();
//			} else {
//				ItemStack copy = stackInSlot.copy();
//				copy.setCount(amount);
//				return copy;
//			}
//		} else {
//			int m = Math.min(stackInSlot.getCount(), amount);
//
//			ItemStack decrStackSize = getInv().removeItem(slot, m);
//			getInv().setChanged();
//			return decrStackSize;
//		}
		int m = Math.toIntExact(Math.min(stackInSlot.getCount(), maxAmount));

		ItemStack decrStackSize = getInv().removeItem(slot, m);
		getInv().setChanged();
		return decrStackSize.getCount();
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		getInv().setItem(slot, stack);
	}

	@Override
	public int getSlotLimit(int slot) {
		return getInv().getMaxStackSize();
	}

	@Override
	public boolean isItemValid(int slot, ItemVariant resource, int count) {
		return getInv().canPlaceItem(slot, resource.toStack());
	}

	public Container getInv() {
		return inv;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		SingleSlotStorage storage = (SingleSlotStorage) this;
		return storage.insert(resource, maxAmount, transaction);
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		SingleSlotStorage storage = (SingleSlotStorage) this;
		return storage.extract(resource, maxAmount, transaction);
	}
}
