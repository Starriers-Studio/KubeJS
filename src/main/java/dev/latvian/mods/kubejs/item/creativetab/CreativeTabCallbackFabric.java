package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public record CreativeTabCallbackFabric(FabricItemGroupEntries entries) implements CreativeTabCallback {
	@Override
	public void addAfter(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility) {
		entries.addAfter(order, Arrays.asList(items), visibility);
	}

	@Override
	public void addBefore(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility) {
		entries.addBefore(order, Arrays.asList(items), visibility);
	}

	@Override
	public void remove(ItemPredicate filter, boolean removeParent, boolean removeSearch) {
		if (removeParent) {
			entries.getDisplayStacks().removeIf(filter);
		}

		if (removeSearch) {
			entries.getSearchTabStacks().removeIf(filter);
		}
	}
}