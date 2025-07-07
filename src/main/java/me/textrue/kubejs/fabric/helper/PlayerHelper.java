package me.textrue.kubejs.fabric.helper;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerHelper {
	public static boolean isFakePlayer(Player player) {
		return player instanceof FakePlayer;
	}
}
