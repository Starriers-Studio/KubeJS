package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.plugin.builtin.event.EntityEvents;
import me.textrue.kubejs.fabric.thirdparty.events.entity.living.LivingCheckSpawnEvent;
import me.textrue.kubejs.fabric.thirdparty.events.entity.living.LivingDamageEvents;
import me.textrue.kubejs.fabric.thirdparty.events.entity.living.LivingDeathEvent;
import me.textrue.kubejs.fabric.thirdparty.events.entity.living.LivingDropsEvent;
import me.textrue.kubejs.fabric.thirdparty.extensions.IOwnedSpawner;
import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class KubeJSEntityEventHandler {

	public static void init() {
		LivingCheckSpawnEvent.EVENT.register(KubeJSEntityEventHandler::checkSpawn);
		LivingDeathEvent.EVENT.register(KubeJSEntityEventHandler::livingDeath);
		LivingDamageEvents.PRE.register(KubeJSEntityEventHandler::beforeLivingHurt);
		LivingDamageEvents.POST.register(KubeJSEntityEventHandler::afterLivingHurt);
		ServerEntityEvents.ENTITY_LOAD.register(KubeJSEntityEventHandler::entitySpawned);
		LivingDropsEvent.EVENT.register(KubeJSEntityEventHandler::livingDrops);
	}

	public static ThirdPartyEventResult checkSpawn(LivingEntity entity, LevelAccessor world, double x, double y, double z, MobSpawnType type, @Nullable IOwnedSpawner spawner) {
		var key = entity.getType().kjs$getKey();

		if (world instanceof ServerLevel level && EntityEvents.CHECK_SPAWN.hasListeners(key)) {
			var result = EntityEvents.CHECK_SPAWN.post(level, key, new CheckLivingEntitySpawnKubeEvent(
				entity,
				level,
				x,
				y,
				z,
				type,
				spawner.getOwner()
			));

			if (result.interruptFalse() || result.interruptTrue()) {
				return ThirdPartyEventResult.interrupt(result.interruptFalse());
			}
		}
		return ThirdPartyEventResult.pass();
	}

	public static ThirdPartyEventResult livingDeath(LivingEntity entity, DamageSource source) {
		var key = entity.getType().kjs$getKey();

		if (EntityEvents.DEATH.hasListeners(key)) {
			return EntityEvents.DEATH.post(entity, key, new LivingEntityDeathKubeEvent(entity, source)).compoundResult().result();
		}
		return ThirdPartyEventResult.pass();
	}

	public static float beforeLivingHurt(LivingEntity entity, DamageSource source, float amount) {
		var key = entity.getType().kjs$getKey();

		if (EntityEvents.BEFORE_HURT.hasListeners(key)) {
			if (EntityEvents.BEFORE_HURT.post(entity, key, new BeforeLivingEntityHurtKubeEvent(entity, source, amount)).interruptFalse()) {
				return 0F;
			}
		}
		return amount;
	}

	public static void afterLivingHurt(LivingEntity entity, DamageSource source, float amount) {
		var key = entity.getType().kjs$getKey();

		if (EntityEvents.AFTER_HURT.hasListeners(key)) {
			EntityEvents.AFTER_HURT.post(entity, key, new AfterLivingEntityHurtKubeEvent(entity, source, amount));
		}
	}

	public static void entitySpawned(Entity entity, ServerLevel serverLevel) {
		var key = entity.getType().kjs$getKey();

		if (EntityEvents.SPAWNED.hasListeners(key) && serverLevel instanceof ServerLevel level) {
			EntityEvents.SPAWNED.post(level, key, new EntitySpawnedKubeEvent(entity, level)).compoundResult().result();
		}
	}

	public static boolean livingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, boolean recentlyHit) {
		var key = entity.getType().kjs$getKey();

		if (EntityEvents.ENTITY_DROPS.hasListeners(key)) {
			var e = new LivingEntityDropsKubeEvent(entity, source, drops, recentlyHit);

			if (!EntityEvents.ENTITY_DROPS.post(entity, key, e).compoundResult().isFalse() && e.eventDrops != null) {
				drops.clear();
				drops.addAll(e.eventDrops);
			}
		}
		return recentlyHit;
	}
}