package dev.latvian.mods.kubejs.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LivingEntityDropsKubeEvent implements KubeLivingEntityEvent {
	private final LivingEntity entity;
	private final DamageSource source;
	private final Collection<ItemEntity> drops;
	public List<ItemEntity> eventDrops;
	private final boolean recentlyHit;

	public LivingEntityDropsKubeEvent(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, boolean recentlyHit) {
		this.entity = entity;
		this.source = source;
		this.drops = drops;
		this.recentlyHit = recentlyHit;
	}

	@Override
	public LivingEntity getEntity() {
		return entity;
	}

	public DamageSource getSource() {
		return source;
	}

	public boolean isRecentlyHit() {
		return recentlyHit;
	}

	public List<ItemEntity> getDrops() {
		if (eventDrops == null) {
			eventDrops = new ArrayList<>(drops);
		}

		return eventDrops;
	}

	@Nullable
	public ItemEntity addDrop(ItemStack stack) {
		if (!stack.isEmpty()) {
			var e = entity;
			var ei = new ItemEntity(e.level(), e.getX(), e.getY(), e.getZ(), stack);
			ei.setPickUpDelay(10);
			getDrops().add(ei);
			return ei;
		}

		return null;
	}

	@Nullable
	public ItemEntity addDrop(ItemStack stack, float chance) {
		if (chance >= 1F || entity.level().random.nextFloat() <= chance) {
			return addDrop(stack);
		}

		return null;
	}
}