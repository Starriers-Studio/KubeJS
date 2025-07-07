package me.textrue.kubejs.fabric.thirdparty.mixin;

import dev.latvian.mods.kubejs.item.KubeJSItemEventHandler;
import me.textrue.kubejs.fabric.thirdparty.events.entity.living.LivingDamageEvents;
import me.textrue.kubejs.fabric.thirdparty.events.entity.player.PlayerTickEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
	@Inject(method = "tick", at = @At("HEAD"))
	private void preTick(CallbackInfo ci) {
		PlayerTickEvents.PRE.invoker().tick((Player) (Object) this);
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void postTick(CallbackInfo ci) {
		PlayerTickEvents.POST.invoker().tick((Player) (Object) this);
	}

	@Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"), cancellable = true)
	private void drop(ItemStack itemStack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> cir) {
		if (cir.getReturnValue() != null && KubeJSItemEventHandler.itemDrop((Player) (Object) this, cir.getReturnValue()).isFalse()) {
			cir.setReturnValue(null);
		}
	}

	@ModifyVariable(method = "actuallyHurt", at = @At(value = "LOAD", ordinal = 0), ordinal = 0, argsOnly = true)
	private float actuallyHurt(float amount, DamageSource damageSource) {
		float modified = LivingDamageEvents.PRE.invoker().modifyDamage((LivingEntity) (Object) this, damageSource, amount);
		LivingDamageEvents.POST.invoker().damaged((LivingEntity) (Object) this, damageSource, modified);
		return modified;
	}
}
