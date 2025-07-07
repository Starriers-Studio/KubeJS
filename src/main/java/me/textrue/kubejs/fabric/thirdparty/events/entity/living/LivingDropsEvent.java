package me.textrue.kubejs.fabric.thirdparty.events.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;

public interface LivingDropsEvent {

	Event<LivingDropsEvent> EVENT = EventFactory.createArrayBacked(LivingDropsEvent.class, listeners -> (entity, source, drops, recentlyHit) -> {
		for (LivingDropsEvent listener : listeners) {
			return listener.onLivingEntityDrops(entity, source, drops, recentlyHit);
		}
		return false;
	});


	boolean onLivingEntityDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, boolean recentlyHit);
}
