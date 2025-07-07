package me.textrue.kubejs.fabric.thirdparty.mixin;

import me.textrue.kubejs.fabric.thirdparty.events.FabricBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin {
	@Unique
	private static ThreadLocal<Triple<Long, Float, Entity>> turnToDirtLocal = new ThreadLocal<>();

	@Inject(
		method = "fallOn",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/FarmBlock;turnToDirt(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"
		)
	)
	private void fallOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float f, CallbackInfo ci) {
		turnToDirtLocal.set(Triple.of(blockPos.asLong(), f, entity));
	}

	@Inject(method = "turnToDirt", at = @At("HEAD"), cancellable = true)
	private static void turnToDirt(@Nullable Entity entity, BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
		var triple = turnToDirtLocal.get();
		turnToDirtLocal.remove();
		if (triple != null && triple.getLeft() == pos.asLong() && triple.getRight() == entity) {
			if (FabricBlockEvents.FARMLAND_TRAMPLE.invoker().trample(level, pos, state, triple.getMiddle(), entity).value() != null) {
				ci.cancel();
			}
		}
	}
}
