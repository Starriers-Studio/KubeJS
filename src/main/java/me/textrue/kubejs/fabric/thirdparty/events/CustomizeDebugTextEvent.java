package me.textrue.kubejs.fabric.thirdparty.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.List;

public class CustomizeDebugTextEvent {

	public static final Event<DebugText> LEFT = EventFactory.createArrayBacked(DebugText.class, (listeners) -> (strings) -> {
		for (DebugText listener : listeners) {
			listener.gatherText(strings);
		}
	});

	public static final Event<DebugText> RIGHT = EventFactory.createArrayBacked(DebugText.class, (listeners) -> (strings) -> {
		for (DebugText listener : listeners) {
			listener.gatherText(strings);
		}
	});

	@Environment(EnvType.CLIENT)
	public interface DebugText {
		void gatherText(List<String> strings);
	}
}
