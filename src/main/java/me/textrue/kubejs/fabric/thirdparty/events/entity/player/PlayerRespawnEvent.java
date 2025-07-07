package me.textrue.kubejs.fabric.thirdparty.events.entity.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface PlayerRespawnEvent {

	Event<PlayerRespawnEvent> EVENT = EventFactory.createArrayBacked(PlayerRespawnEvent.class, (listeners) -> (newPlayer, conqueredEnd, removalReason) -> {
		for (PlayerRespawnEvent listener : listeners) {
			listener.respawn(newPlayer, conqueredEnd, removalReason);
		}
	});

	void respawn(ServerPlayer newPlayer, boolean conqueredEnd, Entity.RemovalReason removalReason);
}
