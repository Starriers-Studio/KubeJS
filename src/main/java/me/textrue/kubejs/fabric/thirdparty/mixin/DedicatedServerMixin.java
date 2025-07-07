package me.textrue.kubejs.fabric.thirdparty.mixin;

import dev.latvian.mods.kubejs.server.KubeJSServerEventHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
	@Inject(method = "initServer", at = @At("RETURN"), cancellable = true)
	private void serverStarting(CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValueZ()) {
			KubeJSServerEventHandler.serverStarting((MinecraftServer) (Object) this);
		}
	}
}
