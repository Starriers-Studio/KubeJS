package me.textrue.kubejs.fabric.thirdparty.items;

import com.google.common.base.Preconditions;
import me.textrue.kubejs.fabric.helper.ItemContainerContentsHelper;
import me.textrue.kubejs.fabric.thirdparty.util.MutableDataComponentHolder;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

/**
 * Variant of {@link ItemStackHandler} for use with data components.
 *
 * @implNote All functions in this class should attempt to minimize component read/writes to avoid unnecessary churn, noting that the component can never be cached.
 */
public abstract class ComponentItemHandler implements ItemHandlerModifiable {
	protected final MutableDataComponentHolder parent;
	protected final DataComponentType<ItemContainerContents> component;
	protected final int size;

	/**
	 * Creates a new {@link ComponentItemHandler} with target size. If the existing component is smaller than the given size, it will be expanded on write.
	 *
	 * @param parent    The parent component holder, such as an {@link ItemStack}
	 * @param component The data component referencing the stored inventory of the item stack
	 * @param size      The number of slots. Must be less than 256 due to limitations of {@link ItemContainerContents}
	 */
	public ComponentItemHandler(MutableDataComponentHolder parent, DataComponentType<ItemContainerContents> component, int size) {
		this.parent = parent;
		this.component = component;
		this.size = size;
		Preconditions.checkArgument(size <= 256, "The max size of ItemContainerContents is 256 slots.");
	}

	@Override
	public int getSlotCount() {
		return this.size;
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		SingleSlotStorage storage = (SingleSlotStorage) this;
		return storage.getSlot(slot);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		ItemContainerContents contents = this.getContents();
		return this.getStackFromContents(contents, slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		this.validateSlotIndex(slot);
		if (!this.isItemValid(slot, ItemVariant.of(stack), stack.getCount())) {
			throw new RuntimeException("Invalid stack " + stack + " for slot " + slot + ")");
		}
		ItemContainerContents contents = this.getContents();
		ItemStack existing = this.getStackFromContents(contents, slot);
		if (!ItemStack.matches(stack, existing)) {
			this.updateContents(contents, stack, slot);
		}
	}

	@Override
	public long insertItem(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		var toInsert = resource.toStack();

		this.validateSlotIndex(slot);

		if (toInsert.isEmpty()) {
			return ItemStack.EMPTY.getCount();
		}

		if (!this.isItemValid(slot, resource, toInsert.getCount())) {
			return toInsert.getCount();
		}

		ItemContainerContents contents = this.getContents();
		ItemStack existing = this.getStackFromContents(contents, slot);
		// Max amount of the stack that could be inserted
		int insertLimit = Math.min(this.getSlotLimit(slot), toInsert.getMaxStackSize());

		if (!existing.isEmpty()) {
			if (!ItemStack.isSameItemSameComponents(toInsert, existing)) {
				return toInsert.getCount();
			}

			insertLimit -= existing.getCount();
		}

		if (insertLimit <= 0) {
			return toInsert.getCount();
		}

		int inserted = Math.min(insertLimit, toInsert.getCount());

//		if (!simulate) {
//			this.updateContents(contents, toInsert.copyWithCount(existing.getCount() + inserted), slot);
//		}

		return toInsert.copyWithCount(toInsert.getCount() - inserted).getCount();
	}

	@Override
	public long extractItem(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		this.validateSlotIndex(slot);

		if (maxAmount == 0) {
			return ItemStack.EMPTY.getCount();
		}

		ItemContainerContents contents = this.getContents();
		ItemStack existing = this.getStackFromContents(contents, slot);

		if (existing.isEmpty()) {
			return ItemStack.EMPTY.getCount();
		}

		int toExtract = Math.toIntExact(Math.min(maxAmount, existing.getCount()));

//		if (!simulate) {
//			this.updateContents(contents, existing.copyWithCount(existing.getCount() - toExtract), slot);
//		}

		return existing.copyWithCount(toExtract).getCount();
	}

	@Override
	public int getSlotLimit(int slot) {
		return Item.ABSOLUTE_MAX_STACK_SIZE;
	}

	@Override
	public boolean isItemValid(int slot, ItemVariant resource, int count) {
		var stack = resource.toStack();
		return stack.getItem().canFitInsideContainerItems();
	}

	/**
	 * Called from {@link #updateContents} after the stack stored in a slot has been updated.
	 * <p>
	 * Modifications to the stacks used as parameters here will not write-back to the stored data.
	 *
	 * @param slot     The slot that changed
	 * @param oldStack The old stack that was present in the slot
	 * @param newStack The new stack that is now present in the slot
	 */
	protected void onContentsChanged(int slot, ItemStack oldStack, ItemStack newStack) {}

	/**
	 * Retrieves the {@link ItemContainerContents} from the parent object's data component map.
	 */
	protected ItemContainerContents getContents() {
		return this.parent.getOrDefault(this.component, ItemContainerContents.EMPTY);
	}

	/**
	 * Retrieves a copy of a single stack from the underlying data component, returning {@link ItemStack#EMPTY} if the component does not have a slot present.
	 * <p>
	 * Throws an exception if the slot is out-of-bounds for this capability.
	 *
	 * @param contents The existing contents from {@link #getContents()}
	 * @param slot     The target slot
	 * @return A copy of the stack in the target slot
	 */
	protected ItemStack getStackFromContents(ItemContainerContents contents, int slot) {
		this.validateSlotIndex(slot);
		return ItemContainerContentsHelper.getSlots(contents) <= slot ? ItemStack.EMPTY : ItemContainerContentsHelper.getStackInSlot(contents, slot);
	}

	/**
	 * Performs a copy and write operation on the underlying data component, changing the stack in the target slot.
	 * <p>
	 * If the existing component is larger than {@link #getSlots()}, additional slots will <b>not</b> be truncated.
	 *
	 * @param contents The existing contents from {@link #getContents()}
	 * @param stack    The new stack to set to the slot
	 * @param slot     The target slot
	 */
	protected void updateContents(ItemContainerContents contents, ItemStack stack, int slot) {
		this.validateSlotIndex(slot);
		// Use the max of the contents slots and the capability slots to avoid truncating
		NonNullList<ItemStack> list = NonNullList.withSize(Math.max(ItemContainerContentsHelper.getSlots(contents), this.getSlotCount()), ItemStack.EMPTY);
		contents.copyInto(list);
		ItemStack oldStack = list.get(slot);
		list.set(slot, stack);
		this.parent.set(this.component, ItemContainerContents.fromItems(list));
		this.onContentsChanged(slot, oldStack, stack);
	}

	/**
	 * Throws {@link UnsupportedOperationException} if the provided slot index is invalid.
	 */
	protected final void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= getSlotCount()) {
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlotCount() + ")");
		}
	}
}
