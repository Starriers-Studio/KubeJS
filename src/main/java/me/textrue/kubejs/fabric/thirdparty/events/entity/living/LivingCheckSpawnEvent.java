package me.textrue.kubejs.fabric.thirdparty.events.entity.living;

import me.textrue.kubejs.fabric.thirdparty.extensions.IOwnedSpawner;
import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

public interface LivingCheckSpawnEvent {

	Event<LivingCheckSpawnEvent> EVENT = EventFactory.createArrayBacked(LivingCheckSpawnEvent.class, (listeners) -> (entity, level, x, y, z, difficulty, spawner) -> {
		for (LivingCheckSpawnEvent listener : listeners) {
			return listener.checkSpawn(entity, level, x, y, z, difficulty, spawner);
		}
		return ThirdPartyEventResult.pass();
	});

	ThirdPartyEventResult checkSpawn(LivingEntity entity, LevelAccessor world, double x, double y, double z, MobSpawnType type, @Nullable IOwnedSpawner spawner);
}
