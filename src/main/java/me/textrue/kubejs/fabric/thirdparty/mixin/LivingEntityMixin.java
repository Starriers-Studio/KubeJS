package me.textrue.kubejs.fabric.thirdparty.mixin;

import me.textrue.kubejs.fabric.thirdparty.events.entity.living.LivingDamageEvents;
import me.textrue.kubejs.fabric.thirdparty.events.entity.living.LivingDropsEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Shadow
	protected int lastHurtByPlayerTime;

	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyVariable(method = "actuallyHurt", at = @At(value = "LOAD", ordinal = 0), ordinal = 0, argsOnly = true)
	private float actuallyHurt(float amount, DamageSource damageSource) {
		float modified = LivingDamageEvents.PRE.invoker().modifyDamage((LivingEntity) (Object) this, damageSource, amount);
		LivingDamageEvents.POST.invoker().damaged((LivingEntity) (Object) this, damageSource, modified);
		return modified;
	}

	@Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
	private void port_lib$startCapturingDrops(ServerLevel serverLevel, DamageSource damageSource, CallbackInfo ci) {
		kjs$captureDrops(new ArrayList<>());
	}

	@Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
	private void dropCapturedDrops(ServerLevel level, DamageSource source, CallbackInfo ci) {
		Collection<ItemEntity> drops = this.kjs$captureDrops(null);
		if (!LivingDropsEvent.EVENT.invoker().onLivingEntityDrops((LivingEntity) (Object) this, source, drops, lastHurtByPlayerTime > 0))
			drops.forEach(e -> level.addFreshEntity(e));
	}
}
