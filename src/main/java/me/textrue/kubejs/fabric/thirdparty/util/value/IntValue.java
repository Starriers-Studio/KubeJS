package me.textrue.kubejs.fabric.thirdparty.util.value;

import it.unimi.dsi.fastutil.ints.IntConsumer;

import java.util.function.IntSupplier;

public interface IntValue extends Value<Integer>, IntSupplier, IntConsumer {
	@Override
	default Integer get() {
		return getAsInt();
	}
}
