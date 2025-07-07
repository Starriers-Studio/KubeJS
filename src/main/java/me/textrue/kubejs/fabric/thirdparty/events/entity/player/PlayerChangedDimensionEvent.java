package me.textrue.kubejs.fabric.thirdparty.events.entity.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public interface PlayerChangedDimensionEvent {

	Event<PlayerChangedDimensionEvent> EVENT = EventFactory.createArrayBacked(PlayerChangedDimensionEvent.class, (listeners) -> (player, oldLevel, newLevel) -> {
		for (PlayerChangedDimensionEvent listener : listeners) {
			listener.change(player, oldLevel, newLevel);
		}
	});

	void change(ServerPlayer player, ResourceKey<Level> oldLevel, ResourceKey<Level> newLevel);
}
