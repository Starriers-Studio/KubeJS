package dev.latvian.mods.kubejs.block.entity;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;

import java.util.List;

public interface BlockEntityAttachmentFactory {
	BlockEntityAttachment create(BlockEntityAttachmentInfo info, KubeBlockEntity entity);

	default List<BlockApiLookup<?, ?>> getCapabilities() {
		return List.of();
	}

	default boolean isTicking() {
		return false;
	}
}
