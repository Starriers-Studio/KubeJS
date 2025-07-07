package dev.latvian.mods.kubejs.player;

import me.textrue.kubejs.fabric.thirdparty.events.ServerChatEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PlayerChatReceivedKubeEvent implements KubePlayerEvent {
	private final ServerPlayer serverPlayer;
	private final ServerChatEvents.ChatComponent chatComponent;

	public PlayerChatReceivedKubeEvent(ServerPlayer serverPlayer, ServerChatEvents.ChatComponent chatComponent) {
		this.serverPlayer = serverPlayer;
		this.chatComponent = chatComponent;
	}

	@Override
	public ServerPlayer getEntity() {
		return serverPlayer;
	}

	public String getUsername() {
		return serverPlayer.getGameProfile().getName();
	}

	public String getMessage() {
		return chatComponent.getMessage().getString();
	}

	public Component getComponent() {
		return chatComponent.getMessage();
	}

	public void setComponent(Component component) {
		chatComponent.setMessage(component);
	}
}