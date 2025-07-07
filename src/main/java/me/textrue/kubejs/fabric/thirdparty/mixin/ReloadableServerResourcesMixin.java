package me.textrue.kubejs.fabric.thirdparty.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.latvian.mods.kubejs.server.KubeJSServerEventHandler;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin {

	@ModifyReturnValue(method = "listeners", at = @At(value = "RETURN"))
	private List<PreparableReloadListener> crafttweaker$attachListener(List<PreparableReloadListener> original) {

		List<PreparableReloadListener> listeners = new ArrayList<>(original);
		listeners.add(KubeJSServerEventHandler.addReloadListeners((ReloadableServerResources) (Object) this));
		// Lets keep it unmodifiable
		return Collections.unmodifiableList(listeners);
	}

}
