package me.textrue.kubejs.fabric.thirdparty.holdersets;

import net.minecraft.core.HolderSet;

public interface ICustomHolderSet<T> extends HolderSet<T> {
	HolderSetType type();

	@Override
	default SerializationType serializationType() {
		return SerializationType.OBJECT;
	}
}
