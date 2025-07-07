package me.textrue.kubejs.fabric.thirdparty.mixin;

import dev.latvian.mods.kubejs.item.KubeJSItemEventHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FurnaceResultSlot.class)
public class FurnaceResultSlotMixin {
	@Shadow
	@Final
	private Player player;

	@Inject(method = "checkTakeAchievements", at = @At("RETURN"))
	private void checkTakeAchievements(ItemStack itemStack, CallbackInfo ci) {
		KubeJSItemEventHandler.smelted(player, itemStack);
	}
}
