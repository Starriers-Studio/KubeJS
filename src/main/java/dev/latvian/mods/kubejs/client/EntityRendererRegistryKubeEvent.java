package dev.latvian.mods.kubejs.client;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EntityRendererRegistryKubeEvent implements ClientKubeEvent {
	public EntityRendererRegistryKubeEvent() {
	}

	public void register(EntityType<?> type, EntityRendererProvider renderer) {
		EntityRendererRegistry.register(type, renderer);
	}
}
