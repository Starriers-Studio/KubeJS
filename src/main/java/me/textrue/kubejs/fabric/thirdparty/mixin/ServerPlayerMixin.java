package me.textrue.kubejs.fabric.thirdparty.mixin;

import me.textrue.kubejs.fabric.thirdparty.events.entity.player.PlayerChangedDimensionEvent;
import me.textrue.kubejs.fabric.thirdparty.events.entity.player.PlayerCloneEvent;
import me.textrue.kubejs.fabric.thirdparty.events.entity.player.PlayerContainerEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
	@Inject(method = "restoreFrom", at = @At("RETURN"))
	private void restoreFrom(ServerPlayer serverPlayer, boolean bl, CallbackInfo ci) {
		PlayerCloneEvent.EVENT.invoker().clone(serverPlayer, (ServerPlayer) (Object) this, bl);
	}

	@Inject(method = "openMenu", at = @At("RETURN"))
	private void openMenu(MenuProvider menuProvider, CallbackInfoReturnable<OptionalInt> cir) {
		if (cir.getReturnValue().isPresent()) {
			PlayerContainerEvents.OPEN.invoker().open((ServerPlayer) (Object) this, ((ServerPlayer) (Object) this).containerMenu);
		}
	}

	@Inject(method = "openHorseInventory", at = @At("RETURN"))
	private void openHorseInventory(AbstractHorse abstractHorse, Container container, CallbackInfo ci) {
		PlayerContainerEvents.OPEN.invoker().open((ServerPlayer) (Object) this, ((ServerPlayer) (Object) this).containerMenu);
	}

	@Inject(method = "doCloseContainer",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;removed(Lnet/minecraft/world/entity/player/Player;)V",
			shift = At.Shift.AFTER))
	private void doCloseContainer(CallbackInfo ci) {
		PlayerContainerEvents.CLOSE.invoker().close((ServerPlayer) (Object) this, ((ServerPlayer) (Object) this).containerMenu);
	}

	@Inject(method = "triggerDimensionChangeTriggers", at = @At("HEAD"))
	private void changeDimension(ServerLevel serverLevel, CallbackInfo ci) {
		PlayerChangedDimensionEvent.EVENT.invoker().change((ServerPlayer) (Object) this, serverLevel.dimension(), ((ServerPlayer) (Object) this).level().dimension());
	}
}
