package dev.latvian.mods.kubejs.plugin;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.function.Consumer;

/**
 * Base class for all KubeJS Plugin events
 */
public abstract class KubeJSPluginEvent {
	public static final Event<Consumer<KubeJSPluginEvent>> EVENT = EventFactory.createArrayBacked(Consumer.class, listeners -> event -> {
		for (Consumer<KubeJSPluginEvent> listener : listeners) {
			listener.accept(event);
		}
	});
}
