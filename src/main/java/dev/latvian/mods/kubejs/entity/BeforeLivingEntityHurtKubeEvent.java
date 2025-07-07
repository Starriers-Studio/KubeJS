package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@Info("""
	Invoked before an entity is hurt by a damage source.
	""")
public class BeforeLivingEntityHurtKubeEvent implements KubeLivingEntityEvent {
	private final LivingEntity livingEntity;
	private final DamageSource damageSource;
	private float damage;

	public BeforeLivingEntityHurtKubeEvent(LivingEntity livingEntity, DamageSource source, float damage) {
		this.livingEntity = livingEntity;
		this.damageSource = source;
		this.damage = damage;
	}

	@Override
	@Info("The entity that was hurt.")
	public LivingEntity getEntity() {
		return livingEntity;
	}

	@Info("The damage source.")
	public DamageSource getSource() {
		return damageSource;
	}

	@Info("The amount of damage.")
	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}
}