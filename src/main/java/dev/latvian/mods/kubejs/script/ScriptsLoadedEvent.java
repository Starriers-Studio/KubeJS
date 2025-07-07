package dev.latvian.mods.kubejs.script;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class ScriptsLoadedEvent {
	public static final Event<Runnable> EVENT = EventFactory.createArrayBacked(Runnable.class, runnables -> () -> {
		for (Runnable runnable : runnables) {
			runnable.run();
		}
	});
}