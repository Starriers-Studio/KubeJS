package me.textrue.kubejs.fabric.thirdparty.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

import org.jetbrains.annotations.UnknownNullability;

/**
 * An interface designed to unify various things in the Minecraft
 * code base that can be serialized to and from a NBT tag.
 */
public interface NBTSerializable<T extends Tag> {
	@UnknownNullability
	T serializeNBT(HolderLookup.Provider provider);

	void deserializeNBT(HolderLookup.Provider provider, T nbt);
}
