package me.textrue.kubejs.fabric;

import dev.latvian.mods.kubejs.KubeJSModEventHandler;
import dev.latvian.mods.kubejs.client.KubeJSGameClientEventHandler;
import dev.latvian.mods.kubejs.client.KubeJSModClientEventHandler;
import net.fabricmc.api.ClientModInitializer;

public class KubeJSClientFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		KubeJSModClientEventHandler.setupClient();
		KubeJSModClientEventHandler.init();
		KubeJSGameClientEventHandler.init();
		KubeJSModEventHandler.loadComplete();
	}
}
