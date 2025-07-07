package me.textrue.kubejs.fabric.thirdparty.mixin;

import me.textrue.kubejs.fabric.thirdparty.events.FabricBlockEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
	@Inject(method = "place",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z"),
		locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true)
	private void place(BlockPlaceContext _0, CallbackInfoReturnable<InteractionResult> cir, BlockPlaceContext context, BlockState placedState) {
		var result = FabricBlockEvents.PLACE.invoker().placeBlock(context.getLevel(), context.getClickedPos(), placedState, context.getPlayer());
		if (result.isFalse()) {
			cir.setReturnValue(InteractionResult.FAIL);
		}
	}
}
