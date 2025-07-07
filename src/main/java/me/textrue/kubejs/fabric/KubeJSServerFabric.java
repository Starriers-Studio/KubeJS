package me.textrue.kubejs.fabric;

import dev.latvian.mods.kubejs.KubeJSModEventHandler;
import net.fabricmc.api.DedicatedServerModInitializer;

public class KubeJSServerFabric implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		KubeJSModEventHandler.loadComplete();
	}
}
