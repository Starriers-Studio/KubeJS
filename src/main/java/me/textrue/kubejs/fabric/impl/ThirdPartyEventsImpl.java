package me.textrue.kubejs.fabric.impl;

import me.textrue.kubejs.fabric.thirdparty.events.ServerChatEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

public class ThirdPartyEventsImpl {
	public static void init() {
		ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.CONTENT_PHASE, (player, component) -> {
			ServerChatEvents.ChatComponent chatComponent = new ChatComponentImpl(component);
			ServerChatEvents.DECORATE.invoker().decorate(player, chatComponent);
			return chatComponent.getMessage();
		});
		ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
			ServerChatEvents.ChatComponent chatComponent = new ChatComponentImpl(message.decoratedContent());
			return !ServerChatEvents.RECEIVED.invoker().received(sender, chatComponent).isFalse();
		});
	}
}
