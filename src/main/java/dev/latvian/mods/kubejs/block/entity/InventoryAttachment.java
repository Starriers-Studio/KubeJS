package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.item.ItemPredicate;
import me.textrue.kubejs.fabric.thirdparty.items.ItemStackHandler;
import me.textrue.kubejs.fabric.thirdparty.util.ThirdPartyContexts;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class InventoryAttachment implements BlockEntityAttachment {
	public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType(KubeJS.id("inventory"), Factory.class);

	public record Factory(int width, int height, Optional<ItemPredicate> inputFilter) implements BlockEntityAttachmentFactory {
		@Override
		public BlockEntityAttachment create(BlockEntityAttachmentInfo info, KubeBlockEntity entity) {
			return new InventoryAttachment(entity, width, height, inputFilter.orElse(null));
		}

		@Override
		public List<BlockApiLookup<?, ?>> getCapabilities() {
			BlockApiLookup<ItemStackHandler, Direction> lookup = BlockApiLookup.get(ThirdPartyContexts.ITEM_HANDLER_BLOCK_ID, ItemStackHandler.class, Direction.class);
			return List.of(lookup);
		}
	}

	public static class Wrapped extends ItemStackHandler implements InventoryKJS {
		private final InventoryAttachment attachment;

		public Wrapped(InventoryAttachment attachment) {
			super(attachment.width * attachment.height);
			this.attachment = attachment;
		}

		public NonNullList<ItemStack> stacks() {
			return stacks;
		}

		@Override
		protected void onContentsChanged(int slot) {
			attachment.blockEntity.save();
		}

		@Override
		public boolean isItemValid(int slot, ItemVariant resource, int count) {
			var stack = resource.toStack(count);
			return (attachment.inputFilter == null || attachment.inputFilter.test(stack)) && super.isItemValid(slot, resource, count);
		}

		@Override
		public int kjs$getWidth() {
			return attachment.width;
		}

		@Override
		public int kjs$getHeight() {
			return attachment.height;
		}

		@Override
		public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			return 0;
		}
	}

	public final int width, height;
	public final KubeBlockEntity blockEntity;
	public final ItemPredicate inputFilter;
	public final Wrapped inventory;

	public InventoryAttachment(KubeBlockEntity blockEntity, int width, int height, @Nullable ItemPredicate inputFilter) {
		this.width = width;
		this.height = height;
		this.blockEntity = blockEntity;
		this.inputFilter = inputFilter;
		this.inventory = new Wrapped(this);
	}

	@Override
	public Object getWrappedObject() {
		return inventory;
	}

	@Override
	@Nullable
	public <CAP, SRC> CAP getCapability(BlockApiLookup<CAP, SRC> capability) {
		if (capability == BlockApiLookup.get(ThirdPartyContexts.ITEM_HANDLER_BLOCK_ID, ItemStackHandler.class, Direction.class)) {
			return (CAP) inventory;
		}

		return null;
	}

	@Override
	public ListTag serialize(HolderLookup.Provider registries) {
		var list = new ListTag();

		for (int i = 0; i < width * height; i++) {
			var stack = inventory.stacks().get(i);

			if (!stack.isEmpty()) {
				var itemTag = (CompoundTag) stack.save(registries, new CompoundTag());
				itemTag.putByte("slot", (byte) i);
				list.add(itemTag);
			}
		}

		return list;
	}

	@Override
	public void deserialize(HolderLookup.Provider registries, Tag tag) {
		inventory.setSize(width * height);

		if (tag instanceof ListTag list) {
			for (int i = 0; i < list.size(); i++) {
				var itemTag = list.getCompound(i);
				var slot = itemTag.getByte("slot");

				if (slot >= 0 && slot < width * height) {
					inventory.stacks().set(slot, ItemStack.parse(registries, itemTag).orElse(ItemStack.EMPTY));
				}
			}
		}
	}

	@Override
	public void onRemove(ServerLevel level, KubeBlockEntity blockEntity, BlockState newState) {
		Containers.dropContents(blockEntity.getLevel(), blockEntity.getBlockPos(), inventory.stacks());
	}
}
