package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.plugin.builtin.event.PlayerEvents;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import me.textrue.kubejs.fabric.helper.NetworkHelper;
import me.textrue.kubejs.fabric.thirdparty.events.entity.player.AdvancementEvent;
import me.textrue.kubejs.fabric.thirdparty.events.entity.player.PlayerChangedDimensionEvent;
import me.textrue.kubejs.fabric.thirdparty.events.entity.player.PlayerCloneEvent;
import me.textrue.kubejs.fabric.thirdparty.events.entity.player.PlayerContainerEvents;
import me.textrue.kubejs.fabric.thirdparty.events.entity.player.PlayerLoggedEvents;
import me.textrue.kubejs.fabric.thirdparty.events.entity.player.PlayerRespawnEvent;
import me.textrue.kubejs.fabric.thirdparty.events.ServerChatEvents;
import me.textrue.kubejs.fabric.thirdparty.mixin.accessors.MinecraftServerAccessor;
import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.stream.Stream;

public class KubeJSPlayerEventHandler {

	public static void init() {
		PlayerLoggedEvents.LOGGED_IN.register(KubeJSPlayerEventHandler::loggedIn);
		PlayerCloneEvent.EVENT.register(KubeJSPlayerEventHandler::cloned);
		PlayerRespawnEvent.EVENT.register(KubeJSPlayerEventHandler::respawn);
		PlayerLoggedEvents.LOGGED_OUT.register(KubeJSPlayerEventHandler::loggedOut);
		ServerChatEvents.RECEIVED.register(KubeJSPlayerEventHandler::chatReceived);
		ServerChatEvents.DECORATE.register(KubeJSPlayerEventHandler::chatDecorate);
		AdvancementEvent.EVENT.register(KubeJSPlayerEventHandler::advancement);
		PlayerContainerEvents.OPEN.register(KubeJSPlayerEventHandler::inventoryOpened);
		PlayerContainerEvents.CLOSE.register(KubeJSPlayerEventHandler::inventoryClosed);
		PlayerChangedDimensionEvent.EVENT.register((player, oldLevel, newLevel) -> {
			KubeJSPlayerEventHandler.dimensionChanged(player);
		});
	}

	public static void datapackSync(PlayerList playerList, ServerPlayer serverPlayer) {
		var payload = ((MinecraftServerAccessor) playerList.getServer()).getServerResources().managers().kjs$getServerScriptManager().serverData;
		var relevantPlayers = serverPlayer == null ? playerList.getPlayers().stream() : Stream.of(serverPlayer);
		relevantPlayers.forEach(player -> NetworkHelper.sendToPlayer(player, payload));
	}

	public static void loggedIn(ServerPlayer serverPlayer) {
		if (serverPlayer instanceof ServerPlayer player) {
			if (PlayerEvents.LOGGED_IN.hasListeners()) {
				PlayerEvents.LOGGED_IN.post(ScriptType.SERVER, new SimplePlayerKubeEvent(player));
			}

			player.inventoryMenu.addSlotListener(player.kjs$getInventoryChangeListener());

			if (!ConsoleJS.SERVER.errors.isEmpty() && !CommonProperties.get().hideServerScriptErrors) {
				player.displayClientMessage(ConsoleJS.SERVER.errorsComponent("/kubejs errors server"), false);
			}

			player.kjs$getStages().sync();
		}
	}

	public static void cloned(ServerPlayer original, ServerPlayer serverPlayer, boolean wonGame) {
		if (original instanceof ServerPlayer oldPlayer && serverPlayer instanceof ServerPlayer newPlayer) {
			newPlayer.kjs$setRawPersistentData(oldPlayer.kjs$getRawPersistentData());
			newPlayer.inventoryMenu.addSlotListener(newPlayer.kjs$getInventoryChangeListener()); // move this to respawn?

			if (PlayerEvents.CLONED.hasListeners()) {
				PlayerEvents.CLONED.post(ScriptType.SERVER, new PlayerClonedKubeEvent(newPlayer, oldPlayer, wonGame));
			}
		}
	}

