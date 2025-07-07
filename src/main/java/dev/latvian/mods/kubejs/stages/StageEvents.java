package dev.latvian.mods.kubejs.stages;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface StageEvents {
	Event<Consumer<StageCreationEvent>> OVERRIDE_CREATION = EventFactory.createArrayBacked(Consumer.class, listeners -> event -> {
		for (var listener : listeners) {
			listener.accept(event);
		}
	});

	static Stages create(Player player) {
		if (player instanceof FakePlayer) {
			return NoStages.NULL_INSTANCE;
		}

		var event = new StageCreationEvent(player);
		OVERRIDE_CREATION.invoker().accept(event);
		return event.getPlayerStages() == null ? new TagWrapperStages(player) : event.getPlayerStages();
	}

	static Stages get(@Nullable Player player) {
		return player == null ? NoStages.NULL_INSTANCE : player.kjs$getStages();
	}
}
