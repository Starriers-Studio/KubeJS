package me.textrue.kubejs.fabric.thirdparty.mixin;

import me.textrue.kubejs.fabric.thirdparty.events.entity.living.LivingDeathEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {LivingEntity.class, Player.class, ServerPlayer.class})
public class LivingDeathInvoker {
	@Inject(method = "die", at = @At("HEAD"), cancellable = true)
	private void die(DamageSource source, CallbackInfo ci) {
		if (LivingDeathEvent.EVENT.invoker().die((LivingEntity) (Object) this, source).isFalse()) {
			ci.cancel();
		}
	}
}
