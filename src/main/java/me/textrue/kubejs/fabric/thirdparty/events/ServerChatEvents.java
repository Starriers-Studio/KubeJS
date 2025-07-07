package me.textrue.kubejs.fabric.thirdparty.events;

import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class ServerChatEvents {

	public static final Event<Decorate> DECORATE = EventFactory.createArrayBacked(Decorate.class, decorates -> (player, component) -> {
		for (Decorate decorate : decorates) {
			decorate.decorate(player, component);
		}
	});

	public static final Event<Received> RECEIVED = EventFactory.createArrayBacked(Received.class, listeners -> (player, component) -> {
		for (Received listener : listeners) {
			ThirdPartyEventResult result = listener.received(player, component);
			if (result != ThirdPartyEventResult.pass()) {
				return result;
			}
		}
		return ThirdPartyEventResult.pass();
	});

	@FunctionalInterface
	public interface Decorate {
		void decorate(@Nullable ServerPlayer player, ChatComponent component);
	}

	@FunctionalInterface
	public interface Received {
		ThirdPartyEventResult received(@Nullable ServerPlayer player, ChatComponent component);
	}

	public interface ChatComponent {
		Component getMessage();

		void setMessage(Component component);
	}
}
