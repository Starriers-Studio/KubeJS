package me.textrue.kubejs.fabric.thirdparty.items;

import me.textrue.kubejs.fabric.thirdparty.items.wapper.PlayerMainInvWrapper;
import me.textrue.kubejs.fabric.thirdparty.util.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemHandlerHelper {
	public static ItemStack insertItem(ItemHandler dest, ItemStack stack, boolean simulate) {
		if (dest == null || stack.isEmpty())
			return stack;

		for (int i = 0; i < dest.getSlotCount(); i++) {
			stack = dest.getStackInSlot((int) dest.insertItem(i, ItemVariant.of(stack), stack.getMaxStackSize(), TransferUtil.getTransaction()));
			if (stack.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}

		return stack;
	}

	/**
	 * Inserts the ItemStack into the inventory, filling up already present stacks first.
	 * This is equivalent to the behaviour of a player picking up an item.
	 * Note: This function stacks items without subtypes with different metadata together.
	 */
	public static ItemStack insertItemStacked(ItemHandler inventory, ItemStack stack, boolean simulate) {
		if (inventory == null || stack.isEmpty())
			return stack;

		// not stackable -> just insert into a new slot
		if (!stack.isStackable()) {
			return insertItem(inventory, stack, simulate);
		}

		int sizeInventory = inventory.getSlotCount();

		// go through the inventory and try to fill up already existing items
		for (int i = 0; i < sizeInventory; i++) {
			ItemStack slot = inventory.getStackInSlot(i);
			if (ItemStack.isSameItemSameComponents(slot, stack)) {

				stack = inventory.getStackInSlot((int) inventory.insertItem(i, ItemVariant.of(slot), slot.getMaxStackSize(), TransferUtil.getTransaction()));

				if (stack.isEmpty()) {
					break;
				}
			}
		}

		// insert remainder into empty slots
		if (!stack.isEmpty()) {
			// find empty slot
			for (int i = 0; i < sizeInventory; i++) {
				ItemStack slot = inventory.getStackInSlot(i);
				if (slot.isEmpty()) {
					stack = inventory.getStackInSlot((int) inventory.insertItem(i, ItemVariant.of(slot), slot.getMaxStackSize(), TransferUtil.getTransaction()));
					if (stack.isEmpty()) {
						break;
					}
				}
			}
		}

		return stack;
	}

	/** giveItemToPlayer without preferred slot */
	public static void giveItemToPlayer(Player player, ItemStack stack) {
		giveItemToPlayer(player, stack, -1);
	}

	/**
	 * Inserts the given itemstack into the players inventory.
	 * If the inventory can't hold it, the item will be dropped in the world at the players position.
	 *
	 * @param player The player to give the item to
	 * @param stack  The itemstack to insert
	 */
	public static void giveItemToPlayer(Player player, ItemStack stack, int preferredSlot) {
		if (stack.isEmpty()) return;

		ItemHandler inventory = new PlayerMainInvWrapper(player.getInventory());
		Level level = player.kjs$getLevel();

		// try adding it into the inventory
		ItemStack remainder = stack;
		// insert into preferred slot first
		if (preferredSlot >= 0 && preferredSlot < inventory.getSlotCount()) {
			remainder = inventory.getStackInSlot((int) inventory.insertItem(preferredSlot, ItemVariant.of(stack), stack.getMaxStackSize(), TransferUtil.getTransaction()));
		}
		// then into the inventory in general
		if (!remainder.isEmpty()) {
			remainder = insertItemStacked(inventory, remainder, false);
		}

		// play sound if something got picked up
		if (remainder.isEmpty() || remainder.getCount() != stack.getCount()) {
			level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
				SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}

		// drop remaining itemstack into the level
		if (!remainder.isEmpty() && !level.isClientSide) {
			ItemEntity entityitem = new ItemEntity(level, player.getX(), player.getY() + 0.5, player.getZ(), remainder);
			entityitem.setPickUpDelay(40);
			entityitem.setDeltaMovement(entityitem.getDeltaMovement().multiply(0, 1, 0));

			level.addFreshEntity(entityitem);
		}
	}
}
