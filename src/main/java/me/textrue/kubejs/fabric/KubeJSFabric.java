package me.textrue.kubejs.fabric;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSModEventHandler;
import dev.latvian.mods.kubejs.block.KubeJSBlockEventHandler;
import dev.latvian.mods.kubejs.entity.KubeJSEntityEventHandler;
import dev.latvian.mods.kubejs.item.KubeJSItemEventHandler;
import dev.latvian.mods.kubejs.level.KubeJSWorldEventHandler;
import dev.latvian.mods.kubejs.player.KubeJSPlayerEventHandler;
import dev.latvian.mods.kubejs.server.KubeJSServerEventHandler;
import me.textrue.kubejs.fabric.impl.ThirdPartyEventsImpl;
import net.fabricmc.api.ModInitializer;

public class KubeJSFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		try {
			KubeJS.instance = new KubeJS();
			KubeJS.instance.setup();
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}

		ThirdPartyEventsImpl.init();
		KubeJSModEventHandler.init();
		KubeJSBlockEventHandler.init();
		KubeJSEntityEventHandler.init();
		KubeJSItemEventHandler.init();
		KubeJSWorldEventHandler.init();
		KubeJSPlayerEventHandler.init();
		KubeJSServerEventHandler.init();
	}
}
