package me.textrue.kubejs.fabric.thirdparty.events;

import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.List;

public class FabricExplosionEvents {

	public static final Event<Start> START = EventFactory.createArrayBacked(Start.class, callbacks -> (level, explosion) -> {
		for (Start callback : callbacks) {
			return callback.onStart(level, explosion);
		}
		return ThirdPartyEventResult.pass();
	});

	public static final Event<Detonate> DETONATE = EventFactory.createArrayBacked(Detonate.class, callbacks -> (level, explosion, entityList) -> {
		for (Detonate callback : callbacks) {
			callback.onDetonate(level, explosion, entityList);
		}
	});

	public interface Start {
		ThirdPartyEventResult onStart(Level level, Explosion explosion);
	}

	public interface Detonate {
		void onDetonate(Level level, Explosion explosion, List<Entity> entityList);
	}
}
