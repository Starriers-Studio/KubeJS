package me.textrue.kubejs.fabric.helper;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class TagHelper {
	public static TagKey<Block> createBlockTag(String name) {
		return TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace(name));
	}

	public static TagKey<Block> createBlockTag(ResourceLocation id) {
		return TagKey.create(Registries.BLOCK, id);
	}

	public static TagKey<Item> createItemTag(String name) {
		return TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(name));
	}

	public static TagKey<Item> createItemTag(ResourceLocation id) {
		return TagKey.create(Registries.ITEM, id);
	}

	public static TagKey<Fluid> createFluidTag(String name) {
		return TagKey.create(Registries.FLUID, ResourceLocation.withDefaultNamespace(name));
	}

	public static TagKey<Fluid> createFluidTag(ResourceLocation id) {
		return TagKey.create(Registries.FLUID, id);
	}
}
