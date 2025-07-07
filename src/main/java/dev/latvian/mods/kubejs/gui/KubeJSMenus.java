package dev.latvian.mods.kubejs.gui;

import me.textrue.kubejs.fabric.helper.RegistryHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public interface KubeJSMenus {
	//DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, KubeJS.MOD_ID);

	Supplier<MenuType<KubeJSMenu>> MENU = RegistryHelper.registerMenu("menu", () -> new ExtendedScreenHandlerType<>(KubeJSMenu.FACTORY, KubeJSGUI.STREAM_CODEC));

	static void init() {
		RegistryHelper.MENUS.forEach((id, menu) -> {
			Registry.register(BuiltInRegistries.MENU, id, menu);
		});
	}
}
