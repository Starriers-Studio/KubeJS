package me.textrue.kubejs.fabric.thirdparty.mixin;

import me.textrue.kubejs.fabric.thirdparty.events.CustomizeDebugTextEvent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
	@Inject(method = "getGameInformation", at = @At("RETURN"))
	private void getLeftTexts(CallbackInfoReturnable<List<String>> cir) {
		CustomizeDebugTextEvent.LEFT.invoker().gatherText(cir.getReturnValue());
	}

	@Inject(method = "getSystemInformation", at = @At("RETURN"))
	private void getRightTexts(CallbackInfoReturnable<List<String>> cir) {
		CustomizeDebugTextEvent.RIGHT.invoker().gatherText(cir.getReturnValue());
	}
}
