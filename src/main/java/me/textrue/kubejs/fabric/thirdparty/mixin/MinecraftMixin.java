package me.textrue.kubejs.fabric.thirdparty.mixin;

import me.textrue.kubejs.fabric.thirdparty.events.AddPackFindersEvent;
import me.textrue.kubejs.fabric.thirdparty.events.FabricScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	@Final
	private PackRepository resourcePackRepository;

	@Unique
	private ThreadLocal<Boolean> setScreenCancelled = new ThreadLocal<>();

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V"))
	private void addClientResources(GameConfig gameConfig, CallbackInfo ci) {
		AddPackFindersEvent.EVENT.invoker().findPacks(new AddPackFindersEvent(PackType.CLIENT_RESOURCES, repositorySource -> {
			resourcePackRepository.kjs$addPackFinder(repositorySource);
		}, false));
	}

	@ModifyVariable(
		method = "setScreen",
		at = @At(value = "FIELD",
			opcode = Opcodes.PUTFIELD,
			target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;",
			shift = At.Shift.BY,
			by = -1),
		argsOnly = true
	)
	public Screen modifyScreen(Screen screen) {
		var old = screen;
		var event = FabricScreenEvents.SET_SCREEN.invoker().modifyScreen(screen);
		if (event.isPresent()) {
			if (event.isFalse()) {
				setScreenCancelled.set(true);
				return old;
			} else {
				screen = event.object();
				if (old != null && screen != old) {
					old.removed();
				}
			}
		}
		setScreenCancelled.set(false);
		return screen;
	}

	@Inject(
		method = "setScreen",
		at = @At(value = "FIELD",
			opcode = Opcodes.PUTFIELD,
			target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;",
			shift = At.Shift.BY,
			by = -1),
		cancellable = true
	)
	public void cancelSetScreen(@Nullable Screen screen, CallbackInfo ci) {
		if (setScreenCancelled.get()) {
			ci.cancel();
			setScreenCancelled.set(false);
		}
	}
}
