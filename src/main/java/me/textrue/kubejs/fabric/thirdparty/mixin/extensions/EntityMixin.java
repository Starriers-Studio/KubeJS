package me.textrue.kubejs.fabric.thirdparty.mixin.extensions;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.textrue.kubejs.fabric.thirdparty.extensions.EntityExtension;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(Entity.class)
public class EntityMixin implements EntityExtension {
	@Unique
	private Collection<ItemEntity> kjs$captureDrops = null;

	@WrapWithCondition(
		method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
		)
	)
	public boolean kjs$captureDrops(Level level, Entity entity) {
		if (kjs$captureDrops != null && entity instanceof ItemEntity item) {
			kjs$captureDrops.add(item);
			return false;
		}
		return true;
	}

	@Unique
	@Override
	public Collection<ItemEntity> kjs$captureDrops() {
		return kjs$captureDrops;
	}

	@Unique
	@Override
	public Collection<ItemEntity> kjs$captureDrops(Collection<ItemEntity> value) {
		Collection<ItemEntity> ret = kjs$captureDrops;
		kjs$captureDrops = value;
		return ret;
	}
}
