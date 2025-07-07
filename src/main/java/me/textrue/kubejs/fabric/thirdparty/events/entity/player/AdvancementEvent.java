package me.textrue.kubejs.fabric.thirdparty.events.entity.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;

public interface AdvancementEvent {

	Event<AdvancementEvent> EVENT = EventFactory.createArrayBacked(AdvancementEvent.class, (listeners) -> (player, advancement) -> {
		for (AdvancementEvent listener : listeners) {
			listener.award(player, advancement);
		}
	});

	void award(ServerPlayer player, AdvancementHolder advancement);
}
