package dev.latvian.mods.kubejs.block.entity;

import me.textrue.kubejs.fabric.thirdparty.util.NBTSerializable;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface BlockEntityAttachment {
	default Object getWrappedObject() {
		return this;
	}

	@Nullable
	default <CAP, SRC> CAP getCapability(BlockApiLookup<CAP, SRC> capability) {
		return null;
	}

	@Nullable
	default Tag serialize(HolderLookup.Provider registries) {
		if (getWrappedObject() instanceof NBTSerializable<?> s) {
			return s.serializeNBT(registries);
		}

		return null;
	}

	default void deserialize(HolderLookup.Provider registries, @Nullable Tag tag) {
		if (tag != null && getWrappedObject() instanceof NBTSerializable s) {
			s.deserializeNBT(registries, tag);
		}
	}

	default void onRemove(ServerLevel level, KubeBlockEntity blockEntity, BlockState newState) {
	}

	default void serverTick() {
	}
}
