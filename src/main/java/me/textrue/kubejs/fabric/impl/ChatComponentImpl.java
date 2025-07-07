package me.textrue.kubejs.fabric.impl;

import me.textrue.kubejs.fabric.thirdparty.events.ServerChatEvents;
import net.minecraft.network.chat.Component;

public class ChatComponentImpl implements ServerChatEvents.ChatComponent {
	private Component component;

	public ChatComponentImpl(Component component) {
		this.component = component;
	}

	@Override
	public Component getMessage() {
		return component;
	}

	@Override
	public void setMessage(Component component) {
		this.component = component;
	}
}
