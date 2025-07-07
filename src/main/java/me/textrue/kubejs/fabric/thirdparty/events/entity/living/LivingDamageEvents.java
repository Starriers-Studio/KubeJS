package me.textrue.kubejs.fabric.thirdparty.events.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class LivingDamageEvents {

	public static final Event<Pre> PRE = EventFactory.createArrayBacked(Pre.class, listeners -> (livingEntity, source, damage) -> {
		for (Pre listener : listeners) {
			return listener.modifyDamage(livingEntity, source, damage);
		}
		return damage;
	});

	public static final Event<Post> POST = EventFactory.createArrayBacked(Post.class, listeners -> (livingEntity, source, damage) -> {
		for (Post listener : listeners) {
			listener.damaged(livingEntity, source, damage);
		}
	});

	public interface Pre {
		float modifyDamage(LivingEntity livingEntity, DamageSource source, float damage);
	}

	public interface Post {
		void damaged(LivingEntity livingEntity, DamageSource source, float damage);
	}
}
