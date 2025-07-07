package me.textrue.kubejs.fabric.thirdparty.events.entity.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

public interface PlayerCloneEvent {

	Event<PlayerCloneEvent> EVENT = EventFactory.createArrayBacked(PlayerCloneEvent.class, (listeners) -> (oldPlayer, newPlayer, wonGame) -> {
		for (PlayerCloneEvent listener : listeners) {
			listener.clone(oldPlayer, newPlayer, wonGame);
		}
	});

	void clone(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean wonGame);
}
