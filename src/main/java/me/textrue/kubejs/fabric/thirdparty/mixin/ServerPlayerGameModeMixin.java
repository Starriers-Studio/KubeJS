package me.textrue.kubejs.fabric.thirdparty.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.textrue.kubejs.fabric.thirdparty.events.FabricBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
	@Shadow
	protected ServerLevel level;

	@Shadow
	@Final
	protected ServerPlayer player;

	@Inject(method = "destroyBlock", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;",
		ordinal = 0),
		cancellable = true)
	private void onBreak(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir, @Local BlockState state) {
		if (FabricBlockEvents.BREAK.invoker().breakBlock(this.level, blockPos, state, this.player, null).isFalse()) {
			cir.setReturnValue(false);
		}
	}
}
