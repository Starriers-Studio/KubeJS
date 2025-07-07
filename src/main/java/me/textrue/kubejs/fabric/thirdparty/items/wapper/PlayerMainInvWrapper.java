package me.textrue.kubejs.fabric.thirdparty.items.wapper;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Exposes the player inventory WITHOUT the armor inventory as IItemHandler.
 * Also takes core of inserting/extracting having the same logic as picking up items.
 */
public class PlayerMainInvWrapper extends RangedWrapper {
	private final Inventory inventoryPlayer;

	public PlayerMainInvWrapper(Inventory inv) {
		super(new InvWrapper(inv), 0, inv.items.size());
		inventoryPlayer = inv;
	}

	@Override
	public long insertItem(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
		ItemStack stack = resource.toStack();
		ItemStack rest = this.getStackInSlot((int) super.insertItem(slot, resource, maxAmount, transaction));
		if (rest.getCount() != stack.getCount()) {
			// the stack in the slot changed, animate it
			ItemStack inSlot = getStackInSlot(slot);
			if (!inSlot.isEmpty()) {
				if (getInventoryPlayer().player.level().isClientSide) {
					inSlot.setPopTime(5);
				} else if (getInventoryPlayer().player instanceof ServerPlayer) {
					getInventoryPlayer().player.containerMenu.broadcastChanges();
				}
			}
		}
		return rest.getCount();
	}

	public Inventory getInventoryPlayer() {
		return inventoryPlayer;
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
