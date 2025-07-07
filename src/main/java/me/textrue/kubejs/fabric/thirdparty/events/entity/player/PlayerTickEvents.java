package me.textrue.kubejs.fabric.thirdparty.events.entity.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public interface PlayerTickEvents {

	Event<PlayerTickEvents> PRE = EventFactory.createArrayBacked(PlayerTickEvents.class, (listeners) -> (player) -> {
		for (PlayerTickEvents listener : listeners) {
			listener.tick(player);
		}
	});

	Event<PlayerTickEvents> POST = EventFactory.createArrayBacked(PlayerTickEvents.class, (listeners) -> (player) -> {
		for (PlayerTickEvents listener : listeners) {
			listener.tick(player);
		}
	});

	void tick(Player player);
}
