package dev.latvian.mods.kubejs.server;

import com.mojang.brigadier.CommandDispatcher;
import dev.latvian.mods.kubejs.command.CommandRegistryKubeEvent;
import dev.latvian.mods.kubejs.command.KubeJSCommands;
import dev.latvian.mods.kubejs.gui.chest.CustomChestMenu;
import dev.latvian.mods.kubejs.level.SimpleLevelKubeEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.LevelEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.web.LocalWebServer;
import dev.latvian.mods.kubejs.web.WebServerProperties;
import me.textrue.kubejs.fabric.thirdparty.events.CommandPerformEvent;
import me.textrue.kubejs.fabric.thirdparty.events.entity.player.ItemPickupEvents;
import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.UUID;

public class KubeJSServerEventHandler {
	private static final LevelResource PERSISTENT_DATA = new LevelResource("kubejs_persistent_data.nbt");

	public static void init() {
		CommandRegistrationCallback.EVENT.register(KubeJSServerEventHandler::registerCommands);
		ServerLifecycleEvents.SERVER_STARTING.register(KubeJSServerEventHandler::serverBeforeStart);
		ServerLifecycleEvents.SERVER_STOPPING.register(KubeJSServerEventHandler::serverStopping);
		ServerLifecycleEvents.SERVER_STOPPED.register(KubeJSServerEventHandler::serverStopped);
		ServerWorldEvents.LOAD.register(KubeJSServerEventHandler::serverLevelLoaded);
		CommandPerformEvent.EVENT.register(KubeJSServerEventHandler::command);
		ItemPickupEvents.PRE.register(KubeJSServerEventHandler::preventPickupDuringChestGUI);
	}

	public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
		KubeJSCommands.register(dispatcher);

		if (ServerEvents.COMMAND_REGISTRY.hasListeners()) {
			ServerEvents.COMMAND_REGISTRY.post(ScriptType.SERVER, new CommandRegistryKubeEvent(dispatcher, registryAccess, environment));
		}
	}

	public static void serverBeforeStart(MinecraftServer server) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER && !PlatformWrapper.isGeneratingData() && WebServerProperties.get().enabled && !WebServerProperties.get().publicAddress.isEmpty()) {
			LocalWebServer.start(server, false);
		}

		var p = server.getWorldPath(PERSISTENT_DATA);

		if (Files.exists(p)) {
			try {
				var tag = NbtIo.readCompressed(p, NbtAccounter.unlimitedHeap());

				if (tag != null) {
					var t = tag.getCompound("__restore_inventories");

					if (!t.isEmpty()) {
						tag.remove("__restore_inventories");

						var playerMap = server.kjs$restoreInventories();

						for (var key : t.getAllKeys()) {
							var list = t.getList(key, 10);
							var map = playerMap.computeIfAbsent(UUID.fromString(key), k -> new HashMap<>());

							for (var tag2 : list) {
								var slot = ((CompoundTag) tag2).getShort("Slot");
								var stack = ItemStack.parse(server.registryAccess(), tag2);

								if (stack.isPresent()) {
									map.put((int) slot, stack.get());
								}
							}
						}
					}

					server.kjs$getPersistentData().merge(tag);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void serverStarting(MinecraftServer server) {
		ServerEvents.LOADED.post(ScriptType.SERVER, new ServerKubeEvent(server));
	}

	public static void serverStopping(MinecraftServer server) {
		ServerEvents.UNLOADED.post(ScriptType.SERVER, new ServerKubeEvent(server));
	}

	public static void serverStopped(MinecraftServer server) {
		RegistryAccessContainer.current = RegistryAccessContainer.BUILTIN;
	}

	public static void serverLevelLoaded(MinecraftServer server, ServerLevel serverLevel) {
		if (serverLevel instanceof ServerLevel level && LevelEvents.LOADED.hasListeners(level.dimension())) {
			LevelEvents.LOADED.post(new SimpleLevelKubeEvent(level), level.dimension());
		}
	}

	public static void serverLevelSaved(ServerLevel serverLevel) {
		if (serverLevel instanceof ServerLevel level && LevelEvents.SAVED.hasListeners(level.dimension())) {
			LevelEvents.SAVED.post(new SimpleLevelKubeEvent(level), level.dimension());
		}

		if (serverLevel instanceof ServerLevel level && level.dimension() == Level.OVERWORLD) {
			var serverData = level.getServer().kjs$getPersistentData().copy();
			var p = level.getServer().getWorldPath(PERSISTENT_DATA);

			var playerMap = level.getServer().kjs$restoreInventories();

			if (!playerMap.isEmpty()) {
				var nbt = new CompoundTag();

				for (var entry : playerMap.entrySet()) {
					var list = new ListTag();

					for (var entry2 : entry.getValue().entrySet()) {
						var tag = new CompoundTag();
						tag.putShort("Slot", entry2.getKey().shortValue());
						entry2.getValue().save(level.registryAccess(), tag);
						list.add(tag);
					}

					nbt.put(entry.getKey().toString(), list);
				}

				serverData.put("__restore_inventories", nbt);
			}

			Util.ioPool().execute(() -> {
				try {
					NbtIo.writeCompressed(serverData, p);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
		}
	}

	public static ThirdPartyEventResult command(CommandPerformEvent event) {
		if (ServerEvents.COMMAND.hasListeners()) {
			var e = new CommandKubeEvent(event);

			if (ServerEvents.COMMAND.hasListeners(e.getCommandName())) {
				return ServerEvents.COMMAND.post(e, e.getCommandName()).compoundResult().result();
			}
		}
		return ThirdPartyEventResult.pass();
	}

	public static ResourceManagerReloadListener addReloadListeners(ReloadableServerResources resources) {
		return new KubeJSReloadListener(resources);
	}

	public static ThirdPartyEventResult preventPickupDuringChestGUI(Player player, ItemEntity entity, ItemStack stack) {
		if (player instanceof ServerPlayer serverPlayer && player.isAlive() && !serverPlayer.hasDisconnected() && player.containerMenu instanceof CustomChestMenu) {
			return ThirdPartyEventResult.interruptFalse();
		}
		return ThirdPartyEventResult.pass();
	}
}