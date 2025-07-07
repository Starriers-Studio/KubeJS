package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemDestroyedKubeEvent implements KubePlayerEvent {
	private final Player player;
	private final ItemStack item;
	private final InteractionHand hand;

	public ItemDestroyedKubeEvent(Player player, ItemStack item, InteractionHand hand) {
		this.player = player;
		this.item = item;
		this.hand = hand;
	}

	@Override
	public Player getEntity() {
		return player;
	}

	@Nullable
	public InteractionHand getHand() {
		return hand;
	}

	public ItemStack getItem() {
		return item;
	}
}