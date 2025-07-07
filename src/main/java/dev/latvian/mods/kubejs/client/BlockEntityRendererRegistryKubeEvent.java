package dev.latvian.mods.kubejs.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BlockEntityRendererRegistryKubeEvent implements ClientKubeEvent {
	public BlockEntityRendererRegistryKubeEvent() {
	}

	public void register(BlockEntityType<?> type, BlockEntityRendererProvider renderer) {
		BlockEntityRenderers.register(type, renderer);
	}
}
