package me.textrue.kubejs.fabric.thirdparty.events.entity.living;

import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface LivingDeathEvent {

	Event<LivingDeathEvent> EVENT = EventFactory.createArrayBacked(LivingDeathEvent.class, listeners -> (entity, source) -> {
		for (LivingDeathEvent listener : listeners) {
			return listener.die(entity, source);
		}
		return ThirdPartyEventResult.pass();
	});

	ThirdPartyEventResult die(LivingEntity entity, DamageSource source);
}
