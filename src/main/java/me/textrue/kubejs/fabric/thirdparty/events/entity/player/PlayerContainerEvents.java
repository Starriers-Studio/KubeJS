package me.textrue.kubejs.fabric.thirdparty.events.entity.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class PlayerContainerEvents {

	public static final Event<Open> OPEN = EventFactory.createArrayBacked(Open.class, listeners -> (player, menu) -> {
		for (Open listener : listeners) {
			listener.open(player, menu);
		}
	});

	public static final Event<Close> CLOSE = EventFactory.createArrayBacked(Close.class, listeners -> (player, menu) -> {
		for (Close listener : listeners) {
			listener.close(player, menu);
		}
	});

	@FunctionalInterface
	public interface Open {
		void open(Player player, AbstractContainerMenu menu);
	}

	@FunctionalInterface
	public interface Close {
		void close(Player player, AbstractContainerMenu menu);
	}
}
