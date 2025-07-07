package me.textrue.kubejs.fabric.thirdparty.events.entity.player;

import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemPickupEvents {

	public static final Event<Pre> PRE = EventFactory.createArrayBacked(Pre.class, callbacks -> (player, entity, stack) -> {
		for (var callback : callbacks) {
			var result = callback.canPickup(player, entity, stack);
			if (result != ThirdPartyEventResult.pass()) {
				return result;
			}
		}
		return ThirdPartyEventResult.pass();
	});

	public static final Event<Post> POST = EventFactory.createArrayBacked(Post.class, callbacks -> (player, entity, stack) -> {
		for (var callback : callbacks) {
			callback.pickup(player, entity, stack);
		}
	});

	@FunctionalInterface
	public interface Pre {
		ThirdPartyEventResult canPickup(Player player, ItemEntity entity, ItemStack stack);
	}

	@FunctionalInterface
	public interface Post {
		void pickup(Player player, ItemEntity entity, ItemStack stack);
	}
}
