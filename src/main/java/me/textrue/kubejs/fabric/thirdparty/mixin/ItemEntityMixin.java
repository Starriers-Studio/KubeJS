package me.textrue.kubejs.fabric.thirdparty.mixin;

import me.textrue.kubejs.fabric.thirdparty.events.entity.player.ItemPickupEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Shadow
	public abstract ItemStack getItem();

	@Unique
	private ItemStack cache;

	@Inject(method = "playerTouch",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I"), cancellable = true)
	private void prePickup(Player player, CallbackInfo ci) {
		cache = getItem().copy();
		var canPickUp = ItemPickupEvents.PRE.invoker().canPickup(player, (ItemEntity) (Object) this, getItem());
		if (canPickUp.isFalse()) {
			ci.cancel();
		}
	}

	@Inject(method = "playerTouch",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;take(Lnet/minecraft/world/entity/Entity;I)V"))
	private void pickup(Player player, CallbackInfo ci) {
		if (cache != null) {
			ItemPickupEvents.POST.invoker().pickup(player, (ItemEntity) (Object) this, cache);
		}

		this.cache = null;
	}
}
