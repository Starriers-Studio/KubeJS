package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.plugin.builtin.event.LevelEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import me.textrue.kubejs.fabric.thirdparty.events.FabricExplosionEvents;
import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.List;

public class KubeJSWorldEventHandler {

	public static void init() {
		ServerWorldEvents.LOAD.register(KubeJSWorldEventHandler::serverLevelLoad);
		ServerWorldEvents.UNLOAD.register(KubeJSWorldEventHandler::serverLevelUnload);
		ServerTickEvents.END_WORLD_TICK.register(KubeJSWorldEventHandler::serverTickEvent);
		FabricExplosionEvents.START.register(KubeJSWorldEventHandler::preExplosion);
		FabricExplosionEvents.DETONATE.register(KubeJSWorldEventHandler::detonateExplosion);
	}

	public static void serverLevelLoad(MinecraftServer server, ServerLevel world) {
		if (world instanceof ServerLevel level && LevelEvents.LOADED.hasListeners(level.dimension())) {
			LevelEvents.LOADED.post(new SimpleLevelKubeEvent(level), level.dimension());
		}
	}

	public static void serverLevelUnload(MinecraftServer server, ServerLevel world) {
		if (world instanceof ServerLevel level && LevelEvents.UNLOADED.hasListeners(level.dimension())) {
			LevelEvents.UNLOADED.post(new SimpleLevelKubeEvent(level), level.dimension());
		}
	}

	public static void serverTickEvent(ServerLevel world) {
		if (world instanceof ServerLevel level && LevelEvents.TICK.hasListeners(level.dimension())) {
			LevelEvents.TICK.post(ScriptType.SERVER, level.dimension(), new SimpleLevelKubeEvent(level));
		}
	}

	public static ThirdPartyEventResult preExplosion(Level world, Explosion explosion) {
		if (world instanceof ServerLevel level && LevelEvents.BEFORE_EXPLOSION.hasListeners(level.dimension())) {
			return LevelEvents.BEFORE_EXPLOSION.post(level, level.dimension(), new ExplosionKubeEvent.Before(level, explosion)).compoundResult().result();
		}
		return ThirdPartyEventResult.pass();
	}

	public static void detonateExplosion(Level world, Explosion explosion, List<Entity> entityList) {
		if (world instanceof ServerLevel level && LevelEvents.AFTER_EXPLOSION.hasListeners(level.dimension())) {
			LevelEvents.AFTER_EXPLOSION.post(level, level.dimension(), new ExplosionKubeEvent.After(level, explosion, entityList));
		}
	}
}