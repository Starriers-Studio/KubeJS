package me.textrue.kubejs.fabric.thirdparty.items;

import me.textrue.kubejs.fabric.thirdparty.util.NBTSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class ItemStackHandler implements ItemHandler, ItemHandlerModifiable, NBTSerializable<CompoundTag> {
	protected NonNullList<ItemStack> stacks;

	public ItemStackHandler() {
		this(1);
	}

	public ItemStackHandler(int size) {
		stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	public ItemStackHandler(NonNullList<ItemStack> stacks) {
		this.stacks = stacks;
	}

	public void setSize(int size) {
		stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		this.stacks.set(slot, stack);
		onContentsChanged(slot);
	}

	@Override
	public int getSlotCount() {
		return stacks.size();
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		SingleSlotStorage storage = (SingleSlotStorage) this;
		return storage.getSlot(slot);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return this.stacks.get(slot);
	}

	@Override
	public long insertItem(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		var stack = resource.toStack();

		if (resource.toStack().isEmpty())
			return ItemStack.EMPTY.getCount();

		if (!isItemValid(slot, resource, stack.getCount()))
			return stack.getCount();

		validateSlotIndex(slot);

		ItemStack existing = this.stacks.get(slot);

		int limit = getStackLimit(slot, stack);

		if (!existing.isEmpty()) {
			if (!ItemStack.isSameItemSameComponents(stack, existing))
				return stack.getCount();

			limit -= existing.getCount();
		}

		if (limit <= 0)
			return stack.getCount();

		boolean reachedLimit = stack.getCount() > limit;

//		if (!simulate) {
//			if (existing.isEmpty()) {
//				this.stacks.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
//			} else {
//				existing.grow(reachedLimit ? limit : stack.getCount());
//			}
//			onContentsChanged(slot);
//		}

		return reachedLimit ? stack.copyWithCount(stack.getCount() - limit).getCount() : ItemStack.EMPTY.getCount();
	}

	@Override
	public long extractItem(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (maxAmount == 0)
			return ItemStack.EMPTY.getCount();

		validateSlotIndex(slot);

		ItemStack existing = this.stacks.get(slot);

		if (existing.isEmpty())
			return ItemStack.EMPTY.getCount();

		int toExtract = Math.toIntExact(Math.min(maxAmount, existing.getMaxStackSize()));

		if (existing.getCount() <= toExtract) {
//			if (!simulate) {
//				this.stacks.set(slot, ItemStack.EMPTY);
//				onContentsChanged(slot);
//				return existing.getCount();
//			} else {
				return existing.copy().getCount();
//			}
		} else {
//			if (!simulate) {
//				this.stacks.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
//				onContentsChanged(slot);
//			}

			return existing.copyWithCount(toExtract).getCount();
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return Item.ABSOLUTE_MAX_STACK_SIZE;
	}

	protected int getStackLimit(int slot, ItemStack stack) {
		return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
	}

	@Override
	public boolean isItemValid(int slot, ItemVariant resource, int count) {
		return true;
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		ListTag nbtTagList = new ListTag();
		for (int i = 0; i < stacks.size(); i++) {
			if (!stacks.get(i).isEmpty()) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putInt("Slot", i);
				nbtTagList.add(stacks.get(i).save(provider, itemTag));
			}
		}
		CompoundTag nbt = new CompoundTag();
		nbt.put("Items", nbtTagList);
		nbt.putInt("Size", stacks.size());
		return nbt;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
		setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : stacks.size());
		ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTags = tagList.getCompound(i);
			int slot = itemTags.getInt("Slot");

			if (slot >= 0 && slot < stacks.size()) {
				ItemStack.parse(provider, itemTags).ifPresent(stack -> stacks.set(slot, stack));
			}
		}
		onLoad();
	}

	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= stacks.size())
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
	}

	protected void onLoad() {}

	protected void onContentsChanged(int slot) {}
}
