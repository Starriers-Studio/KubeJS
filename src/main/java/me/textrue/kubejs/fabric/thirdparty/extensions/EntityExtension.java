package me.textrue.kubejs.fabric.thirdparty.extensions;

import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;

public interface EntityExtension {
	default Collection<ItemEntity> kjs$captureDrops() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Collection<ItemEntity> kjs$captureDrops(Collection<ItemEntity> value) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
