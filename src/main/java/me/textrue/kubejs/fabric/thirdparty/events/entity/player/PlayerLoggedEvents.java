package me.textrue.kubejs.fabric.thirdparty.events.entity.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

public class PlayerLoggedEvents {

	public static Event<LoggedIn> LOGGED_IN = EventFactory.createArrayBacked(LoggedIn.class, callbacks -> (player) -> {
		for (var callback : callbacks) {
			callback.login(player);
		}
	});

	public static Event<LoggedOut> LOGGED_OUT = EventFactory.createArrayBacked(LoggedOut.class, callbacks -> (player) -> {
		for (var callback : callbacks) {
			callback.logout(player);
		}
	});

	@FunctionalInterface
	public interface LoggedIn {
		void login(ServerPlayer player);
	}

	@FunctionalInterface
	public interface LoggedOut {
		void logout(ServerPlayer player);
	}
}
