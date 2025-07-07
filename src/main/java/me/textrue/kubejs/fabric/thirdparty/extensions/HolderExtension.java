package me.textrue.kubejs.fabric.thirdparty.extensions;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

/**
 * Extension for {@link Holder}
 */
public interface HolderExtension<T> {
	/**
	 * Get the resource key held by this Holder, or null if none is present. This method will be overriden
	 * by Holder implementations to avoid allocation associated with {@link Holder#unwrapKey()}
	 */
	@Nullable
	default ResourceKey<T> getKey() {
		return ((Holder<T>) this).unwrapKey().orElse(null);
	}
}
