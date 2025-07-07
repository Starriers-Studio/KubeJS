package dev.latvian.mods.kubejs.recipe.viewer.server;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.Nullable;

public interface RemoteRecipeViewerDataUpdatedEvent {
	Event<RemoteRecipeViewerDataUpdatedEvent> EVENT = EventFactory.createArrayBacked(RemoteRecipeViewerDataUpdatedEvent.class, listeners -> data -> {
		for (var listener : listeners) {
			listener.dataUpdated(data);
		}
	});

	void dataUpdated(@Nullable RecipeViewerData data);
}
