package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachmentInfo;
import dev.latvian.mods.kubejs.block.entity.BlockEntityBuilder;
import dev.latvian.mods.kubejs.block.entity.KubeBlockEntity;
import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabCallbackFabric;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabKubeEvent;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.plugin.builtin.event.StartupEvents;
import dev.latvian.mods.kubejs.registry.RegistryObjectStorage;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptsLoadedEvent;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;


public class KubeJSModEventHandler {

	public static void init() {
		ItemGroupEvents.MODIFY_ENTRIES_ALL.register(KubeJSModEventHandler::creativeTab);
		registerCapabilities();
	}

	public static void creativeTab(CreativeModeTab group, FabricItemGroupEntries entries) {
		var tabId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(group);

		if (StartupEvents.MODIFY_CREATIVE_TAB.hasListeners(tabId)) {
			StartupEvents.MODIFY_CREATIVE_TAB.post(ScriptType.STARTUP, tabId, new CreativeTabKubeEvent(group, entries.shouldShowOpRestrictedItems(), new CreativeTabCallbackFabric(entries)));
		}
	}

	public static void loadComplete() {
		KubeJSPlugins.forEachPlugin(KubeJSPlugin::afterInit);
		ScriptsLoadedEvent.EVENT.invoker().run();
		StartupEvents.POST_INIT.post(ScriptType.STARTUP, KubeStartupEvent.BASIC);
		UtilsJS.postModificationEvents();

		if (!ConsoleJS.STARTUP.errors.isEmpty()) {
			var list = new ArrayList<String>();
			list.add("Startup script errors:");

			var lines = ConsoleJS.STARTUP.errors.toArray(ConsoleLine.EMPTY_ARRAY);

			for (int i = 0; i < lines.length; i++) {
				list.add((i + 1) + ") " + lines[i]);
			}

			KubeJS.LOGGER.error(String.join("\n", list));

			ConsoleJS.STARTUP.flush(true);

			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER || !CommonProperties.get().startupErrorGUI) {
				throw new RuntimeException("There were KubeJS startup script syntax errors! See logs/kubejs/startup.log for more info");
			}
		}

		ConsoleJS.STARTUP.stopCapturingErrors();
		ConsoleJS.CLIENT.stopCapturingErrors();

		// TODO: NEED UPDATE CHECK
		/*
		Util.nonCriticalIoPool().submit(() -> {
			try {
				var response = HttpClient.newBuilder()
					.followRedirects(HttpClient.Redirect.ALWAYS)
					.connectTimeout(Duration.ofSeconds(5L))
					.build()
					.send(HttpRequest.newBuilder().uri(URI.create("https://v.kubejs.com/update-check?" + KubeJS.QUERY)).GET().build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
				if (response.statusCode() == 200) {
					var body = response.body().trim();

					if (!body.isEmpty()) {
						ConsoleJS.STARTUP.info("Update available: " + body);
					}
				}
			} catch (Exception ignored) {
			}
		});
		 */
	}

	private record KubeEntityCapabilityProvider<CAP, SRC>(BlockApiLookup<CAP, SRC> capability, BlockEntityAttachmentInfo attachment) implements BlockApiLookup.BlockEntityApiProvider<CAP, SRC> {

		@Override
		public @Nullable CAP find(BlockEntity blockEntity, SRC context) {
			if (attachment.directions().isEmpty() || (context instanceof Direction d && attachment.directions().contains(d))) {
				if (blockEntity.getLevel() instanceof ServerLevel level) {
					return capability.find(level, blockEntity.getBlockPos(), context);
				}
			}

			return null;
		}
	}

	public static void registerCapabilities() {
		for (var info : RegistryObjectStorage.BLOCK_ENTITY.objects.values().stream()
			.filter(BlockEntityBuilder.class::isInstance)
			.map(b -> ((BlockEntityBuilder) b).info).toList()) {
			for (var attachment : info.attachments.values()) {
				for (var capability : attachment.factory().getCapabilities()) {
					if (capability instanceof BlockApiLookup<?,?> lookup) {
						lookup.registerForBlockEntities(new KubeEntityCapabilityProvider(lookup, attachment), (BlockEntityType<KubeBlockEntity>) info.entityType);
					}
				}
			}
		}
	}
}