	public static void respawn(ServerPlayer newPlayer, boolean conqueredEnd, Entity.RemovalReason removalReason) {
		if (newPlayer instanceof ServerPlayer player) {
			if (PlayerEvents.RESPAWNED.hasListeners()) {
				PlayerEvents.RESPAWNED.post(ScriptType.SERVER, new PlayerRespawnedKubeEvent(player, conqueredEnd));
			}

			player.kjs$getStages().sync();
		}
	}

	public static void loggedOut(ServerPlayer serverPlayer) {
		if (PlayerEvents.LOGGED_OUT.hasListeners() && serverPlayer instanceof ServerPlayer player) {
			PlayerEvents.LOGGED_OUT.post(ScriptType.SERVER, new SimplePlayerKubeEvent(player));
		}
	}

	public static void tick(Player tickPlayer) {
		if (PlayerEvents.TICK.hasListeners() && tickPlayer instanceof ServerPlayer player) {
			PlayerEvents.TICK.post(player, new SimplePlayerKubeEvent(player));
		}
	}

	public static void chatDecorate(ServerPlayer serverPlayer, ServerChatEvents.ChatComponent chatComponent) {
		if (PlayerEvents.DECORATE_CHAT.hasListeners()) {
			PlayerEvents.DECORATE_CHAT.post(ScriptType.SERVER, new PlayerChatReceivedKubeEvent(serverPlayer, chatComponent));
		}
	}

	public static ThirdPartyEventResult chatReceived(ServerPlayer serverPlayer, ServerChatEvents.ChatComponent chatComponent) {
		if (PlayerEvents.CHAT.hasListeners()) {
			return PlayerEvents.CHAT.post(ScriptType.SERVER, new PlayerChatReceivedKubeEvent(serverPlayer, chatComponent)).compoundResult().result();
		}
		return ThirdPartyEventResult.pass();
	}

	public static void advancement(ServerPlayer serverPlayer, AdvancementHolder advancement) {
		var id = advancement.id();

		if (PlayerEvents.ADVANCEMENT.hasListeners(id) && serverPlayer instanceof ServerPlayer player) {
			PlayerEvents.ADVANCEMENT.post(new PlayerAdvancementKubeEvent(player, player.server.kjs$getAdvancement(id)), id);
		}
	}

	public static void inventoryOpened(Player inventoryPlayer, AbstractContainerMenu menu) {
		if (inventoryPlayer instanceof ServerPlayer player) {
			if (!(menu instanceof InventoryMenu)) {
				menu.addSlotListener(player.kjs$getInventoryChangeListener());
			}

			ResourceKey<MenuType<?>> key;

			try {
				key = menu.getType().kjs$getKey();
			} catch (Exception ex) {
				return;
			}

			if (key != null) {
				if (PlayerEvents.INVENTORY_OPENED.hasListeners(key)) {
					PlayerEvents.INVENTORY_OPENED.post(player, key, new InventoryKubeEvent(player, menu));
				}

				if (menu instanceof ChestMenu && PlayerEvents.CHEST_OPENED.hasListeners(key)) {
					PlayerEvents.CHEST_OPENED.post(player, key, new ChestKubeEvent(player, menu));
				}
			}
		}
	}

	public static void inventoryClosed(Player inventoryPlayer, AbstractContainerMenu menu) {
		if (inventoryPlayer instanceof ServerPlayer player) {
			ResourceKey<MenuType<?>> key;

			try {
				key = menu.getType().kjs$getKey();
			} catch (Exception ex) {
				return;
			}

			if (key != null) {
				if (PlayerEvents.INVENTORY_CLOSED.hasListeners(key)) {
					PlayerEvents.INVENTORY_CLOSED.post(player, key, new InventoryKubeEvent(player, menu));
				}

				if (menu instanceof ChestMenu && PlayerEvents.CHEST_CLOSED.hasListeners(key)) {
					PlayerEvents.CHEST_CLOSED.post(player, key, new ChestKubeEvent(player, menu));
				}
			}
		}
	}

	public static void dimensionChanged(Player player) {
		try {
			player.kjs$getStages().sync();
		} catch (Exception ignored) {
		}
	}
}