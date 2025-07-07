package dev.latvian.mods.kubejs.core.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.latvian.mods.kubejs.core.ItemEntityKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import me.textrue.kubejs.fabric.helper.ItemEntityHelper;
import me.textrue.kubejs.fabric.helper.ItemStackHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
@RemapPrefixForJS("kjs$")
public abstract class ItemEntityMixin implements ItemEntityKJS {
	@Shadow
	@Final
	public static int LIFETIME;

	@Shadow
	public abstract ItemStack getItem();

	@Unique
	public int lifespan = LIFETIME;

	@Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;DDD)V", at = @At("TAIL"))
	private void kjs$initLifespan(Level level, double posX, double posY, double posZ, ItemStack stack, double deltaX, double deltaY, double deltaZ, CallbackInfo ci) {
		this.lifespan = (stack.getItem() == null ? LIFETIME : ItemStackHelper.getEntityLifespan(stack, level));
		ItemEntityHelper.setLifespan((ItemEntity) (Object) this, lifespan);
	}

	@Inject(method = "<init>(Lnet/minecraft/world/entity/item/ItemEntity;)V", at = @At("TAIL"))
	private void kjs$initLifespanFromOther(ItemEntity other, CallbackInfo ci) {
		this.lifespan = other.kjs$getLifespan();
		ItemEntityHelper.setLifespan((ItemEntity) (Object) this, lifespan);
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "intValue=6000"))
	private int kjs$useForgeLifespan(int original) {
		if (original != LIFETIME) {
			return original;
		}

		ItemEntityHelper.setLifespan((ItemEntity) (Object) this, lifespan);
		return lifespan;
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void kjs$saveLifespanData(CompoundTag compound, CallbackInfo ci) {
		compound.putInt("Lifespan", this.lifespan);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void kjs$loadLifespanData(CompoundTag compound, CallbackInfo ci) {
		if (compound.contains("Lifespan")) {
			this.lifespan = compound.getInt("Lifespan");
		}
	}

	@ModifyExpressionValue(method = "makeFakeItem", at = @At(value = "CONSTANT", args = "intValue=5999"))
	private int kjs$useEntityLifespanIfPossible(int original) {
		var lifespan = ItemStackHelper.getEntityLifespan(this.getItem(), this.kjs$getLevel()) - 1;

		if (lifespan == LIFETIME - 1) {
			return original;
		}

		ItemEntityHelper.setLifespan((ItemEntity) (Object) this, lifespan);
		return lifespan;
	}
}
