package me.textrue.kubejs.fabric.thirdparty.items.wapper;

import com.google.common.base.Preconditions;
import me.textrue.kubejs.fabric.thirdparty.items.ItemHandlerModifiable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

/**
 * A wrapper that composes another IItemHandlerModifiable, exposing only a range of the composed slots.
 * Shifting of slot indices is handled automatically for you.
 */
public abstract class RangedWrapper implements ItemHandlerModifiable {
	private final ItemHandlerModifiable compose;
	private final int minSlot;
	private final int maxSlot;

	public RangedWrapper(ItemHandlerModifiable compose, int minSlot, int maxSlotExclusive) {
		Preconditions.checkArgument(maxSlotExclusive > minSlot, "Max slot must be greater than min slot");
		this.compose = compose;
		this.minSlot = minSlot;
		this.maxSlot = maxSlotExclusive;
	}

	@Override
	public int getSlotCount() {
		return maxSlot - minSlot;
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		SingleSlotStorage storage = (SingleSlotStorage) this;
		return storage.getSlot(slot);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (checkSlot(slot)) {
			return compose.getStackInSlot(slot + minSlot);
		}

		return ItemStack.EMPTY;
	}

	@Override
	public long insertItem(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (checkSlot(slot)) {
			return compose.insertItem(slot + minSlot, resource, maxAmount, transaction);
		}

		return resource.toStack().getCount();
	}

	@Override
	public long extractItem(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (checkSlot(slot)) {
			return compose.extractItem(slot + minSlot, resource, maxAmount, transaction);
		}

		return ItemStack.EMPTY.getCount();
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (checkSlot(slot)) {
			compose.setStackInSlot(slot + minSlot, stack);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		if (checkSlot(slot)) {
			return compose.getSlotLimit(slot + minSlot);
		}

		return 0;
	}

	@Override
	public boolean isItemValid(int slot, ItemVariant resource, int count) {
		if (checkSlot(slot)) {
			return compose.isItemValid(slot + minSlot, resource, count);
		}

		return false;
	}

	private boolean checkSlot(int localSlot) {
		return localSlot + minSlot < maxSlot;
	}
}
