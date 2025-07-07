package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.util.ScheduledEvents;
import me.textrue.kubejs.fabric.thirdparty.util.ServerLifecycleHooks;
import net.minecraft.server.MinecraftServer;

public class ScheduledServerEvent extends ScheduledEvents.ScheduledEvent {
	public static final ScheduledEvents EVENTS = new ScheduledEvents(ScheduledServerEvent::new);

	public MinecraftServer getServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}
}