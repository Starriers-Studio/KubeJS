package me.textrue.kubejs.fabric.helper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class ItemContainerContentsHelper {
   /**
     * Neo:
     * {@return the number of slots in this container}
     */
    public static int getSlots(ItemContainerContents contents) {
        return contents.items.size();
    }

	/**
     * Neo: Gets a copy of the stack at a particular slot.
     *
     * @param slot The slot to check. Must be within [0, {@link #getSlots(ItemContainerContents)}]
     * @return A copy of the stack in that slot
     * @throws UnsupportedOperationException if the provided slot index is out-of-bounds.
     */
    public static ItemStack getStackInSlot(ItemContainerContents contents, int slot) {
        validateSlotIndex(contents, slot);
        return contents.items.get(slot).copy();
    }

    /**
    * Neo: Throws {@link UnsupportedOperationException} if the provided slot index is invalid.
	*/
    private static void validateSlotIndex(ItemContainerContents contents, int slot) {
       if (slot < 0 || slot >= contents.items.size()) {
            throw new UnsupportedOperationException("Slot " + slot + " not in valid range - [0," + contents.items.size() + ")");
        }
    }
}
