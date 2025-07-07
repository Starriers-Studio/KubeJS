package dev.latvian.mods.kubejs;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class StartupScriptManager extends ScriptManager {
	public StartupScriptManager() {
		super(ScriptType.STARTUP);
	}

	@Override
	public void loadFromDirectory() {
		super.loadFromDirectory();

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			loadPackFromDirectory(KubeJSPaths.LOCAL_STARTUP_SCRIPTS, "local startup", true);
		}
	}
}
