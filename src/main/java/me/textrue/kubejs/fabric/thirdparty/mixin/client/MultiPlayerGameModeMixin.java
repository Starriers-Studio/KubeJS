package me.textrue.kubejs.fabric.thirdparty.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.kubejs.item.KubeJSItemEventHandler;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
	@Inject(method = "method_41929", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/player/Player;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V",
		shift = At.Shift.AFTER
	))
	private void onPlayerDestroyItem(InteractionHand hand, Player player, MutableObject<InteractionResult> result, int sequence, CallbackInfoReturnable<Packet> cir, @Local(index = 6) ItemStack destroyed, @Local(index = 8) ItemStack item) {
		if (item.isEmpty())
			KubeJSItemEventHandler.itemDestroyed(player, destroyed, hand);
	}
}
